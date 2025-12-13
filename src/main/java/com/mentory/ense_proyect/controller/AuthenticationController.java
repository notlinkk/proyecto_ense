package com.mentory.ense_proyect.controller;

import java.time.Duration;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.exception.InvalidRefreshTokenException;
import com.mentory.ense_proyect.model.dto.LoginDTO;
import com.mentory.ense_proyect.model.dto.UserDTO;
import com.mentory.ense_proyect.model.entity.User;
import com.mentory.ense_proyect.service.AuthenticationService;
import com.mentory.ense_proyect.service.UserService;

@NullMarked
@RestController
@RequestMapping("auth")
public class AuthenticationController {
    // Nombre de la cookie del refresh token
    // Nota: En desarrollo sin HTTPS, no usar prefijo __Secure-
    // En producción con HTTPS, cambiar a "__Secure-Refresh-Token"
    private static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh-Token";
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("login")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> login(@RequestBody LoginDTO user) {
        System.out.println("Password recibida: " + user.password());
        Authentication auth = authenticationService.login(user.username(), user.password());
        String token = authenticationService.generateJWT(auth);
        String refreshToken = authenticationService.regenerateRefreshToken(auth);

        // Configuración de la cookie del refresh token:
        // - httpOnly: true -> No accesible desde JavaScript (protección XSS)
        // - secure: false en desarrollo, true en producción (solo HTTPS)
        // - sameSite: LAX -> Permite envío en navegación cross-site (necesario para CORS)
        // - path: /auth -> Solo se envía a endpoints de autenticación
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .secure(false)  // Cambiar a true en producción con HTTPS
                .httpOnly(true)
                .sameSite(Cookie.SameSite.LAX.toString())  // LAX para CORS en desarrollo
                .path("/auth")  // Simplificado para mayor compatibilidad
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.noContent()
                .headers(headers -> headers.setBearerAuth(token))
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<User> register(@RequestBody UserDTO dto) throws DuplicatedUserException {

        User user = new User(
            dto.username(),
            dto.name(),
            dto.surname1(),
            dto.surname2(),
            dto.email(),
            dto.password()
        );

        User createdUser = userService.create(user);

        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(UserController.class, "getUserV1", user.getUsername()).build().toUri())
                .body(createdUser);
    }

    @PostMapping("refresh")
    // IMPORTANTE: Este endpoint NO requiere JWT (isAnonymous)
    // La autenticación se hace mediante el refresh token en la cookie
    // Esto es necesario porque cuando el JWT expira, el usuario no tiene token válido
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("No refresh token provided");
        }
        Authentication auth = authenticationService.login(refreshToken);

        if (auth.getPrincipal() != null) {
            User user = (User)auth.getPrincipal();
            return login(new LoginDTO(user.getUsername(), user.getPassword()));
        }

        throw new InvalidRefreshTokenException(refreshToken);
    }

    @PostMapping("logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        Authentication auth = authenticationService.parseJWT(token);

        if (auth.getPrincipal() != null) {
            User user = (User)auth.getPrincipal();
            authenticationService.invalidateTokens(user);
            ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, null).build();

            return ResponseEntity.noContent()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();
        }

        throw new RuntimeException("Internal Error");
    }
}
