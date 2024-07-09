package com.dedication.force.controller;

import com.dedication.force.domain.dto.AddMemberRequest;
import com.dedication.force.domain.entity.Member;
import com.dedication.force.domain.entity.Role;
import com.dedication.force.domain.entity.RoleType;
import com.dedication.force.repository.MemberRepository;
import com.dedication.force.repository.RoleRepository;
import com.dedication.force.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Member] 컨트롤러")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void clean() {
        memberRepository.deleteAll();
    }

    @DisplayName("[Member][POST]: 회원가입 성공")
    @Test
    public void GivenValidMember_WhenSignUp_ExpectedSuccess() throws Exception {
        // given
        AddMemberRequest request = AddMemberRequest.builder()
                .email("spring@naver.com")
                .password("SpringBoot3!")
                .phone("01012341234")
                .build();

        Role role = new Role(RoleType.USER);
        roleRepository.save(role);

        // Expected
        mockMvc.perform(post("/api/v1/auth/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
                .andDo(print());
    }

    @DisplayName("[Member][POST]: 회원가입 실패- 유효성")
    @Test
    public void GivenInValidEmailMember_WhenSignUp_ExpectedFail() throws Exception {
        // given
        AddMemberRequest request = AddMemberRequest.builder()
                .email("spring")
                .password("SpringBoot3")
                .phone("0101234")
                .build();



        // Expected
        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(-1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("유효성 검사 실패"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.password").value("비밀번호: 8~20자 영문 대소문자, 숫자, 특수문자를 조합하여 작성해야 합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phone").value("휴대전화번호: 10~11자 숫자만 입력해주세요."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("이메일: 유효한 이메일 주소를 입력해주세요."))
                .andDo(print());
    }

    @DisplayName("[Member][POST]: 회원가입 실패- null")
    @Test
    public void GivenEmptyMember_WhenSignUp_ThenFail() throws Exception {
        // given
        AddMemberRequest request = AddMemberRequest.builder()
                .email(null)
                .password(null)
                .phone(null)
                .build();


        // Expected
        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(-1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("유효성 검사 실패"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.password").value("비밀번호: 필수 정보입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phone").value("휴대전화번호: 필수 정보입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("이메일: 필수 정보입니다."))
                .andDo(print());
    }

    @DisplayName("[Member][GET]: 모든 회원 조회")
    @Test
    public void GivenMembers_WhenSavedMembers_ThenSuccess() throws Exception {
        // given
        Member member1 = Member.of(
                "spring@naver.com",
                "SpringBoot3!",
                "01012341234"
        );

        Member member2 = Member.of(
                "python@naver.com",
                "python3.12.1",
                "01045674567"
        );

        // when
        memberRepository.save(member1);
        memberRepository.save(member2);

         // then
        mockMvc.perform(get("/api/v1/auth")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("spring@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phone").value("01012341234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].email").value("python@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].phone").value("01045674567"))
                .andDo(print());
    }
}
