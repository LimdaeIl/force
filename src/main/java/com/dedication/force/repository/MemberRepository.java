package com.dedication.force.repository;

import com.dedication.force.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);

    Optional<Member> findByEmail(String email);
}
