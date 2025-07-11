package io.github.kaurami.wems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WemsApplication.class, args);
	}

}
