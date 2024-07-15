package com.dedication.force.member.controller;

import com.dedication.force.common.HttpResponse;
import com.dedication.force.common.jwt.dto.RefreshTokenRequest;
import com.dedication.force.member.domain.dto.*;
import com.dedication.force.member.service.MemberService;
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
        return new ResponseEntity<>(new HttpResponse<>(1, " 로그인에 성공했습니다.", loginMemberResponse), HttpStatus.OK);
    }

    // JWT 토큰 재발급
    @PostMapping("/token-reissue")
    public ResponseEntity<HttpResponse<LoginMemberResponse>> refreshTokenReissue(@RequestBody RefreshTokenRequest request) {
        LoginMemberResponse loginMemberResponse = memberService.refreshTokenReissue(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "토큰을 재발급 합니다.", loginMemberResponse), HttpStatus.OK);
    }

    // 회원 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<HttpResponse<Void>> logout(@RequestBody LogoutRequest request) {
        memberService.logout(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "로그아웃에 성공했습니다.", null), HttpStatus.OK);
    }

    // 모든 회원 조회
    @GetMapping("/findAll")
    public ResponseEntity<HttpResponse<List<MemberDto>>> findAllMember() {
        return new ResponseEntity<>(new HttpResponse<>(1, "모든 회원 조회입니다.", memberService.findAllMember()), HttpStatus.OK);
    }

    // 단건 회원 조회(id)

    // 아이디 중복 확인

    // 전화번호 중복 확인

    // 회원 수정

    // 회원 삭제
}
