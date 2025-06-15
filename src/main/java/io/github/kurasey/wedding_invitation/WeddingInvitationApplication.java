package io.github.kurasey.wedding_invitation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WeddingInvitationApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeddingInvitationApplication.class, args);
	}

}
