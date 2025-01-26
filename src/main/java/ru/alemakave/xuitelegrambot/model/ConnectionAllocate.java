package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class ConnectionAllocate {
    private String strategy;
    private int refresh;
    private int concurrency;
}
