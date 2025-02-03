package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import ru.alemakave.xuitelegrambot.actions.ListAction;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGCommandAnnotation
public class ListConnectionsCommand extends TGCommand {
    public ThreeXConnection threeXConnection;

    public ListConnectionsCommand(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public void action(Update update) {
        long chatId = update.message().chat().id();

        ListAction.action(telegramBot, threeXConnection, chatId, -1);
    }
}
