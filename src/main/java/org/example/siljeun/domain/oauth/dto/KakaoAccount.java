package org.example.siljeun.domain.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAccount(
    KakaoProfile profile, // 프로필 정보(닉네임, 프로필 사진)
    String email
) {

}