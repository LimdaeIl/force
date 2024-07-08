package com.dedication.force.controller;

import com.dedication.force.common.HttpResponse;
import com.dedication.force.domain.dto.AddMemberRequest;
import com.dedication.force.domain.dto.LoginMemberRequest;
import com.dedication.force.domain.dto.LoginMemberResponse;
import com.dedication.force.domain.entity.Member;
import com.dedication.force.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final MemberService memberService;

    // 회원 등록
    @PostMapping("/signup")
    public ResponseEntity<HttpResponse<Void>> addMember(@RequestBody @Valid AddMemberRequest request, BindingResult result) {
        memberService.addMember(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "회원가입에 성공했습니다.", null), HttpStatus.CREATED);
    }

    // 회원 로그인
    @PostMapping("/login")
    public ResponseEntity<HttpResponse<LoginMemberResponse>> login(@RequestBody @Valid LoginMemberRequest request, BindingResult result) {
        LoginMemberResponse loginMemberResponse = memberService.login(request);
        return new ResponseEntity<>(new HttpResponse<>(1, " 로그인에 성공했습니다.", loginMemberResponse), HttpStatus.CREATED);
    }

    // 회원 로그아웃


    // 모든 회원 조회
    @GetMapping("")
    public ResponseEntity<HttpResponse<List<Member>>> findAllMember() {
        return new ResponseEntity<>(new HttpResponse<>(1, "모든 회원 조회입니다.", memberService.findAllMember()), HttpStatus.OK);
    }

    // 단건 회원 조회(id)

    // 아이디 중복 확인

    // 전화번호 중복 확인

    // 회원 수정

    // 회원 삭제

    // JWT 엑세스 토큰 재발급

    // JWT 리프레시 토큰 재발급
}
