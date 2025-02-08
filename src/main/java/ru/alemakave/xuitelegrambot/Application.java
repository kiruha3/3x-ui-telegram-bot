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
		if (System.getenv("threex.connection.proxy.port").isEmpty()) {
			System.setProperty("threex.connection.proxy.port", "-1");
		}

		log.info("Running 3x-ui Telegram Bot v" + BuildInfo.BUILD_VERSION);

		SpringApplication application = new SpringApplication(Application.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}
}
