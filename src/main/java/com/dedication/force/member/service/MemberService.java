package com.dedication.force.member.service;

import com.dedication.force.article.domain.dto.*;
import com.dedication.force.article.domain.entity.*;
import com.dedication.force.common.exception.CustomAPIException;
import com.dedication.force.common.exception.CustomDataNotFoundException;
import com.dedication.force.common.exception.CustomForbiddenException;
import com.dedication.force.common.exception.CustomJwtException;
import com.dedication.force.common.jwt.JwtTokenProvider;
import com.dedication.force.common.jwt.dto.JwtTokenRequest;
import com.dedication.force.common.jwt.entity.TokenType;
import com.dedication.force.common.jwt.dto.RefreshTokenRequest;
import com.dedication.force.common.jwt.entity.TokenBlacklist;
import com.dedication.force.common.jwt.entity.TokenStorage;
import com.dedication.force.common.security.CustomUserDetailsService;
import com.dedication.force.member.repository.MemberRepository;
import com.dedication.force.member.repository.RoleRepository;
import com.dedication.force.common.jwt.repository.TokenBlacklistRepository;
import com.dedication.force.common.jwt.repository.TokenStorageRepository;
import com.dedication.force.common.jwt.service.TokenStorageService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final TokenStorageRepository tokenStorageRepository;

    private final TokenStorageService tokenStorageService;
    private final JPAQueryFactory queryFactory;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

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

        tokenStorageService.addToken(refreshToken, TokenType.REFRESH_TOKEN);

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 회원 로그아웃
    @Transactional
    public void logout(LogoutRequest request) {
        if(tokenBlacklistRepository.existsByToken(request.refreshToken())) {
            throw new CustomJwtException("블랙리스트에 등록된 토큰입니다.");
        }

        TokenStorage tokenStorage = tokenStorageRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new CustomJwtException("존재하지 않는 토큰입니다."));

        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                .token(request.refreshToken())
                .build();

        tokenStorageRepository.delete(tokenStorage);
        tokenBlacklistRepository.save(tokenBlacklist);
    }


    // 토큰 재발급
    @Transactional
    public LoginMemberResponse refreshTokenReissue(RefreshTokenRequest request) {
        Boolean isValidateToken = jwtTokenProvider.validateToken(request.refreshToken(), jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY());
        Boolean isRefreshTokenExpiringSoon = jwtTokenProvider.isTokenExpiringSoon(request.refreshToken(), jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY(), 60 * 24L);
        Long memberId = jwtTokenProvider.getMemberIdFromToken(request.refreshToken(), jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY());
        String email = jwtTokenProvider.getEmailFromToken(request.refreshToken(), jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY());
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (tokenBlacklistRepository.existsByToken(request.refreshToken())) {
            throw new CustomJwtException("블랙리스트에 등록된 토큰입니다.");
        }

        // 리프레시 토큰이 유효하고 만료 시간이 24 시간 이상 남은 경우: 액세스 토큰만 재발행
        if (isValidateToken && !isRefreshTokenExpiringSoon) {
            String newAccessToken = jwtTokenProvider.createAccessToken(new JwtTokenRequest(userDetails));
            return LoginMemberResponse.builder()
                    .memberId(memberId)
                    .accessToken(newAccessToken)
                    .refreshToken(request.refreshToken())
                    .build();
        }

        // 리프레시 토큰이 만료되거나 만료 시간이 24시간 미만 남은 경우: 액세스 토큰, 리프레시 토큰 재발행
        String newAccessToken = jwtTokenProvider.createAccessToken(new JwtTokenRequest(userDetails));
        String newRefreshToken = jwtTokenProvider.createRefreshToken(new JwtTokenRequest(userDetails));
        return LoginMemberResponse.builder()
                .memberId(memberId)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // 모든 회원 조회
    @Transactional
    public List<MemberDto> findAllMember() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(member -> new MemberDto(
                        member.getId(),
                        member.getEmail(),
                        member.getPhone(),
                        member.getCreatedAt(),
                        member.getModifiedAt())
                ).toList();
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
