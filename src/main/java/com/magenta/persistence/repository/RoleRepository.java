package com.magenta.persistence.repository;

import com.magenta.persistence.entitiy.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
