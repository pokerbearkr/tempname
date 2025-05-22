package org.example.siljeun.global.queueing;

import io.micrometer.common.util.StringUtils;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.reservation.exception.CustomException;
import org.example.siljeun.domain.reservation.exception.ErrorCode;
import org.example.siljeun.domain.schedule.repository.ScheduleRepository;
import org.example.siljeun.global.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

  private final JwtUtil jwtUtil;
  private final ScheduleRepository scheduleRepository;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {

    HttpHeaders headers = request.getHeaders();
    URI uri = request.getURI();
    MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(uri).build()
        .getQueryParams();

    String token = headers.getFirst("Authorization");
    String scheduleId = params.getFirst("scheduleId");

    if (!jwtUtil.validateToken(token)) {
      return false;
    }

    if (StringUtils.isBlank(scheduleId) || !scheduleRepository.existsById(
        Long.valueOf(scheduleId))) {
      throw new CustomException(ErrorCode.MISSING_HEADER); // or return false
    }

    String username = jwtUtil.getUsername(token);
    attributes.put("username", username);
    attributes.put("scheduleId", scheduleId);

    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {

  }
}
