package org.example.siljeun.global.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.user.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain
  ) throws ServletException, IOException {
    String token = resolveToken(request);

    if (token != null) {
      if (jwtUtil.validateToken(token)) {
        try {
          String username = jwtUtil.getUsername(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          Authentication auth = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
          // 인증 실패 시 로깅
          log.warn("JWT 인증 실패: {}", e.getMessage());

          // 필요한 경우 401 응답을 명시적으로 줄 수 있음
          // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
        }
      } else {
        log.debug("유효하지 않은 JWT 토큰");
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");

    if (bearer != null && bearer.startsWith(JwtUtil.BEARER_PREFIX)) {
      return bearer.substring(JwtUtil.BEARER_PREFIX.length());
    }

    return null;
  }

}