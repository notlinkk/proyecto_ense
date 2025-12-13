package com.mentory.ense_proyect.controller;

import java.time.Duration;

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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.mentory.ense_proyect.exception.DuplicatedUserException;
import com.mentory.ense_proyect.exception.InvalidRefreshTokenException;
import com.mentory.ense_proyect.model.User;
import com.mentory.ense_proyect.service.AuthenticationService;
import com.mentory.ense_proyect.service.UserService;

public class AuthenticationController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "__Secure-Refresh-Token";
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("login")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<Void> login(@RequestBody User user) {
        Authentication auth = authenticationService.login(user);
        String token = authenticationService.generateJWT(auth);
        String refreshToken = authenticationService.regenerateRefreshToken(auth);
        String refreshPath = MvcUriComponentsBuilder.fromMethodName(AuthenticationController.class, "refresh", "").build().toUri().getPath();

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .secure(true)
                .httpOnly(true)
                .sameSite(Cookie.SameSite.STRICT.toString())
                .path(refreshPath)
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.noContent()
                .headers(headers -> headers.setBearerAuth(token))
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<User> register(@RequestBody User user) throws DuplicatedUserException {
        User createdUser = userService.create(user);

        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(UserController.class, "getUser", user.getUsername()).build().toUri())
                .body(createdUser);
    }

    @PostMapping("refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        Authentication auth = authenticationService.login(refreshToken);

        if (auth.getPrincipal() != null) {
            return login((User)auth.getPrincipal());
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
