package ru.alemakave.xuitelegrambot.commands.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alemakave.xuitelegrambot.annotations.TGCommandAnnotation;
import ru.alemakave.xuitelegrambot.buttons.inline.GetConnectionInlineButton;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.List;

@TGCommandAnnotation
public class ListCommand extends TGCommand {
    public ThreeXConnection threeXConnection;

    public ListCommand(TelegramBot telegramBot) {
        super(telegramBot);
    }

    @Override
    public String getCommand() {
        return "/list";
    }

    @Override
    public void action(Update update) {
        long chatId = update.message().chat().id();
        String receivedMessage = update.message().text();

        StringBuilder message = new StringBuilder();

        List<Connection> connections = threeXConnection.list();
        InlineKeyboardButton[][] buttons = new InlineKeyboardButton[(int)Math.ceil((double)connections.size() / 2)][2];

        int j = 0;
        for (int i = 0; i < connections.size(); i++) {
            if (i > 0 && i % 2 == 0) {
                j++;
            }

            Connection connection = connections.get(i);

            message.append("Id: ").append(connection.getId()).append("\n");
            message.append("Имя: ").append(connection.getRemark()).append("\n\n");

            GetConnectionInlineButton getConnectionInlineButton = new GetConnectionInlineButton(telegramBot);
            getConnectionInlineButton.setButtonText(connection.getRemark());
            getConnectionInlineButton.setCallbackData(getConnectionInlineButton.getCallbackData() + " " + connection.getId());

            buttons[j][i % 2] = getConnectionInlineButton.getButton();
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(buttons);

        SendMessage sendMessage = new SendMessage(chatId, message.toString());
        sendMessage.replyMarkup(keyboardMarkup);
        telegramBot.execute(sendMessage);
    }
}
