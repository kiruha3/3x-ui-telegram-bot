package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.model.*;
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
            ConnectionSettings connectionSettings = connection.getSettings();
            if (connectionSettings != null) {
                List<Client> clients = connectionSettings.getClients();
                for (int i = 0; i < clients.size(); i++) {
                    Client client = clients.get(i);

                    GenerateClientConnectionQRInlineButton generateClientConnectionQR = new GenerateClientConnectionQRInlineButton(telegramBot);
                    generateClientConnectionQR.addCallbackArgs(connection.getId(), client.getId());
                    generateClientConnectionQR.setButtonText("Сгенерировать QR код для клиента " + (i + 1));

                    keyboardMarkup.addRow(generateClientConnectionQR.getButton());
                }
            }

            AddClientInlineButton addClientButton = new AddClientInlineButton(telegramBot);
            addClientButton.addCallbackArg(connection.getId());
            keyboardMarkup.addRow(addClientButton.getButton());

            DeleteConnectionInlineButton deleteButton = new DeleteConnectionInlineButton(telegramBot);
            deleteButton.addCallbackArg(connection.getId());
            keyboardMarkup.addRow(deleteButton.getButton());

            ListConnectionsInlineButton backButton = new ListConnectionsInlineButton(telegramBot);
            backButton.setButtonText("Назад");
            keyboardMarkup.addRow(backButton.getButton());

            int messageId = maybeInaccessibleMessage.messageId();

            if (((Message)maybeInaccessibleMessage).photo() == null) {
                EditMessageText editMessageText = new EditMessageText(chatId, messageId, generateExtensionConnectionInfo(connection));
                editMessageText.replyMarkup(keyboardMarkup);

                telegramBot.execute(editMessageText);
            } else {
                SendMessage message = new SendMessage(chatId, generateExtensionConnectionInfo(connection));
                message.replyMarkup(keyboardMarkup);
                telegramBot.execute(message);

                DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
                telegramBot.execute(deleteMessage);
            }
        } catch (NumberFormatException e) {
            SendMessage sendMessage = new SendMessage(chatId, String.format("Не удалось преобразовать ID (\"%s\") в число", receivedMessageParts[1]));
            telegramBot.execute(sendMessage);
        }
    }

    //TODO: Добавить функцию генерации сокращенной информации подключения
    private String generateExtensionConnectionInfo(Connection connection) {
        StringBuilder msg = new StringBuilder();
        msg.append("Id: ").append(connection.getId());
        msg.append("\nUp: ").append(connection.getUp());
        msg.append("\nDown: ").append(connection.getDown());
        msg.append("\nTotal: ").append(connection.getTotal());
        msg.append("\nRemark: ").append(connection.getRemark());
        msg.append("\nEnabled: ").append(connection.isEnable());
        msg.append("\nExpiryTime: ").append(connection.getExpiryTime());
        msg.append("\nClient Stats: ").append(connection.getClientStats());
        msg.append("\nListen: ").append(connection.getListen());
        msg.append("\nPort: ").append(connection.getPort());
        msg.append("\nProtocol: ").append(connection.getProtocol());
        msg.append("\nSettings: ");
        if (connection.getSettings() != null) {
            msg.append("\n|    Clients: ");
            List<Client> clients = connection.getSettings().getClients();
            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);
                msg.append("\n|    |    ").append(i + 1).append(": ");
                msg.append("\n|    |    |    Id: ").append(client.getId());
                msg.append("\n|    |    |    Flow: ").append(client.getFlow());
                msg.append("\n|    |    |    Email: ").append(client.getEmail());
                msg.append("\n|    |    |    Limit Ip: ").append(client.getLimitIp());
                msg.append("\n|    |    |    Total GB: ").append(client.getTotalGB());
                msg.append("\n|    |    |    Expiry Time: ").append(client.getExpiryTime());
                msg.append("\n|    |    |    Enabled: ").append(client.isEnable());
                msg.append("\n|    |    |    Telegram Id: ").append(client.getTgId());
                msg.append("\n|    |    |    Sub Id: ").append(client.getSubId());
                msg.append("\n|    |    |    Reset: ").append(client.getReset());
            }
            msg.append("\n|    Decryption: ").append(connection.getSettings().getDecryption());
            msg.append("\n|    Fallbacks: ").append(connection.getSettings().getFallbacks());
        }
        msg.append("\nStream Settings: ");
        ConnectionStreamSettings streamSettings = connection.getStreamSettings();
        if (streamSettings != null) {
            msg.append("\n|    Network: ").append(streamSettings.getNetwork());
            msg.append("\n|    Security: ").append(streamSettings.getSecurity());
            msg.append("\n|    External Proxy: ").append(streamSettings.getExternalProxy());
            msg.append("\n|    Reality Settings: ");
            RealitySettings realitySettings = streamSettings.getRealitySettings();
            msg.append("\n|    |    Show: ").append(realitySettings.isShow());
            msg.append("\n|    |    Xver: ").append(realitySettings.getXver());
            msg.append("\n|    |    Dest: ").append(realitySettings.getDest());
            msg.append("\n|    |    Server Names: ").append(realitySettings.getServerNames());
            msg.append("\n|    |    Private Key: ").append(realitySettings.getPrivateKey());
            msg.append("\n|    |    Min Client: ").append(realitySettings.getMinClient());
            msg.append("\n|    |    Max Client: ").append(realitySettings.getMaxClient());
            msg.append("\n|    |    Max Timediff: ").append(realitySettings.getMaxTimediff());
            msg.append("\n|    |    Short Ids: ").append(realitySettings.getShortIds());
            msg.append("\n|    |    Settings: ");
            RealityConnectionSettings realityConnectionSettings = realitySettings.getSettings();
            msg.append("\n|    |    |    Public Key: ").append(realityConnectionSettings.getPublicKey());
            msg.append("\n|    |    |    Fingerprint: ").append(realityConnectionSettings.getFingerprint());
            msg.append("\n|    |    |    Server Name: ").append(realityConnectionSettings.getServerName());
            msg.append("\n|    |    |    SpiderX: ").append(realityConnectionSettings.getSpiderX());
            msg.append("\n|    Tcp Settings: ");
            TcpSettings tcpSettings = streamSettings.getTcpSettings();
            msg.append("\n|    |    |    Accept Proxy Protocol: ").append(tcpSettings.isAcceptProxyProtocol());
            msg.append("\n|    |    |    Header: ");
            msg.append("\n|    |    |    |    Type: ").append(tcpSettings.getHeader().getType());
        }
        msg.append("\nTag: ").append(connection.getTag());
        msg.append("\nSniffing: ").append(connection.getSniffing());
        msg.append("\nAllocate: ").append(connection.getAllocate());

        return msg.toString();
    }
}
