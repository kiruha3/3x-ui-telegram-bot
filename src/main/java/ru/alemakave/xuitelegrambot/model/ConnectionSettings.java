package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;

@Data
@EqualsAndHashCode
@ToString
public class ConnectionSettings {
    private ArrayList<Client> clients;
    private String decryption;
    private Object fallbacks;
}
