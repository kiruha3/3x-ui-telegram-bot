package ru.alemakave.xuitelegrambot.exception;

public class InvalidCountryException extends RuntimeException {
    public InvalidCountryException() {
        super("Invalid country!");
    }

    public InvalidCountryException(String message) {
        super(message);
    }
}
