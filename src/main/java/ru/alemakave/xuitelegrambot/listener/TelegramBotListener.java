package ru.alemakave.xuitelegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.buttons.inline.TGInlineButton;
import ru.alemakave.xuitelegrambot.commands.telegram.TGCommand;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TelegramBotListener implements UpdatesListener {
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private ThreeXConnection threeXConnection;
    @Autowired
    private List<Long> adminChatIds;

    private final Map<String, TGCommand> commands = new HashMap<>();
    private final Map<String, TGInlineButton> buttons = new HashMap<>();

    @Override
    public int process(List<Update> listUpdates) {
        listUpdates.forEach(update -> {
            if (update.message() != null) {
                processMessage(update);
            } else if (update.callbackQuery() != null) {
                processCallbackQuery(update);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processMessage(Update update) {
        long chatId = update.message().chat().id();
        String receivedMessage = update.message().text();

        if (receivedMessage != null) {
            if (receivedMessage.equals("531254")) {
                Connection connection = threeXConnection.get(1);
                connection.getSettings().getClients().get(0).setTgId("Admin:" + chatId);
                threeXConnection.update(1, connection);

                adminChatIds.add(chatId);

                SendMessage message = new SendMessage(chatId, "Бот подключен");
                telegramBot.execute(message);
                return;
            }

            if (receivedMessage.equals("685479")) {
                Connection connection = threeXConnection.get(3);
                connection.getSettings().getClients().get(0).setTgId("Admin:" + chatId);
                threeXConnection.update(3, connection);

                adminChatIds.add(chatId);

                SendMessage message = new SendMessage(chatId, "Бот подключен");
                telegramBot.execute(message);
                return;
            }

            if (!adminChatIds.contains(chatId)) {
                SendMessage message = new SendMessage(chatId, "Недостаточно прав!");
                telegramBot.execute(message);

                return;
            }

            boolean hasInCache = commands.containsKey(receivedMessage.split(" ")[0]);
            if (!hasInCache) {
                SendMessage sendMessage = new SendMessage(chatId, String.format("Команда \"%s\" не распознана", receivedMessage));
                telegramBot.execute(sendMessage);
                return;
            }

            commands.get(receivedMessage.split(" ")[0]).action(update);
        }
    }

    private void processCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String callbackText = callbackQuery.data();

        boolean hasInCache = buttons.containsKey(callbackText.split(" ")[0]);
        if (hasInCache) {
            buttons.get(callbackText.split(" ")[0]).action(update);
        }

        AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.id());
        telegramBot.execute(answer);
    }

    private void registerCommands() {
        Reflections reflections = new Reflections("");
        Map<String, Object> thisClassFieldsByType = getThisClassDeclaredFields();

        reflections.getTypesAnnotatedWith(TGCommandAnnotation.class).forEach(clazz -> {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Registering command class: {}", clazz.getSimpleName());
                    log.debug("    Class fields: ");
                    Arrays.stream(clazz.getFields()).forEach(field -> {
                        log.debug("        {}", field);
                    });
                    log.debug("    Class declared fields: ");
                    Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                        log.debug("        {}", field);
                    });
                }

                Constructor<?> constructor = clazz.getDeclaredConstructor(TelegramBot.class);
                List<Field> declaredFields = Arrays.stream(clazz.getDeclaredFields()).toList();

                TGCommand command = (TGCommand) constructor.newInstance(telegramBot);

                if (!declaredFields.isEmpty()) {
                    declaredFields.forEach(field -> {
                        if (thisClassFieldsByType.containsKey(field.getType().getTypeName())) {
                            try {
                                log.debug("\tSign field {} => {}", thisClassFieldsByType.get(field.getType().getTypeName()), field.getName());
                                field.set(command, thisClassFieldsByType.get(field.getType().getTypeName()));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                commands.put(command.getCommand(), command);
                log.info("Registered command: {}", command.getCommand());
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void registerButtons() {
        Reflections reflections = new Reflections("");
        Map<String, Object> thisClassFieldsByType = getThisClassDeclaredFields();

        reflections.getTypesAnnotatedWith(TGInlineButtonAnnotation.class).forEach(clazz -> {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Registering button class: {}", clazz.getSimpleName());
                    log.debug("    Class fields: ");
                    Arrays.stream(clazz.getFields()).forEach(field -> {
                        log.debug("        {}", field);
                    });
                    log.debug("    Class declared fields: ");
                    Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                        log.debug("        {}", field);
                    });
                }

                Constructor<?> constructor = clazz.getDeclaredConstructor(TelegramBot.class);
                List<Field> declaredFields = Arrays.stream(clazz.getDeclaredFields()).toList();

                TGInlineButton button = (TGInlineButton) constructor.newInstance(telegramBot);

                if (!declaredFields.isEmpty()) {
                    declaredFields.forEach(field -> {
                        if (thisClassFieldsByType.containsKey(field.getType().getTypeName())) {
                            try {
                                log.debug("\tSign field {} => {}", thisClassFieldsByType.get(field.getType().getTypeName()), field.getName());
                                field.set(button, thisClassFieldsByType.get(field.getType().getTypeName()));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                buttons.put(button.getCallbackData(), button);
                log.info("Registered button: {}", button.getCallbackData());
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @return Map<String, Object>, где:
     *    Ключ - путь к классу типа переменной.
     *    Значение - значение переменной.
     */
    private Map<String, Object> getThisClassDeclaredFields() {
        return Arrays.stream(TelegramBotListener.this.getClass().getDeclaredFields())
                        .filter(field -> field.getType().getTypeName().startsWith("ru"))
                        .collect(
                                Collectors.toMap(
                                        field1 -> field1.getType().getTypeName(),
                                        field2 -> {
                                            try {
                                                return field2.get(TelegramBotListener.this);
                                            } catch (IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                        );
    }

    @PostConstruct
    private void postConstruct() {
        registerCommands();
        registerButtons();
        telegramBot.setUpdatesListener(this);
        log.info("TelegramBotListener initialized");
    }
}
