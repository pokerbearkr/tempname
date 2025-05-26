package org.example.siljeun.global.config;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.user.service.PrincipalDetailsService;
import org.example.siljeun.global.security.CustomOAuth2SuccessHandler;
import org.example.siljeun.global.security.JwtAuthenticationFilter;
import org.example.siljeun.global.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final PrincipalDetailsService userDetailsService;
  private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**", "/oauth/**", "/oauth2/**", "/login/**", "/ws/**", "/ws",
                "/checkout.html", "/payments", "/success.html").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .successHandler(customOAuth2SuccessHandler)
            .defaultSuccessUrl("/auth/oauth2/success")
            .failureUrl("/auth/oauth2/failure")
        )
//        .formLogin(form -> form
//            .loginPage("/login"))
        .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}