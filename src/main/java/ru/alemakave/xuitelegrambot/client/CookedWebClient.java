package ru.alemakave.xuitelegrambot.client;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;

import java.util.*;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Scope(SCOPE_SINGLETON)
public class CookedWebClient {
    private final WebClient webClient;
    @Getter
    private final Map<String, String> cookies = new HashMap<>();

    public CookedWebClient() {
        this(WebClient.builder().build());
    }

    public CookedWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public RequestHeadersSpec<?> get(String uri) {
        return webClient.get().uri(uri).cookies(
                stringStringMultiValueMap -> cookies.forEach(
                        stringStringMultiValueMap::add
                )
        );
    }

    public RequestBodySpec post(String uri) {
        return webClient.post().uri(uri).cookies(
                stringStringMultiValueMap -> cookies.forEach(
                        stringStringMultiValueMap::add
                )
        );
    }

    public void putCookie(String name, String value) {
        cookies.put(name, value);
    }
}
