package ru.alemakave.xuitelegrambot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.alemakave.xuitelegrambot.model.Client;

import java.util.List;

@Data
@AllArgsConstructor
public class ClientAddDtoSettings {
    private List<Client> clients;
}
