package org.example.siljeun.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.auth.dto.LoginResponse;
import org.example.siljeun.global.security.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManagerBuilder authManagerBuilder;
  private final JwtUtil jwtUtil;

  public LoginResponse login(String username, String password) {
    Authentication authentication = authManagerBuilder.getObject()
        .authenticate(new UsernamePasswordAuthenticationToken(username, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtUtil.createToken(username);

    return new LoginResponse(token);
  }

}