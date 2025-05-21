package org.example.siljeun.domain.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.oauth.service.KakaoOAuthService;
import org.example.siljeun.global.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

  private final KakaoOAuthService kakaoOAuthService;

  @GetMapping("/kakao/callback")
  public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
    String jwt = kakaoOAuthService.kakaoLogin(code);
    return ResponseEntity.ok(jwt);
  }

}