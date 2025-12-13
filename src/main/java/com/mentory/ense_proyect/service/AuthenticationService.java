package com.mentory.ense_proyect.service;

import com.mentory.ense_proyect.exception.InvalidRefreshTokenException;
import com.mentory.ense_proyect.model.entity.Permission;
import com.mentory.ense_proyect.model.entity.RefreshToken;
import com.mentory.ense_proyect.model.entity.User;
import com.mentory.ense_proyect.repository.UserRepository;
import com.mentory.ense_proyect.repository.RefreshTokenRepository;
import com.mentory.ense_proyect.repository.RoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.sql.Ref;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final KeyPair keyPair;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.jwt.ttl:PT15M}")
    private Duration tokenTTL;
    
    @Value("${auth.refresh.ttl:PT72H}")
    private Duration refreshTTL;

    @Autowired
    public AuthenticationService(
            AuthenticationManager authenticationManager,
            KeyPair keyPair,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository

    ) {
        this.authenticationManager = authenticationManager;
        this.keyPair = keyPair;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Authentication login(String username, String password) throws AuthenticationException {
        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, password));
    }

    public Authentication login(String refreshToken) throws AuthenticationException {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            User user = userRepository.findByUsername(token.get().getUser())
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
                
            return login(user.getUsername(), user.getPassword());
        }

        throw new InvalidRefreshTokenException("Invalid refresh token");
    }

    public void invalidateTokens(User user) {
        refreshTokenRepository.deleteAllByUser(user.getUsername());
    }

    

    public String regenerateRefreshToken(Authentication auth) {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken(uuid.toString(), auth.getName(), refreshTTL.toSeconds());
        refreshTokenRepository.deleteAllByUser(auth.getName());
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }



    public String generateJWT(Authentication auth) {
        List<String> roles = auth.getAuthorities()
                .stream()
                .filter(authority -> authority instanceof SimpleGrantedAuthority)
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(auth.getName())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(tokenTTL)))
                .notBefore(Date.from(Instant.now()))
                .claim("roles", roles)
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public Authentication parseJWT(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return UsernamePasswordAuthenticationToken.authenticated(username, token, user.get().getAuthorities());
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    public RoleHierarchy loadRoleHierarchy() {
        RoleHierarchyImpl.Builder builder = RoleHierarchyImpl.withRolePrefix("");

        roleRepository.findAll().forEach(role -> {
            if (!role.getIncludes().isEmpty()) {
                builder.role("ROLE_"+role.getRolename()).implies(
                        role.getIncludes().stream().map(i -> "ROLE_"+i.getRolename()).toArray(String[]::new)
                );
            }
            if (!role.getPermissions().isEmpty()) {
                builder.role("ROLE_"+role.getRolename()).implies(
                        role.getPermissions().stream().map(Permission::toString).toArray(String[]::new)
                );
            }
        });

        return builder.build();
    }
}
