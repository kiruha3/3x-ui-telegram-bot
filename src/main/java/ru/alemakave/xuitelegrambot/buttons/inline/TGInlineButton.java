package ru.alemakave.xuitelegrambot.buttons.inline;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import lombok.Getter;
import lombok.Setter;

public abstract class TGInlineButton {
    protected final TelegramBot telegramBot;
    @Getter
    @Setter
    private String buttonText;
    @Getter
    @Setter
    private String callbackData;

    public TGInlineButton(TelegramBot telegramBot, String buttonText, String callbackData) {
        this.telegramBot = telegramBot;
        this.buttonText = buttonText;
        this.callbackData = callbackData;
    }

    public InlineKeyboardButton getButton() {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        inlineKeyboardButton.setCallbackData(callbackData);
        inlineKeyboardButton.setText(buttonText);

        return inlineKeyboardButton;
    }

    public abstract void action(Update update);
}
