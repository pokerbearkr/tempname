package org.example.siljeun.global.queueing;

import io.micrometer.common.util.StringUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.global.jwt.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

  private final JwtUtil jwtUtil;

  // 소켓 연결 시도 직전에 동작
  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {

    HttpHeaders headers = request.getHeaders();
    String bearer = headers.getFirst("Authorization");

    if (bearer == null || !bearer.startsWith(JwtUtil.BEARER_PREFIX)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    String jwt = bearer.substring(JwtUtil.BEARER_PREFIX.length());
    jwtUtil.validateToken(jwt);
    String username = jwtUtil.getUsername(jwt);
    attributes.put("username", username);

    // Todo : STOMP 테스트 실패 여부에 따라 헤더가 아니라 uri 추출 방식으로 변경
    String scheduleId = headers.getFirst("scheduleId");
    if (StringUtils.isBlank(scheduleId)) {
      throw new CustomException(ErrorCode.MISSING_HEADER);
    }
    attributes.put("scheduleId", scheduleId);

    return true;
  }

  // 소켓 연결된 직후 동작
  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {

  }
}
