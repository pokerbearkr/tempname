package org.example.siljeun.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.user.entity.User;
import org.example.siljeun.domain.user.repository.UserRepository;
import org.example.siljeun.global.security.PrincipalDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User principal = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    return new PrincipalDetails(principal);
  }

}