package org.example.siljeun.global.queueing;

import java.util.Map;
import org.example.siljeun.global.jwt.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

  private final JwtUtil jwtUtil;

  public JwtHandShakeInterceptor(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  // 소켓 연결 시도 직전에 동작
  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {

    HttpHeaders headers = request.getHeaders();
    String bearer = headers.getFirst("Authorization");

    if (bearer != null && bearer.startsWith(JwtUtil.BEARER_PREFIX)) {
      String jwt = bearer.substring(JwtUtil.BEARER_PREFIX.length());
      String username = jwtUtil.getUsername(jwt);
      attributes.put("username", username);
    }

    String scheduleId = headers.getFirst("scheduleId");
    attributes.put("scheduleId", scheduleId);

    return true;
  }

  // 소켓 연결된 직후 동작
  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {

  }
}
