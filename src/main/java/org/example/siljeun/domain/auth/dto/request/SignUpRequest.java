package org.example.siljeun.domain.auth.dto.request;

import org.example.siljeun.domain.user.enums.Provider;
import org.example.siljeun.domain.user.enums.Role;

public record SignUpRequest(String email,
                            String username,
                            String password,
                            String name,
                            String nickname,
                            Role role,
                            Provider provider) {

}