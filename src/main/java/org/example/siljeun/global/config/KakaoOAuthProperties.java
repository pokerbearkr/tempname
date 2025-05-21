package org.example.siljeun.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoOAuthProperties {

  private String clientId;
  private String redirectUri;
  private String tokenUri;
  private String userInfoUri;

}
