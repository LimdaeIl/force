package com.dedication.force.service;

import com.dedication.force.common.exception.CustomAPIException;
import com.dedication.force.domain.dto.AddMemberRequest;
import com.dedication.force.domain.entity.Member;
import com.dedication.force.domain.entity.QMember;
import com.dedication.force.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dedication.force.domain.entity.QMember.member;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JPAQueryFactory queryFactory;


    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PHONE_PATTERN = "^[0-9]{10,11}$";

    // 이메일 중복 확인하기
    public void checkMemberEmail(String email) {
        Matcher emailRex = Pattern.compile(EMAIL_PATTERN).matcher(email);

        if (email.isEmpty()) {throw new CustomAPIException("이메일은 필수 입력입니다.");}
        if (!emailRex.matches()) {throw new CustomAPIException("잘못된 이메일 입니다.");}
        if (memberRepository.existsByEmail(email)) {throw new CustomAPIException("이미 가입된 이메일 입니다.");}
    }

    // 전화번호 중복 확인하기
    public void checkMemberPhone(String phone) {
        Matcher phoneRex = Pattern.compile(PHONE_PATTERN).matcher(phone);

        if (phone.isEmpty()) {throw new CustomAPIException("휴대전화번호는 필수 입력입니다.");}
        if (!phoneRex.matches()) {throw new CustomAPIException("잘못된 휴대전화번호 입니다.");}
        if (memberRepository.existsByPhone(phone)) {throw new CustomAPIException("이미 가입된 휴대전화번호 입니다.");}
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
    }

    // 모든 회원 조회
    @Transactional
    public List<Member> allMember() {
        QMember qMember = QMember.member; //기본 인스턴스 사용

        return queryFactory.selectFrom(qMember)
                .orderBy(qMember.createdAt.asc())
                .fetch();
    }


    // 회원 수정

    // 회원 삭제

}
