package com.cafe.order.domain.member.service;

import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MemberDomainService 테스트")
class MemberDomainServiceTest {

    private MemberDomainService memberDomainService;

    @BeforeEach
    void setUp() {
        memberDomainService = new MemberDomainService();
    }

    // ========== 회원 가입 검증 ==========

    @Test
    @DisplayName("회원 가입 검증 성공")
    void validateMemberRequirements_Success() {
        // Given
        String validName = "홍길동";
        String validPhoneNumber = "01012345678";
        Gender gender = Gender.MALE;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // When & Then
        assertThatCode(() ->
                memberDomainService.validateMemberRequirements(validName, validPhoneNumber, gender, birthDate))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("회원 가입 검증 실패 - 잘못된 전화번호 형식")
    void validateMemberRequirements_Fail_InvalidPhoneNumber() {
        // Given
        String invalidPhoneNumber = "010-1234-5678";

        // When & Then
        assertThatThrownBy(() ->
                memberDomainService.validateMemberRequirements(
                        "홍길동", invalidPhoneNumber, Gender.MALE, LocalDate.of(1990, 1, 1)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("전화번호 형식이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("회원 가입 검증 실패 - 이름 길이 (1자)")
    void validateMemberRequirements_Fail_NameTooShort() {
        // Given
        String shortName = "김";

        // When & Then
        assertThatThrownBy(() ->
                memberDomainService.validateMemberRequirements(
                        shortName, "01012345678", Gender.MALE, LocalDate.of(1990, 1, 1)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이름은 2-10자 이내여야 합니다.");
    }

    // ========== 회원 탈퇴 검증 ==========

    @Test
    @DisplayName("회원 탈퇴 검증 성공")
    void validateWithdrawalRequirements_Success() {
        // Given
        Member activeMember = Member.builder()
                .id(1L)
                .status(MemberStatus.ACTIVE)
                .build();

        // When & Then
        assertThatCode(() -> memberDomainService.validateWithdrawalRequirements(activeMember))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("회원 탈퇴 검증 실패 - 이미 탈퇴한 회원")
    void validateWithdrawalRequirements_Fail_AlreadyWithdrawn() {
        // Given
        Member withdrawnMember = Member.builder()
                .id(1L)
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(10))
                .build();

        // When & Then
        assertThatThrownBy(() -> memberDomainService.validateWithdrawalRequirements(withdrawnMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
    }

    // ========== 탈퇴 철회 검증 ==========

    @Test
    @DisplayName("탈퇴 철회 검증 성공 - 7일 이내")
    void validateCancelWithdrawalRequirements_Success() {
        // Given
        Member withdrawnMember = Member.builder()
                .id(1L)
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(7))
                .build();

        // When & Then
        assertThatCode(() -> memberDomainService.validateCancelWithdrawalRequirements(withdrawnMember))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("탈퇴 철회 검증 실패 - 31일 경과")
    void validateCancelWithdrawalRequirements_Fail_PeriodExpired() {
        // Given
        Member withdrawnMember = Member.builder()
                .id(1L)
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(31).minusHours(1))
                .build();

        // When & Then
        assertThatThrownBy(() -> memberDomainService.validateCancelWithdrawalRequirements(withdrawnMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WITHDRAWAL_PERIOD_EXPIRED);
    }

    @Test
    @DisplayName("탈퇴 철회 검증 실패 - 활성 회원")
    void validateCancelWithdrawalRequirements_Fail_ActiveMember() {
        // Given
        Member activeMember = Member.builder()
                .id(1L)
                .status(MemberStatus.ACTIVE)
                .build();

        // When & Then
        assertThatThrownBy(() -> memberDomainService.validateCancelWithdrawalRequirements(activeMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_WITHDRAWN);
    }
}
