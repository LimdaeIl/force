package com.dedication.force.controller;

import com.dedication.force.common.HttpResponse;
import com.dedication.force.domain.dto.AddMemberRequest;
import com.dedication.force.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final MemberService memberService;

    // 회원 등록
    @PostMapping("")
    public ResponseEntity<HttpResponse<Void>> addMember(@RequestBody @Valid AddMemberRequest request, BindingResult result) {
        memberService.addMember(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "회원가입에 성공했습니다.", null), HttpStatus.CREATED);
    }

    // 모든 회원 조회

    // 단건 회원 조회(id)

    // 아이디 중복 확인

    // 전화번호 중복 확인

    // 회원 수정

    // 회원 삭제
}
