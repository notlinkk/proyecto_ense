package com.mentory.ense_proyect.filter;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.net.HttpHeaders;
import com.mentory.ense_proyect.service.AuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;

@Component
public class JWTFilter extends OncePerRequestFilter{
    private final AuthenticationService authenticationService;
    
    @Autowired
    public JWTFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String jwt = token.substring(7); // Quitar "Bearer " (7 caracteres)
            if (jwt.isEmpty() || !jwt.contains(".")) {
                // Token inválido, continuar sin autenticación
                chain.doFilter(request, response);
                return;
            }
            
            Authentication authentication = authenticationService.parseJWT(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            // Token malformado o expirado, continuar sin autenticación
            // El endpoint decidirá si requiere autenticación o no
        }

        chain.doFilter(request, response);
    }
}
