package org.codenergic.akinabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	private Application() {
	}

	public static void main(String[] args) {
		SpringApplication.run(new Application(), args);
	}
}
