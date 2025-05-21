package org.example.siljeun.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.auth.dto.LoginRequest;
import org.example.siljeun.domain.auth.dto.LoginResponse;
import org.example.siljeun.domain.auth.service.AuthService;
import org.example.siljeun.global.dto.ResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseDto<LoginResponse> login(@RequestBody LoginRequest request) {
    try {
      LoginResponse response = authService.login(request.username(), request.password());
      return ResponseDto.success("로그인 성공", response);
    } catch (Exception e) {
      return ResponseDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
  }

}