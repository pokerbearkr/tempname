package org.example.siljeun.domain.auth.service;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.auth.dto.request.SignUpRequest;
import org.example.siljeun.domain.auth.dto.response.LoginResponse;
import org.example.siljeun.domain.auth.dto.response.SignUpResponse;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.example.siljeun.global.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public SignUpResponse signUp(SignUpRequest request) {
    // 비밀번호 암호화
    String password = passwordEncoder.encode(request.password());

    // 회원 생성 및 저장
    User user = new User(request.email(), request.username(), password, request.name(),
        request.nickname(), request.role(), request.provider());
    User savedUser = userRepository.save(user);

    return new SignUpResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getUsername());
  }

  public LoginResponse login(String username, String rawPassword) {
    // 회원이 존재하지 않는 경우
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchElementException("아이디 또는 비밀번호가 올바르지 않습니다."));

    // 비밀번호가 틀린 경우
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    String token = jwtUtil.createToken(username);

    return new LoginResponse(token);
  }

}