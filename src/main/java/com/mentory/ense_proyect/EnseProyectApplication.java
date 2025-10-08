package com.mentory.ense_proyect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;



@SpringBootApplication
@EnableMongoRepositories
// @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class EnseProyectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnseProyectApplication.class, args);
	}

}
