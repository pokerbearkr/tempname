package org.example.siljeun.domain.oauth.client;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.oauth.dto.KakaoUserInfo;
import org.example.siljeun.global.config.KakaoOAuthProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

  private final RestTemplate restTemplate;
  private final KakaoOAuthProperties properties;

  public String getAccessToken(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", properties.getClientId());
    params.add("redirect_uri", properties.getRedirectUri());
    params.add("code", code);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        properties.getTokenUri(),
        HttpMethod.POST,
        request,
        new ParameterizedTypeReference<>() {
        }
    );

    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
      throw new RuntimeException("카카오 Access Token 요청 실패");
    }

    return response.getBody().get("access_token").toString();
  }

  public KakaoUserInfo getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
        properties.getUserInfoUri(),
        HttpMethod.GET,
        request,
        KakaoUserInfo.class
    );

    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
      throw new RuntimeException("카카오 사용자 정보 요청 실패");
    }

    return response.getBody();
  }

}
