package com.mentory.ense_proyect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication
// @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class EnseProyectApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnseProyectApplication.class, args);
	}

}
