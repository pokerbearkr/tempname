package org.example.siljeun.domain.oauth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.siljeun.domain.oauth.dto.KakaoAccessToken;
import org.example.siljeun.domain.oauth.dto.KakaoUserInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient {

  private final RestTemplate restTemplate;

  // 현재 카카오 API 서버에서 인가 코드를 제공한 상태이다
  // 서비스 서버가 인가 코드를 이용해 카카오 API 서버로 액세스 토큰을 요청한다
  public String getAccessToken(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    // 아래 4가지 값은 필수
    params.add("grant_type", "authorization_code");
    params.add("client_id", "eaee0e144aeb9afef54d5c449448baea"); // 카카오 REST API 키
    params.add("redirect_uri", "http://localhost:8080/oauth/kakao/callback"); // 여기서 문제 발생?
    params.add("code", code); // 인가 코드

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    // 명시한 URL로 (인가 코드를 담은) POST 요청을 보내면 카카오 API 서버에서 액세스 토큰을 응답한다
    KakaoAccessToken response = restTemplate.postForEntity(
        "https://kauth.kakao.com/oauth/token",
        request,
        KakaoAccessToken.class
    ).getBody();

    log.debug("----- 액세스 토큰: {} -----", response.accessToken());

    return response.accessToken();
  }

  // 서비스 서버가 카카오 인증 서버에 저장된 회원 정보를 요청한다
  public KakaoUserInfo getUserInfo(String accessToken) {
    // HTTP 헤더 설정
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // 설정한 HTTP 헤더를 이용해 요청 생성
    final HttpEntity<Void> request = new HttpEntity<>(headers);

    // GET 메서드로 회원 정보를 요청한 후 KakaoUserInfo 객체에 담음
    final KakaoUserInfo response = restTemplate.exchange(
        "https://kapi.kakao.com/v2/user/me",
        HttpMethod.GET,
        request,
        KakaoUserInfo.class
    ).getBody();

    return response;
  }

}