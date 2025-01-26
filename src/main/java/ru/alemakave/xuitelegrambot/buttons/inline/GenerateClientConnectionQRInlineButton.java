package ru.alemakave.xuitelegrambot.buttons.inline;

import com.google.zxing.WriterException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.xuitelegrambot.annotations.TGInlineButtonAnnotation;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;
import ru.alemakave.xuitelegrambot.utils.ImageUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@TGInlineButtonAnnotation
public class GenerateClientConnectionQRInlineButton extends TGInlineButton {
    public ThreeXConnection threeXConnection;

    public GenerateClientConnectionQRInlineButton(TelegramBot telegramBot) {
        super(telegramBot, "Сгенерировать QR код для клиента ", "/gen_qr");
    }

    @Override
    public void action(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        long chatId = message.chat().id();
        String receivedMessage = callbackQuery.data();

        String[] receivedMessageParts = receivedMessage.split(" ");
        if (receivedMessageParts.length < 3) {
            if (receivedMessageParts.length < 2) {
                SendMessage sendMessage = new SendMessage(chatId, "При запросе не был указан ID подключения и UUID клиента!");
                telegramBot.execute(sendMessage);
                return;
            }
            return;
        }

        int connectionId = Integer.parseInt(receivedMessageParts[1]);
        String clientUUID = receivedMessageParts[2];

        Connection connection = threeXConnection.get(connectionId);
        Client client = connection.getSettings().getClients()
                .stream()
                .filter(client1 -> client1.getId().equals(clientUUID))
                .findFirst()
                .orElse(null);

        if (client == null) {
            SendMessage sendMessage = new SendMessage(chatId, "Клиент с данным UUID не найден!");
            telegramBot.execute(sendMessage);
            return;
        }

        String msg = connection.getProtocol() + "://" + client.getId() +
                "@" + connection.getListen() + ":" + connection.getPort() +
                "?type=" + connection.getStreamSettings().getNetwork() +
                "&security=" + connection.getStreamSettings().getSecurity() +
                "&pbk=" + connection.getStreamSettings().getRealitySettings().getSettings().get("publicKey") +
                "&fp=" + connection.getStreamSettings().getRealitySettings().getSettings().get("fingerprint") +
                "&sni=" + connection.getStreamSettings().getRealitySettings().getDest().split(":")[0] +
                "&sid=" + connection.getStreamSettings().getRealitySettings().getShortIds().get(0) +
                "&spx=" + URLEncoder.encode("/", StandardCharsets.UTF_8) +
                "#" + URLEncoder.encode(connection.getRemark(), StandardCharsets.UTF_8) +
                "-" + client.getEmail();

        try {
            SendPhoto photo = new SendPhoto(chatId, ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(msg, 300, 300), ImageType.PNG));
            photo.parseMode(ParseMode.MarkdownV2);
            photo.caption("```" + msg + "```");
            telegramBot.execute(photo);

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.messageId());
            telegramBot.execute(deleteMessage);
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
