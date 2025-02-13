package ru.alemakave.xuitelegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.alemakave.xuitelegrambot.client.TelegramClient;
import ru.alemakave.xuitelegrambot.exception.UnsetException;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.model.ConnectionSettings;
import ru.alemakave.xuitelegrambot.service.ThreeXConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@PropertySource(value = {"file:./application.yml"}, ignoreResourceNotFound = true)
public class TelegramBotConfiguration {
    @Value("${telegram.bot.token:}")
    private String token;

    @Autowired
    private ThreeXConnection threeXConnection;

    @Bean
    public TelegramBot telegramBot() {
        if (token == null || token.isEmpty()) {
            throw new UnsetException("TelegramBot token unset!");
        }

        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

    @Bean
    public Map<Long, TelegramClient> adminChatIds() {
        Map<Long, TelegramClient> telegramClients = new HashMap<>();
        List<Connection> connections = threeXConnection.list();

        connections.forEach(connection -> {
            ConnectionSettings connectionSettings = connection.getSettings();
            if (connectionSettings == null) {
                return;
            }

            List<Client> clients = connectionSettings.getClients();
            if (clients == null || clients.isEmpty()) {
                return;
            }

            String chatIdStr = clients.get(0).getTgId();

            if (chatIdStr == null || chatIdStr.isEmpty()) {
                return;
            }

            if (chatIdStr.startsWith("Admin:")) {
                long chatId = Long.parseLong(chatIdStr.substring("Admin:".length()));

                TelegramClient telegramClient = new TelegramClient(chatId, TelegramClient.TelegramClientRole.ADMIN);
                telegramClients.put(chatId, telegramClient);
            }
        });

        return telegramClients;
    }
}
