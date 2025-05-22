package org.example.siljeun.domain.oauth.dto;

import java.util.Map;

public interface OAuth2UserInfo {

  public Map<String, Object> getAttributes();

  String getProvider();

  String getProviderId();

  String getEmail();

  String getNickname();

}