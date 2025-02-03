package ru.alemakave.xuitelegrambot.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class TelegramClient {
    private final long tgChatId;
    private final TelegramClientRole role;
    @Setter
    private TelegramClientMode mode = TelegramClientMode.NONE;

    public TelegramClient(long tgChatId, TelegramClientRole role) {
        this.tgChatId = tgChatId;
        this.role = role;
    }

    public enum TelegramClientRole {
        USER,
        ADMIN
    }

    public enum TelegramClientMode {
        NONE,
        ENTER_CONNECTION_NAME
    }
}
