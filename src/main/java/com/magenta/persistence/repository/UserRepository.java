package com.magenta.persistence.repository;

import java.util.Optional;

import com.magenta.persistence.entitiy.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByEmailAndUsernameNot(String email, String username);

}
