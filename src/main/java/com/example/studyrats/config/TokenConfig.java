package com.example.studyrats.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.studyrats.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenConfig {

    @Value("${spring.application.jwt.secret}")
    private String secretKey;

    private Algorithm algorithm;

    public String generateToken(User user) {
        return JWT.create().withClaim(
                "idUser", user.getUserId()
        ).withClaim(
                "email", user.getEmail()
        ).withExpiresAt(Instant.now().plusSeconds(86400)) // 24 horas
                .withIssuedAt(Instant.now())
                .sign(getAlgorithm());
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            var decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
            String idUser = decodedJWT.getClaim("idUser").asString();
            String email = decodedJWT.getSubject();
            return Optional.of(new JWTUserData(idUser, email));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    private Algorithm getAlgorithm() {
        if (algorithm == null) {
            algorithm = Algorithm.HMAC256(secretKey);
        }
        return algorithm;
    }
}
