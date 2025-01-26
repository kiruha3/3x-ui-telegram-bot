package ru.alemakave.xuitelegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.alemakave.xuitelegrambot.exception.UnsetException;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.ArrayList;
import java.util.List;

@Configuration
@PropertySource(value = {"file:./application.propertes", "file:./application.yml"}, ignoreResourceNotFound = true)
public class TelegramBotConfiguration {
    @Value("${telegram.bot.token:}")
    private String token;

    @Autowired
    private ThreeXConnection threeXConnection;

    @Bean
    public TelegramBot telegramBot() {
        if (token == null || token.isEmpty()) {
            throw new UnsetException("TelegramBot token unset!");
        }

        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

    @Bean
    public List<Long> adminChatIds() {
        List<Long> chatIds = new ArrayList<>();
        List<Connection> connections = threeXConnection.list();

        connections.forEach(connection -> {
            String chatId = connection.getSettings().getClients().get(0).getTgId();

            if (chatId == null || chatId.isEmpty()) {
                return;
            }

            if (chatId.startsWith("Admin:")) {
                chatIds.add(Long.valueOf(chatId.substring("Admin:".length())));
            }
        });

        return chatIds;
    }
}
