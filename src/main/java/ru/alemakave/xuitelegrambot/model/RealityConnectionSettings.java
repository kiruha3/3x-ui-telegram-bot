package ru.alemakave.xuitelegrambot.model;

import lombok.Data;

@Data
public class RealityConnectionSettings {
    private String publicKey;
    private String fingerprint;
    private String serverName;
    private String spiderX;
}
