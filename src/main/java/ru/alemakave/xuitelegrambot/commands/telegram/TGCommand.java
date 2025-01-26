package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public abstract class TGCommand {
    protected final TelegramBot telegramBot;

    public TGCommand(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public abstract String getCommand();
    public abstract void action(Update update);
}
