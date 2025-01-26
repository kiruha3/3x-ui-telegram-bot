package ru.alemakave.xuitelegrambot.exception;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(Map<String, String> cookie) {
        super("Unauthorized!" + cookie);
    }
}
