package com.example.studyrats.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.studyrats.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenConfig {
    // TODO colocar no application.properties ou nas env de maneira segura, apenas temporario
    private String secretKey = "mySecretKey12345";

    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    public String generateToken(User user) {
        return JWT.create().withClaim(
                "idUser", user.getUserId()
        ).withClaim(
                "email", user.getEmail()
        ).withExpiresAt(Instant.now().plusSeconds(86400)) // 24 horas
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            var decodedJWT = JWT.require(algorithm).build().verify(token);
            String idUser = decodedJWT.getClaim("idUser").asString();
            String email = decodedJWT.getSubject();
            return Optional.of(new JWTUserData(idUser, email));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}
