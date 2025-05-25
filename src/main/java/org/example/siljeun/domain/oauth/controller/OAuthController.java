package org.example.siljeun.domain.oauth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.auth.dto.response.LoginResponse;
import org.example.siljeun.domain.oauth.service.KakaoOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

  private final KakaoOAuthService kakaoOAuthService;

  /*
     1. 클라이언트가 카카오 로그인을 요청한다
     2. /oauth/kakao/callback?code={code}로 리다이렉트된다
     3. 이때 카카오에서 쿼리 스트링으로 인가 코드를 넘겨준다
     4. 넘어온 인가 코드를 이용해서 카카오 로그인 API를 호출한다
   */
  @GetMapping("/kakao/callback")
  public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
    log.debug("---------- METHOD: kakaoCallback ----------");
    return ResponseEntity.ok(kakaoOAuthService.kakaoLogin(code));
  }

}