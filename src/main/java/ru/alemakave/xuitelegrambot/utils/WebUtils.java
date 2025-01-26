package ru.alemakave.xuitelegrambot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
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

    public static void connectProxyToWebClientBuilder(WebClient.Builder webClientBuilder, String proxyAddress, int proxyPort) {
        if (proxyAddress != null && !proxyAddress.isEmpty() && proxyPort >= 0 && proxyPort <= 65535) {
            HttpClient httpClient = HttpClient.create().proxy(proxy -> {
                proxy.type(ProxyProvider.Proxy.HTTP)
                        .host(proxyAddress)
                        .port(proxyPort);
            });

            webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
    }
}
