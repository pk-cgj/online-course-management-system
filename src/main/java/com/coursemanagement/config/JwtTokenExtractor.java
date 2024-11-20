package com.coursemanagement.config;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class JwtTokenExtractor {
    private final String issuer;
    private final JwkProvider jwkProvider;

    public JwtTokenExtractor(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri
    ) throws MalformedURLException {
        this.issuer = issuerUri;
        this.jwkProvider = new UrlJwkProvider(new URL(jwkSetUri));
    }

    public record UserInfo(
            String keycloakId,
            String email,
            String role,
            String name,
            Set<String> permissions
    ) {
    }

    public UserInfo extractUserInfo(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = jwkProvider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            jwt = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .acceptLeeway(10)
                    .build()
                    .verify(token);

            String keycloakId = jwt.getSubject();
            String email = extractEmail(jwt);
            String name = extractName(jwt);
            Set<String> roles = extractRoles(jwt);
            String role = determineHighestRole(roles);
            Set<String> permissions = extractPermissions(jwt);

            validateTokenInfo(keycloakId, email);

            return new UserInfo(keycloakId, email, role, name, permissions);

        } catch (JWTVerificationException exception) {
            log.error("JWT token verification failed", exception);
            throw new SecurityException("Invalid JWT token", exception);
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            throw new SecurityException("Error processing JWT token", e);
        }
    }

    private String extractEmail(DecodedJWT jwt) {
        String email = jwt.getClaim("email").asString();
        if (email == null || email.isBlank()) {
            email = jwt.getClaim("preferred_username").asString();
        }
        return email;
    }

    private String extractName(DecodedJWT jwt) {
        String name = jwt.getClaim("name").asString();
        if (name == null || name.isBlank()) {
            name = jwt.getClaim("given_name").asString();
            String familyName = jwt.getClaim("family_name").asString();
            if (familyName != null && !familyName.isBlank()) {
                name = name != null ? name + " " + familyName : familyName;
            }
        }
        return name;
    }

    public Set<String> extractRoles(DecodedJWT jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access").asMap();
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                return new HashSet<>(roles);
            }
        } catch (Exception e) {
            log.warn("Failed to extract roles from token", e);
        }
        return new HashSet<>();
    }

    private String determineHighestRole(Set<String> keycloakRoles) {
        if (keycloakRoles.stream().anyMatch(role -> role.toLowerCase().contains("admin"))) {
            return "ADMIN";
        } else if (keycloakRoles.stream().anyMatch(role -> role.toLowerCase().contains("instructor"))) {
            return "INSTRUCTOR";
        } else {
            return "STUDENT";
        }
    }

    private Set<String> extractPermissions(DecodedJWT jwt) {
        try {
            Claim resourceAccessClaim = jwt.getClaim("resource_access");
            if (resourceAccessClaim.isNull()) {
                return new HashSet<>();
            }

            Map<String, Object> resourceAccess = resourceAccessClaim.asMap();
            String clientId = jwt.getClaim("azp").asString(); // authorized party

            if (resourceAccess.containsKey(clientId)) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
                if (clientAccess.containsKey("roles")) {
                    List<String> permissions = (List<String>) clientAccess.get("roles");
                    return new HashSet<>(permissions);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract permissions from token", e);
        }
        return new HashSet<>();
    }

    private void validateTokenInfo(String keycloakId, String email) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new SecurityException("Token must contain a valid subject (keycloak ID)");
        }
        if (email == null || email.isBlank()) {
            throw new SecurityException("Token must contain a valid email");
        }
    }
}
