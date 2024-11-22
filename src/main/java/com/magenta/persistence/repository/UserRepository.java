package com.magenta.persistence.repository;

import java.util.Optional;

import com.magenta.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByEmail(String email);
}
