package ru.alemakave.xuitelegrambot.configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.net.ssl.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
@PropertySource(value = {"file:./application.yml"}, ignoreResourceNotFound = true)
public class SslConfiguration {
    @Value("${threex.panel.ssl.truststore.path:./3x-ui.jks}")
    private String trustStorePath;
    @Value("${threex.panel.ssl.truststore.password:changeme")
    private String trustStorePassword;
    @Value("${threex.panel.ssl.key.public.path:./3x-ui.pem}")
    private String pemCertificateFilePath;

    @Bean
    public SslContext sslContext() throws Exception {
        if (Files.exists(Path.of(trustStorePath))) {
            return getSslContextFromTrustStore();
        } else if (Files.exists(Path.of(pemCertificateFilePath))) {
            return getPemSslContext();
        } else {
            return SslContextBuilder.forClient().build();
        }
    }

    private SslContext getPemSslContext() throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        try (FileInputStream fis = new FileInputStream(pemCertificateFilePath)) {
            Certificate certificate = certificateFactory.generateCertificate(fis);
            keyStore.setCertificateEntry("3x-ui", certificate);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        return SslContextBuilder.forClient()
                .trustManager(tmf)
                .secureRandom(new SecureRandom())
                .build();
    }

    private SslContext getSslContextFromTrustStore() throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(trustStorePath)) {
            trustStore.load(fis, trustStorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        return SslContextBuilder.forClient()
                .protocols("TLSv1.3")
                .trustManager(tmf)
                .build();
    }
}
