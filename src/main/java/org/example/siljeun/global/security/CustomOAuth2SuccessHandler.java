package org.example.siljeun.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    log.debug("----- 로그인 성공 -----");

    // principal에서 사용자 정보 추출
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String username = "kakao_" + oAuth2User.getAttribute("id").toString();

    // JWT 생성
    String token = jwtUtil.createToken(username);

    // JWT를 HttpOnly 쿠키로 설정
    Cookie cookie = new Cookie("token", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(false); // 개발 환경에서는 false, HTTPS 환경에서는 true
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60); // 1시간

    response.addCookie(cookie);

    // 응답 상태만 OK로 설정
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write("{\"message\": \"Login successful\"}");
    response.getWriter().flush();

    // 기본 경로로 리다이렉트
    response.sendRedirect("/");
  }

}
