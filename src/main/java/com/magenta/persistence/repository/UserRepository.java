package com.magenta.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.magenta.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailAndUsernameNot(String email, String username);

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE u.username = :username")
    Optional<UserEntity> findByUsernameWithRoles(
            @Param("username") String username
    );

    @Query("SELECT DISTINCT u FROM UserEntity u " +
            "JOIN FETCH u.roles r " +
            "WHERE r.name = :roleName")
    List<UserEntity> findByRole(
            @Param("roleName") String roleName
    );
}
