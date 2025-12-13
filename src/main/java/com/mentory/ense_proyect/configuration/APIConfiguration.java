
package com.mentory.ense_proyect.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class APIConfiguration implements WebMvcConfigurer {
    @Override
	public void configureApiVersioning(ApiVersionConfigurer configurer) {
		configurer.useRequestHeader("API-Version");
        configurer.useQueryParam("version");
        configurer.setDefaultVersion("1.0");
        configurer.setVersionRequired(false);
	}
}
