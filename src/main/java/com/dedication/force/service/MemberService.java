package com.dedication.force.service;

import com.dedication.force.common.exception.CustomAPIException;
import com.dedication.force.common.exception.CustomDataNotFoundException;
import com.dedication.force.common.exception.CustomForbiddenException;
import com.dedication.force.common.jwt.JwtTokenProvider;
import com.dedication.force.common.jwt.JwtTokenRequest;
import com.dedication.force.domain.dto.AddMemberRequest;
import com.dedication.force.domain.dto.LoginMemberRequest;
import com.dedication.force.domain.dto.LoginMemberResponse;
import com.dedication.force.domain.entity.*;
import com.dedication.force.repository.MemberRepository;
import com.dedication.force.repository.MemberRoleRepository;
import com.dedication.force.repository.RoleRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final TokenStorageService tokenStorageService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JPAQueryFactory queryFactory;


    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PHONE_PATTERN = "^[0-9]{10,11}$";

    // 이메일 중복 확인하기
    public void checkMemberEmail(String email) {
        Matcher emailRex = Pattern.compile(EMAIL_PATTERN).matcher(email);

        if (email.isEmpty()) {
            throw new CustomAPIException("이메일은 필수 입력입니다.");
        }
        if (!emailRex.matches()) {
            throw new CustomAPIException("잘못된 이메일 입니다.");
        }
        if (memberRepository.existsByEmail(email)) {
            throw new CustomAPIException("이미 가입된 이메일 입니다.");
        }
    }

    // 전화번호 중복 확인하기
    public void checkMemberPhone(String phone) {
        Matcher phoneRex = Pattern.compile(PHONE_PATTERN).matcher(phone);

        if (phone.isEmpty()) {
            throw new CustomAPIException("휴대전화번호는 필수 입력입니다.");
        }
        if (!phoneRex.matches()) {
            throw new CustomAPIException("잘못된 휴대전화번호 입니다.");
        }
        if (memberRepository.existsByPhone(phone)) {
            throw new CustomAPIException("이미 가입된 휴대전화번호 입니다.");
        }
    }

    // 회원 등록
    @Transactional
    public void addMember(AddMemberRequest request) {
        checkMemberPhone(request.phone());
        checkMemberEmail(request.email());

        String encodedPassword = bCryptPasswordEncoder.encode(request.password());

        Member member = Member.of(
                request.email(),
                encodedPassword,
                request.phone()
        );

        memberRepository.save(member);

        Role role = roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new CustomAPIException("잘못된 권한입니다."));

        MemberRole memberRole = new MemberRole();
        memberRole.setMember(member);
        memberRole.setRole(role);
    }

    // 회원 로그인
    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomDataNotFoundException("잘못된 아이디입니다."));

        if (!bCryptPasswordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomForbiddenException("잘못된 비밀번호입니다.");
        }

        List<String> roles = member.getMemberRoles().stream()
                .map(memberRole -> memberRole.getRole().getRoleType().name())
                .toList();

        JwtTokenRequest jwtTokenRequest = JwtTokenRequest.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .roles(roles)
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(jwtTokenRequest);
        String refreshToken = jwtTokenProvider.createRefreshToken(jwtTokenRequest);

        tokenStorageService.addToken(refreshToken, "refresh_token");

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 모든 회원 조회
    @Transactional
    public List<Member> findAllMember() {
        QMember qMember = QMember.member; //기본 인스턴스 사용

        return queryFactory.selectFrom(qMember)
                .orderBy(qMember.createdAt.asc())
                .fetch();
    }


    // 회원 수정

    // 회원 삭제

    // 이메일로 회원 검색하기
    public Member findByEmail(String email) {
        QMember qMember = QMember.member;

        return queryFactory.selectFrom(qMember)
                .where(qMember.email.eq(email))
                .fetchOne();
    }
}
