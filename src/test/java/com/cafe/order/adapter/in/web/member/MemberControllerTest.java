package com.cafe.order.adapter.in.web.member;

import com.cafe.order.adapter.in.web.member.mapper.MemberWebMapper;
import com.cafe.order.adapter.in.web.member.request.MemberSignupRequest;
import com.cafe.order.adapter.in.web.member.response.MemberSignupResponse;
import com.cafe.order.application.port.in.member.MemberCommandUseCase;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@DisplayName("MemberController API 테스트")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberCommandUseCase memberCommandUseCase;

    @MockitoBean
    private MemberWebMapper memberWebMapper;

    // ========== 회원 가입 API 테스트 ==========
    // Controller는 HTTP 요청/응답 매핑과 예외 처리만 테스트
    // 비즈니스 로직 검증은 Service 테스트에서 수행

    @Test
    @DisplayName("회원 가입 - 정상 요청 시 200 OK 응답")
    void signup_Success() throws Exception {
        // Given
        String name = "홍길동";
        String phoneNumber = "01012345678";
        Gender gender = Gender.MALE;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        MemberSignupRequest request = new MemberSignupRequest(name, phoneNumber, gender, birthDate);
        MemberSignupCommand command = new MemberSignupCommand(name, phoneNumber, gender, birthDate);

        Member member = Member.builder()
                .id(1L)
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthDate(birthDate)
                .status(MemberStatus.ACTIVE)
                .joinDateTime(LocalDateTime.now())
                .build();

        MemberSignupResponse response = new MemberSignupResponse(
                member.getId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getGender(),
                member.getBirthDate(),
                member.getStatus(),
                member.getJoinDateTime()
        );

        given(memberWebMapper.toCommand(any(MemberSignupRequest.class))).willReturn(command);
        given(memberCommandUseCase.signup(any(MemberSignupCommand.class))).willReturn(member);
        given(memberWebMapper.toResponse(any(Member.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.phoneNumber").value(phoneNumber));
    }

    @Test
    @DisplayName("회원 가입 - 비즈니스 예외 발생 시 400 에러")
    void signup_Fail_BizException() throws Exception {
        // Given
        MemberSignupRequest request = new MemberSignupRequest(
                "홍길동", "01012345678", Gender.MALE, LocalDate.of(1990, 1, 1));

        given(memberWebMapper.toCommand(any(MemberSignupRequest.class)))
                .willReturn(new MemberSignupCommand("홍길동", "01012345678", Gender.MALE, LocalDate.of(1990, 1, 1)));
        given(memberCommandUseCase.signup(any(MemberSignupCommand.class)))
                .willThrow(new com.cafe.order.common.BizException(
                        com.cafe.order.common.ErrorCode.INVALID_REQUEST));

        // When & Then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("E002"));
    }

    // ========== 회원 탈퇴 API 테스트 ==========

    @Test
    @DisplayName("회원 탈퇴 - 정상 요청 시 200 OK 응답")
    void withdraw_Success() throws Exception {
        // Given
        Long memberId = 1L;
        com.cafe.order.adapter.in.web.member.request.MemberWithdrawRequest request = 
                new com.cafe.order.adapter.in.web.member.request.MemberWithdrawRequest(memberId);

        // When & Then
        mockMvc.perform(patch("/api/members/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("회원 탈퇴 - 비즈니스 예외 발생 시 400 에러")
    void withdraw_Fail_BizException() throws Exception {
        // Given
        Long memberId = 999L;
        com.cafe.order.adapter.in.web.member.request.MemberWithdrawRequest request = 
                new com.cafe.order.adapter.in.web.member.request.MemberWithdrawRequest(memberId);

        org.mockito.BDDMockito.willThrow(new com.cafe.order.common.BizException(
                        com.cafe.order.common.ErrorCode.MEMBER_NOT_FOUND))
                .given(memberCommandUseCase).withdraw(any());

        // When & Then
        mockMvc.perform(patch("/api/members/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("M001"));
    }

    // ========== 탈퇴 철회 API 테스트 ==========

    @Test
    @DisplayName("탈퇴 철회 - 정상 요청 시 200 OK 응답")
    void cancelWithdrawal_Success() throws Exception {
        // Given
        Long memberId = 1L;
        com.cafe.order.adapter.in.web.member.request.MemberCancelWithdrawalRequest request = 
                new com.cafe.order.adapter.in.web.member.request.MemberCancelWithdrawalRequest(memberId);

        // When & Then
        mockMvc.perform(patch("/api/members/cancel-withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("탈퇴 철회 - 비즈니스 예외 발생 시 400 에러")
    void cancelWithdrawal_Fail_BizException() throws Exception {
        // Given
        Long memberId = 1L;
        com.cafe.order.adapter.in.web.member.request.MemberCancelWithdrawalRequest request = 
                new com.cafe.order.adapter.in.web.member.request.MemberCancelWithdrawalRequest(memberId);

        org.mockito.BDDMockito.willThrow(new com.cafe.order.common.BizException(
                        com.cafe.order.common.ErrorCode.WITHDRAWAL_PERIOD_EXPIRED))
                .given(memberCommandUseCase).cancelWithdrawal(any());

        // When & Then
        mockMvc.perform(patch("/api/members/cancel-withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("M005"));
    }
}
