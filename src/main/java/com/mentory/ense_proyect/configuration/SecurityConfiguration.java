package com.mentory.ense_proyect.configuration;

import com.mentory.ense_proyect.filter.JWTFilter;
import com.mentory.ense_proyect.configuration.AuthenticationConfiguration;
import com.mentory.ense_proyect.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.*;

@Configuration
@EnableMethodSecurity()
public class SecurityConfiguration {
    JWTFilter jwtFilter;
    AuthenticationService authenticationService;
    CorsConfigurationSource corsConfigurationSource;

    @Autowired
    public SecurityConfiguration(JWTFilter jwtFilter, AuthenticationService authenticationService, 
                                 CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.authenticationService = authenticationService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(authorize -> authorize
            // 🔓 Swagger / OpenAPI / Scalar
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/scalar/**",
                "/scalar.html"
            ).permitAll()

            // 🔓 Auth endpoints
            .requestMatchers("/auth/**").permitAll()

            // 🔒 Resto
            .anyRequest().authenticated()
        )
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterAfter(jwtFilter, BasicAuthenticationFilter.class)
        .build();
}


    @Bean
    public RoleHierarchy roleHierarchy() {
        return authenticationService.loadRoleHierarchy();
    }
}
