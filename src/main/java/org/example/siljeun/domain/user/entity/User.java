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
import org.example.siljeun.domain.user.enums.Provider;
import org.example.siljeun.domain.user.enums.Role;
import org.example.siljeun.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "users")
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

  @Column(nullable = false, length = 255)
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Provider provider;

  @Column(name = "provider_id", length = 255)
  private String providerId;

  private LocalDateTime deletedAt;

}