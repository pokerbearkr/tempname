package org.example.siljeun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SiljeunApplication {

  public static void main(String[] args) {
    SpringApplication.run(SiljeunApplication.class, args);
  }

}
