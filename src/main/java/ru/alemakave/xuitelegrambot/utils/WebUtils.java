package ru.alemakave.xuitelegrambot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import ru.alemakave.xuitelegrambot.model.validation.MyIp;

public class WebUtils {
    public static boolean isInvalidCountry(WebClient.Builder webClientBuilder) {
        MyIp myIp = webClientBuilder
                .baseUrl("https://api.myip.com")
                .build()
                .get()
                .retrieve()
                .toEntity(String.class)
                .map(stringResponseEntity -> {
                    try {
                        return new ObjectMapper().readValue(stringResponseEntity.getBody(), MyIp.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();

        return myIp == null || myIp.getCc().equals("RU");
    }

    public static HttpClient connectProxyToHttpClient(HttpClient httpClient, String proxyAddress, int proxyPort) {
        if (proxyAddress != null && !proxyAddress.isEmpty() && proxyPort >= 0 && proxyPort <= 65535) {
            return httpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                    .host(proxyAddress)
                    .port(proxyPort));
        }

        return httpClient;
    }
}
