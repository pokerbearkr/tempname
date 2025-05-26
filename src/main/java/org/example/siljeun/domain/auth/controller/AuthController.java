package org.example.siljeun.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.auth.dto.request.LoginRequest;
import org.example.siljeun.domain.auth.dto.request.SignUpRequest;
import org.example.siljeun.domain.auth.dto.response.LoginResponse;
import org.example.siljeun.domain.auth.dto.response.SignUpResponse;
import org.example.siljeun.domain.auth.service.AuthService;
import org.example.siljeun.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
    SignUpResponse response = authService.signUp(request);
    if (response != null) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

  @PostMapping("/login")
  public ResponseDto<LoginResponse> login(@RequestBody LoginRequest request) {
    try {
      log.debug("----- 로그인 메서드 실행 -----");
      LoginResponse response = authService.login(request.username(), request.password());
      return ResponseDto.success("로그인 성공", response);
    } catch (Exception e) {
      return ResponseDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
  }

}