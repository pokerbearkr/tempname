package org.example.siljeun.domain.oauth.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.oauth.client.KakaoApiClient;
import org.example.siljeun.domain.oauth.dto.KakaoUserInfo;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.enums.Provider;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.example.siljeun.global.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

  private final KakaoApiClient kakaoApiClient;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  public String kakaoLogin(String code) {
    // 1. 액세스 토큰 요청
    String accessToken = kakaoApiClient.getAccessToken(code);

    // 2. 사용자 정보 요청
    KakaoUserInfo userInfo = kakaoApiClient.getUserInfo(accessToken);

    // 3. 회원 가입 또는 로그인 처리
    User user = userRepository.findByEmail(userInfo.getEmail())
        .orElseGet(() -> registerUser(userInfo));

    // 4. JWT 토큰 발급
    return jwtUtil.createToken(user.getUsername());
  }

  private User registerUser(KakaoUserInfo userInfo) {
    User user = new User(userInfo.getEmail(), userInfo.getNickname(), Provider.KAKAO,
        userInfo.getProviderId());
    return userRepository.save(user);
  }

}