package org.example.siljeun.domain.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.auth.dto.response.LoginResponse;
import org.example.siljeun.domain.oauth.client.KakaoOAuthClient;
import org.example.siljeun.domain.oauth.dto.KakaoUserInfo;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.enums.Provider;
import org.example.siljeun.domain.user.enums.Role;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.example.siljeun.global.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService {

  private final KakaoOAuthClient kakaoOAuthClient;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  // 인가 코드를 이용해 카카오 로그인 API를 호출한다
  public LoginResponse kakaoLogin(String code) {
    // 1. 카카오에 인가 코드를 넘겨서 액세스 토큰을 획득한다
    log.debug("----- 액세스 토큰 발급 -----");
    final String accessToken = kakaoOAuthClient.getAccessToken(code);

    // 2. 카카오에 액세스 토큰을 넘겨서 카카오에 저장된 사용자 정보를 획득한다
    log.debug("----- 사용자 정보 획득 -----");
    final KakaoUserInfo userInfo = kakaoOAuthClient.getUserInfo(accessToken);

    // 3. 해당 정보를 이용해 회원 가입 또는 로그인을 처리한다
    log.debug("----- 회원 가입 또는 로그인 -----");
    User user = userRepository.findByEmail(userInfo.kakaoAccount().email())
        .orElseGet(() -> registerUser(userInfo));

    // 4. 서비스 서버에 저장된 회원 정보를 이용해 JWT 토큰을 발급받는다
    log.debug("----- JWT 토큰 발급 -----");
    String token = jwtUtil.createToken(user.getUsername());

    return new LoginResponse(token);
  }

  private User registerUser(KakaoUserInfo userInfo) {
    String username = "kakao" + userInfo.id();
    String password = passwordEncoder.encode(username);
    User user = new User(
        userInfo.kakaoAccount().email(),
        username,
        password,
        userInfo.kakaoAccount().profile().nickname(),
        userInfo.kakaoAccount().profile().nickname(),
        Role.USER,
        Provider.KAKAO,
        userInfo.id()
    );
    log.debug("--------------------회원 가입 메서드 실행--------------------");
    log.debug("email: {}, username: {}, password: {}, name: {}, nickname: {}, id: {}",
        userInfo.kakaoAccount().email(),
        username,
        password,
        userInfo.kakaoAccount().profile().nickname(),
        userInfo.kakaoAccount().profile().nickname(),
        userInfo.id()
    );

    return userRepository.save(user);
  }

}