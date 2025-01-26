package ru.alemakave.xuitelegrambot.exception;

public class InvalidCountry extends RuntimeException {
    public InvalidCountry() {
        super("Invalid country!");
    }

    public InvalidCountry(String message) {
        super(message);
    }
}
