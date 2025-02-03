package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

@Data
public class Certificate {
    private String privateKey;
    private String publicKey;
}
