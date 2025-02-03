package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.model.messages.DeleteConnectionMessage;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

@TGInlineButtonAnnotation
public class DeleteConnectionInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public DeleteConnectionInlineButton(TelegramBot telegramBot) {
        super(telegramBot, "Удалить подключение", "/delete_connection");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        long chatId = message.chat().id();
        String receivedMessage = callbackQuery.data();
        String[] receivedMessageParts = receivedMessage.split(" ");

        if (receivedMessageParts.length < 2) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось обнаружить ID подключения!");
            telegramBot.execute(sendMessage);
            return;
        }

        try {
            int connectionId = Integer.parseInt(receivedMessageParts[1]);
            DeleteConnectionMessage deleteMessage = (DeleteConnectionMessage)threeXConnection.delete(connectionId);

            String messageText = "Подключение с ID=" + connectionId + " " + (deleteMessage.isSuccess() ? "удалено." : "не удалось удалить.");

            EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), messageText);
            ListConnectionsInlineButton backButton = new ListConnectionsInlineButton(telegramBot);
            backButton.setButtonText("Назад");
            editMessage.replyMarkup(new InlineKeyboardMarkup(backButton.getButton()));

            telegramBot.execute(editMessage);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, "Не удалось преобразовать ID подключения в число!");
            telegramBot.execute(sendMessage);
        }
    }
}
