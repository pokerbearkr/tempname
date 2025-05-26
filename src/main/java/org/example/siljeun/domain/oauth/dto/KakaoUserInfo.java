package org.example.siljeun.domain.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfo(
    Long id,                  // 회원 번호
    KakaoAccount kakaoAccount // 카카오 계정 정보
) {

}