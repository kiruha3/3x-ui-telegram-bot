package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ConnectionStreamSettings {
    private String network;
    private String security;
    private List<Object> externalProxy;
    private RealitySettings realitySettings;
    private TcpSettings tcpSettings;
}
