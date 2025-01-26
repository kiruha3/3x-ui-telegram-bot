package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class TcpSettings {
    private boolean acceptProxyProtocol;
    private Header header;

    @Data
    @EqualsAndHashCode
    @ToString
    public static class Header {
        private String type;
    }
}
