package org.example.siljeun.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.user.enums.Provider;
import org.example.siljeun.domain.user.enums.Role;
import org.example.siljeun.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, unique = true, length = 255)
  private String username;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false, length = 10)
  private String name;

  @Column(nullable = false, length = 10)
  private String nickname;

  @Column(length = 255)
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Provider provider;

  private Long providerId;

  private LocalDateTime deletedAt;

  // 로컬 회원 가입용 생성자
  public User(String email, String username, String password, String name, String nickname,
      Role role, Provider provider) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.role = role;
    this.provider = provider;
  }

  // 소셜 회원 가입용 생성자
  public User(String email, String username, String password, String name, String nickname,
      Role role, Provider provider, Long providerId) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.role = role;
    this.provider = provider;
    this.providerId = providerId;
  }

}