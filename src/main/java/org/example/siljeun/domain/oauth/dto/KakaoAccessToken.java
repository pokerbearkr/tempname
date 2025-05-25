package org.example.siljeun.domain.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAccessToken(String tokenType,
                               String accessToken,
                               Integer expiresIn,
                               String refreshToken,
                               Integer refreshTokenExpiresIn) {

}
