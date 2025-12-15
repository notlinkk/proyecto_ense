package com.mentory.ense_proyect.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AuthenticationConfiguration {
    @Value("${keystore.location:classpath:keys.p12}")
    private String ksLocation;
    @Value("${keystore.password:123456789}")
    private String ksPassword;
    @Value("${keystore.private.password:123456789}")
    private String keyPassword;
    @Value("${keystore.private.name:jwt}")
    private String keyName;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

    @Bean
    public KeyPair jwtSignatureKeys(){
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(ResourceUtils.getFile(ksLocation)), ksPassword.toCharArray());

            return new KeyPair(ks.getCertificate(keyName).getPublicKey(), (PrivateKey)ks.getKey(keyName, keyPassword.toCharArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configuración de CORS para permitir peticiones desde el frontend.
     * 
     * IMPORTANTE para la autenticación:
     * - allowCredentials: permite enviar/recibir cookies HttpOnly
     * - exposedHeaders: permite al frontend leer el header Authorization
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos (frontend en desarrollo)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Headers permitidos en las peticiones
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "API-Version",
            "X-Requested-With"
        ));
        
        // CRÍTICO: Exponer el header Authorization para que el frontend pueda leerlo
        // Sin esto, el navegador oculta el header en respuestas cross-origin
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        
        // CRÍTICO: Permitir credenciales (cookies) en peticiones cross-origin
        // Necesario para que la cookie HttpOnly del refresh token se envíe
        configuration.setAllowCredentials(true);
        
        // Tiempo de caché para preflight requests (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}