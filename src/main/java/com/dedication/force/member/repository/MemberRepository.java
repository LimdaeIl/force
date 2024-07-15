package com.dedication.force.member.repository;

import com.dedication.force.article.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);

    Optional<Member> findByEmail(String email);
}
