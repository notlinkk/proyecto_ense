package com.mentory.ense_proyect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class EnseProyectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnseProyectApplication.class, args);
	}

}
