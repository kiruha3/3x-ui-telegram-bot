package ru.alemakave.xuitelegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Slf4j
public class Application {
	public static void main(String[] args) {
		if (System.getenv("threex.connection.proxy.port") != null && System.getenv("threex.connection.proxy.port").isEmpty()) {
			System.setProperty("threex.connection.proxy.port", "-1");
		}

		SpringApplication application = new SpringApplication(Application.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
		log.info("Started 3x-ui Telegram Bot v" + BuildInfo.BUILD_VERSION);
	}
}
