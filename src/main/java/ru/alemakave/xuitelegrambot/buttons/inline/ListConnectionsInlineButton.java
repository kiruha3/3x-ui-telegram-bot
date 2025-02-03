package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import ru.alemakave.xuitelegrambot.actions.ListAction;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGInlineButtonAnnotation
public class ListConnectionsInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public ListConnectionsInlineButton(TelegramBot telegramBot) {
        super(telegramBot, "Список подключений", "/list");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();

        ListAction.action(telegramBot, threeXConnection, chatId, maybeInaccessibleMessage.messageId());
    }
}
