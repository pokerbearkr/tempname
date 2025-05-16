package org.example.siljeun.domain.user.repository;

import org.example.siljeun.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}