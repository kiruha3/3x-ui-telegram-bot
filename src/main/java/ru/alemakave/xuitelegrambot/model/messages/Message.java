package ru.alemakave.xuitelegrambot.model.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode()
@ToString
public class Message<T> {
    private boolean success;
    private String msg;
    private T obj;
}
