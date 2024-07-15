package com.dedication.force.member.repository;

import com.dedication.force.article.domain.entity.Role;
import com.dedication.force.article.domain.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}
