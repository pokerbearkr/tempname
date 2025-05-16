package org.example.siljeun.global.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  public static final String BEARER_PREFIX = "Bearer "; // 토큰 식별자
  private final long EXPIRATION_TIME = 60 * 60 * 1000;  // 토큰 만료 시간 = 60분

  @Value("${jwt.secret.key}")
  private String SECRET_KEY; // BASE64로 인코딩한 비밀 키

}