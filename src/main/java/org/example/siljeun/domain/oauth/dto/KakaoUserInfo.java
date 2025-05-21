package org.example.siljeun.domain.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfo {

  private Long id;
  private String email;
  private String nickname;

}