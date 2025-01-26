package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.List;

@TGInlineButtonAnnotation
public class GetConnectionInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public GetConnectionInlineButton(TelegramBot telegramBot) {
        super(telegramBot, "", "/get");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage maybeInaccessibleMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = maybeInaccessibleMessage.chat().id();
        String receivedMessage = callbackQuery.data();

        String[] receivedMessageParts = receivedMessage.split(" ");

        if (receivedMessageParts.length < 2) {
            SendMessage sendMessage = new SendMessage(chatId, "При запросе не был указан ID подключения!");
            telegramBot.execute(sendMessage);
            return;
        }

        try {
            int connectionId = Integer.parseInt(receivedMessageParts[1]);

            Connection connection = threeXConnection.get(connectionId);
            if (connection == null) {
                SendMessage sendMessage = new SendMessage(chatId, "Подключение с ID=" + connectionId + " не найдено!");
                telegramBot.execute(sendMessage);
                return;
            }

            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            List<Client> clients = connection.getSettings().getClients();
            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);

                GenerateClientConnectionQRInlineButton generateClientConnectionQR = new GenerateClientConnectionQRInlineButton(telegramBot);
                generateClientConnectionQR.setCallbackData("/gen_qr " + connection.getId() + " " + client.getId());
                generateClientConnectionQR.setButtonText("Сгенерировать QR код для клиента " + (i + 1));

                keyboardMarkup.addRow(generateClientConnectionQR.getButton());
            }

            int messageId = maybeInaccessibleMessage.messageId();

            EditMessageText editMessageText = new EditMessageText(chatId, messageId, generateMessage(connection));
            editMessageText.replyMarkup(keyboardMarkup);

            telegramBot.execute(editMessageText);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, String.format("Не удалось преобразовать ID (\"%s\") в число", receivedMessageParts[1]));
            telegramBot.execute(sendMessage);
        }
    }

    private String generateMessage(Connection connection) {
        StringBuilder msg = new StringBuilder();
        msg.append("id: ").append(connection.getId());
        msg.append("\nup: ").append(connection.getUp());
        msg.append("\ndown: ").append(connection.getDown());
        msg.append("\ntotal: ").append(connection.getTotal());
        msg.append("\nremark: ").append(connection.getRemark());
        msg.append("\nenable: ").append(connection.isEnable());
        msg.append("\nexpiryTime: ").append(connection.getExpiryTime());
        msg.append("\nclientStats: ").append(connection.getClientStats());
        msg.append("\nlisten: ").append(connection.getListen());
        msg.append("\nport: ").append(connection.getPort());
        msg.append("\nprotocol: ").append(connection.getProtocol());
        msg.append("\nsettings: ");
        msg.append("\n    Clients: ");
        for (Client client : connection.getSettings().getClients()) {
            msg.append("\n        ").append(client);
        }
        msg.append("\n    Decryption: ").append(connection.getSettings().getDecryption());
        msg.append("\n    Fallbacks: ").append(connection.getSettings().getFallbacks());
        msg.append("\nstreamSettings: ").append(connection.getStreamSettings());
        msg.append("\ntag: ").append(connection.getTag());
        msg.append("\nsniffing: ").append(connection.getSniffing());
        msg.append("\nallocate: ").append(connection.getAllocate());

        return msg.toString();
    }
}
