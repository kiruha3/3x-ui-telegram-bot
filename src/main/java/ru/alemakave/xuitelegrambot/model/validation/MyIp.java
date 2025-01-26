package ru.alemakave.xuitelegrambot.model.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class MyIp {
    private String ip;
    private String country;
    private String cc;
}
