package ru.alemakave.xuitelegrambot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ConnectionSniffing {
    private boolean enabled;
    private List<String> destOverride;
    private boolean metadataOnly;
    private boolean routeOnly;
}
