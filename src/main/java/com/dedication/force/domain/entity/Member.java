package com.dedication.force.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL})
    private final List<Article> articles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL})
    private final List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL})
    private final List<MemberRole> memberRoles = new ArrayList<>();

    private Member(String email, String password, String phone) {
        this.email = email;
        this.password = password;
        this.phone = phone;

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.createdAt = now;
        this.modifiedAt = now;
    }

    public static Member of(String email, String password, String phone) {
        return new Member(email, password, phone);
    }
}
