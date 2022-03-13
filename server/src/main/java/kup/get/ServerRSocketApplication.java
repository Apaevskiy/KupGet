package kup.get;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ServerRSocketApplication {
	public static void main(String[] args) {
		Hooks.onErrorDropped(err -> System.out.println("Disconnecting client " + err.getLocalizedMessage()));
		SpringApplication.run(ServerRSocketApplication.class, args);
	}
}
