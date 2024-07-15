package com.dedication.force.article.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class MemberRole {

    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Member member;

    @JoinColumn(name = "role_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Role role;

    public void setMember(Member member) {
        this.member = member;
        if (!member.getMemberRoles().contains(this)) {
            member.getMemberRoles().add(this);
        }
    }

    public void setRole(Role role) {
        this.role = role;
        if (!role.getMemberRoles().contains(this)) {
            role.getMemberRoles().add(this);
        }
    }
}