package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode
@ToString
public class RealitySettings {
    private boolean show;
    private int xver;
    private String dest;
    private List<String> serverNames;
    private String privateKey;
    private String minClient;
    private String maxClient;
    private int maxTimediff;
    private List<String> shortIds;
    private Map<String, String> settings;
}
