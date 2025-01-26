package ru.alemakave.xuitelegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.exception.UnauthorizedException;
import ru.alemakave.xuitelegrambot.functions.UnauthorizedThrowingFunction;
import ru.alemakave.xuitelegrambot.mapper.ConnectionMapper;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.model.messages.ConnectionMessage;
import ru.alemakave.xuitelegrambot.model.messages.ConnectionsMessage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreeXConnectionImpl implements ThreeXConnection {
    @Autowired
    private CookedWebClient webClient;
    @Autowired
    private ConnectionMapper connectionMapper;
    @Autowired
    private ThreeXAuth threeXAuth;

    @Override
    public List<Connection> list() {
        if (!threeXAuth.isAuthorized()) {
            threeXAuth.login();
        }

        WebClient.ResponseSpec responseSpec = webClient
                .get("/panel/api/inbounds/list")
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ConnectionsMessage connectionsMessage = responseSpec
                .bodyToMono(ConnectionsMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (connectionsMessage == null) {
            throw new RuntimeException("Connection failed!");
        }

        return connectionsMessage.getObj().stream()
                .map(connectionGetUpdateDTO -> {
                    try {
                        return connectionMapper.connectionGetUpdateDtoToConnection(connectionGetUpdateDTO);
                    } catch (JsonProcessingException e) {
                        log.error("Throw exception for user: " + connectionGetUpdateDTO.getRemark());
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public Connection get(int inboundId) {
        if (!threeXAuth.isAuthorized()) {
            threeXAuth.login();
        }

        WebClient.ResponseSpec responseSpec = webClient
                .get("/panel/api/inbounds/get/" + inboundId)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ConnectionMessage connectionMessage = responseSpec
                .bodyToMono(ConnectionMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (connectionMessage == null) {
            throw new RuntimeException("Connection failed!");
        }

        return connectionMapper.connectionGetUpdateDtoToConnection(connectionMessage.getObj());
    }

    @Override
    public void add() {
        if (!threeXAuth.isAuthorized()) {
            threeXAuth.login();
        }


    }

    @Override
    public void delete(int inboundId) {
        if (!threeXAuth.isAuthorized()) {
            threeXAuth.login();
        }


    }

    @SneakyThrows
    @Override
    public void update(int inboundId, Connection connection) {
        if (!threeXAuth.isAuthorized()) {
            threeXAuth.login();
        }

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/update/" + inboundId)
                .bodyValue(connectionMapper.connectionToConnectionGetUpdateDto(connection))
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ConnectionMessage receivedConnectionMessage = responseSpec
                .bodyToMono(ConnectionMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        System.out.println(receivedConnectionMessage);
    }

    @Override
    public void onlines() {
        if (!threeXAuth.isAuthorized()) {
            threeXAuth.login();
        }


    }
}
