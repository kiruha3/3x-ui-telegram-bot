package ru.alemakave.xuitelegrambot.exception;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message) {
        super(message);
    }
}
