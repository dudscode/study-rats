package com.example.studyrats.config;

import lombok.Builder;

@Builder
public record JWTUserData(
        String idUser,
        String email) {
}
