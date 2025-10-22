package com.cafe.order.application.service.member;

import com.cafe.order.application.port.in.member.command.MemberCancelWithdrawalCommand;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.application.port.in.member.command.MemberWithdrawCommand;
import com.cafe.order.application.port.out.member.MemberPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.cafe.order.domain.member.service.MemberDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberCommandService 테스트")
class MemberCommandServiceTest {

    @InjectMocks
    private MemberCommandService memberCommandService;

    @Mock
    private MemberPort memberPort;

    @Mock
    private MemberDomainService memberDomainService;

    // ========== 회원 가입 ==========

    @Test
    @DisplayName("회원 가입 성공")
    void signup_Success() {
        // Given
        MemberSignupCommand command = new MemberSignupCommand(
                "홍길동", "01012345678", Gender.MALE, LocalDate.of(1990, 1, 1));

        Member savedMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        willDoNothing().given(memberDomainService).validateMemberRequirements(
                command.name(), command.phoneNumber(), command.gender(), command.birthDate());
        given(memberPort.existsByPhoneNumber(command.phoneNumber())).willReturn(false);
        given(memberPort.save(any(Member.class))).willReturn(savedMember);

        // When
        Member result = memberCommandService.signup(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("회원 가입 실패 - 중복된 전화번호")
    void signup_Fail_DuplicatePhoneNumber() {
        // Given
        MemberSignupCommand command = new MemberSignupCommand(
                "홍길동", "01012345678", Gender.MALE, LocalDate.of(1990, 1, 1));

        willDoNothing().given(memberDomainService).validateMemberRequirements(
                any(), any(), any(), any());
        given(memberPort.existsByPhoneNumber(command.phoneNumber())).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> memberCommandService.signup(command))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이미 가입된 전화번호입니다.");

        then(memberPort).should(never()).save(any(Member.class));
    }

    // ========== 회원 탈퇴 ==========

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw_Success() {
        // Given
        Long memberId = 1L;
        MemberWithdrawCommand command = new MemberWithdrawCommand(memberId);

        Member activeMember = Member.builder()
                .id(memberId)
                .status(MemberStatus.ACTIVE)
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(activeMember));
        willDoNothing().given(memberDomainService).validateWithdrawalRequirements(activeMember);
        given(memberPort.save(any(Member.class))).willReturn(activeMember);

        // When
        memberCommandService.withdraw(command);

        // Then
        then(memberDomainService).should().validateWithdrawalRequirements(activeMember);
        then(memberPort).should().save(activeMember);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원")
    void withdraw_Fail_MemberNotFound() {
        // Given
        Long memberId = 999L;
        MemberWithdrawCommand command = new MemberWithdrawCommand(memberId);

        given(memberPort.findById(memberId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberCommandService.withdraw(command))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

        then(memberPort).should(never()).save(any(Member.class));
    }

    // ========== 탈퇴 철회 ==========

    @Test
    @DisplayName("탈퇴 철회 성공")
    void cancelWithdrawal_Success() {
        // Given
        Long memberId = 1L;
        MemberCancelWithdrawalCommand command = new MemberCancelWithdrawalCommand(memberId);

        Member withdrawnMember = Member.builder()
                .id(memberId)
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(7))
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(withdrawnMember));
        willDoNothing().given(memberDomainService).validateCancelWithdrawalRequirements(withdrawnMember);
        given(memberPort.save(any(Member.class))).willReturn(withdrawnMember);

        // When
        memberCommandService.cancelWithdrawal(command);

        // Then
        then(memberDomainService).should().validateCancelWithdrawalRequirements(withdrawnMember);
        then(memberPort).should().save(withdrawnMember);
    }

    @Test
    @DisplayName("탈퇴 철회 실패 - 철회 기간 만료")
    void cancelWithdrawal_Fail_PeriodExpired() {
        // Given
        Long memberId = 1L;
        MemberCancelWithdrawalCommand command = new MemberCancelWithdrawalCommand(memberId);

        Member withdrawnMember = Member.builder()
                .id(memberId)
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(31))
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(withdrawnMember));
        willThrow(new BizException(ErrorCode.WITHDRAWAL_PERIOD_EXPIRED))
                .given(memberDomainService).validateCancelWithdrawalRequirements(withdrawnMember);

        // When & Then
        assertThatThrownBy(() -> memberCommandService.cancelWithdrawal(command))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WITHDRAWAL_PERIOD_EXPIRED);

        then(memberPort).should(never()).save(any(Member.class));
    }
}
