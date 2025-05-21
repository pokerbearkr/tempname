package org.example.siljeun.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    // 1. principal에서 사용자 정보 추출
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String username = "kakao_" + oAuth2User.getAttribute("id").toString();

    // 2. JWT 생성
    String token = jwtUtil.createToken(username);

    // 3. JSON 응답 세팅
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");

    // 4. JWT를 JSON 응답 바디로 내려주기
    String json = String.format("{\"token\":\"%s\"}", token);
    response.getWriter().write(json);
    response.getWriter().flush();
  }

}
