package com.coursemanagement.config;

import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenExtractor {
    private final String issuer;
    private final JwkProvider jwkProvider;

    public JwtTokenExtractor(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri) throws Exception {
        this.issuer = issuerUri;
        this.jwkProvider = new UrlJwkProvider(new URL(jwkSetUri));
    }

    public record TokenInfo(String email, String role, String name) {

    }

    public TokenInfo extractTokenInfo(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = jwkProvider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            jwt = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);

            String email = jwt.getClaim("email").asString();
            String role = extractRole(jwt);
            String name = jwt.getClaim("name").asString();

            return new TokenInfo(email, role, name);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid JWT token", exception);
        } catch (Exception e) {
            throw new RuntimeException("Error processing JWT token", e);
        }
    }

    private String extractRole(DecodedJWT jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access").asMap();
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (!roles.isEmpty()) {
                return convertKeycloakRole(roles.get(0));
            }
        }
        return "STUDENT";
    }

    private String convertKeycloakRole(String keycloakRole) {
        if (keycloakRole.toLowerCase().contains("instructor")) {
            return "INSTRUCTOR";
        } else if (keycloakRole.toLowerCase().contains("admin")) {
            return "ADMIN";
        } else {
            return "STUDENT";
        }
    }
}