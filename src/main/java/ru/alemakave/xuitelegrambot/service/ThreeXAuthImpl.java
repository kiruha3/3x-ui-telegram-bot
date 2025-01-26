package ru.alemakave.xuitelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreeXAuthImpl implements ThreeXAuth {
    @Autowired
    private CookedWebClient webClient;
    @Autowired
    private BodyInserters.FormInserter<String> authBody;

    @Override
    public void login() {
        ResponseEntity<String> response = webClient
                .post("/login")
                .body(authBody)
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null) {
            log.error("Connection failed!");
            return;
        }

        HttpHeaders headers = response.getHeaders();
        if (!headers.containsKey(HttpHeaders.SET_COOKIE)) {
            log.error("Login failed!");
            return;
        }

        List<String> cookie = headers.get(HttpHeaders.SET_COOKIE);
        if (cookie == null) {
            log.error("Login failed!");
            return;
        }

        for (String value : cookie) {
            Map<String, String> cookieParams = Arrays.stream(value.split(";"))
                    .collect(Collectors.toMap(
                            s -> s.contains("=") ? s.split("=", 2)[0].strip() : s.strip(),
                            s -> s.contains("=") ? s.split("=", 2)[1].strip() : ""
                    ));

            webClient.putCookie("3x-ui", cookieParams.get("3x-ui"));
        }

        log.debug("Login response: {}", response.getBody());
    }

    @Override
    public boolean isAuthorized() {
        if (webClient.getCookies().get("3x-ui") == null) {
            return false;
        }

        ResponseEntity<String> response = webClient.get("/panel").retrieve().toEntity(String.class).block();
        if (response == null) {
            return false;
        }

        if (response.getStatusCode().is3xxRedirection()) {
            return response.getHeaders().get(HttpHeaders.LOCATION).get(0).equals("/" + System.getenv("threex.panel.path") + "/panel/");
        }

        return false;
    }
}
