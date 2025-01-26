package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;

@TGCommandAnnotation
public class StartCommand extends TGCommand {
    public StartCommand(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void action(Update update) {
        long chatId = update.message().chat().id();

        SendMessage message = new SendMessage(chatId, "Введите код пользователя");
        telegramBot.execute(message);
    }
}
