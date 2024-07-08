package com.dedication.force.repository;

import com.dedication.force.domain.entity.Role;
import com.dedication.force.domain.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}
