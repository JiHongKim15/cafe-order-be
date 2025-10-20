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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberPort memberPort;

    @Mock
    private MemberDomainService memberDomainService;

    // ========== 회원 가입 시나리오 ==========

    @Test
    @DisplayName("시나리오: 신규 고객이 정상적으로 회원가입")
    void signup_Success() {
        // Given: 유효한 회원 가입 정보
        MemberSignupCommand command = new MemberSignupCommand(
                "홍길동",
                "01012345678",
                Gender.MALE,
                LocalDate.of(1990, 1, 1)
        );

        Member savedMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        willDoNothing().given(memberDomainService).validateMemberRequirements(
                command.name(),
                command.phoneNumber(),
                command.gender(),
                command.birthDate()
        );
        given(memberPort.existsByPhoneNumber(command.phoneNumber())).willReturn(false);
        given(memberPort.save(any(Member.class))).willReturn(savedMember);

        // When: 회원가입 진행
        Member result = memberService.signup(command);

        // Then: 회원이 정상적으로 생성됨
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);

        then(memberDomainService).should().validateMemberRequirements(
                command.name(),
                command.phoneNumber(),
                command.gender(),
                command.birthDate()
        );
        then(memberPort).should().existsByPhoneNumber(command.phoneNumber());
        then(memberPort).should().save(any(Member.class));
    }

    @Test
    @DisplayName("시나리오: 이미 가입된 전화번호로 재가입 시도하면 실패")
    void signup_Fail_DuplicatePhoneNumber() {
        // Given: 이미 존재하는 전화번호
        MemberSignupCommand command = new MemberSignupCommand(
                "김철수",
                "01012345678",
                Gender.MALE,
                LocalDate.of(1992, 5, 15)
        );

        willDoNothing().given(memberDomainService).validateMemberRequirements(
                anyString(), anyString(), any(Gender.class), any(LocalDate.class)
        );
        given(memberPort.existsByPhoneNumber(command.phoneNumber())).willReturn(true);

        // When & Then: 중복 전화번호 예외 발생
        assertThatThrownBy(() -> memberService.signup(command))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이미 가입된 전화번호입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);

        then(memberPort).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("시나리오: 잘못된 전화번호 형식으로 가입 시도하면 실패")
    void signup_Fail_InvalidPhoneNumberFormat() {
        // Given: 잘못된 전화번호 형식
        MemberSignupCommand command = new MemberSignupCommand(
                "이영희",
                "010-1234-5678",  // 하이픈 포함
                Gender.FEMALE,
                LocalDate.of(1995, 3, 20)
        );

        willThrow(new BizException(ErrorCode.INVALID_REQUEST, "전화번호 형식이 올바르지 않습니다."))
                .given(memberDomainService).validateMemberRequirements(
                        command.name(),
                        command.phoneNumber(),
                        command.gender(),
                        command.birthDate()
                );

        // When & Then: 도메인 검증 실패
        assertThatThrownBy(() -> memberService.signup(command))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("전화번호 형식이 올바르지 않습니다.");

        then(memberPort).should(never()).existsByPhoneNumber(anyString());
        then(memberPort).should(never()).save(any(Member.class));
    }

    // ========== 회원 탈퇴 시나리오 ==========

    @Test
    @DisplayName("시나리오: 활성 회원이 정상적으로 탈퇴")
    void withdraw_Success() {
        // Given: 활성 상태의 회원
        Long memberId = 1L;
        MemberWithdrawCommand command = new MemberWithdrawCommand(memberId);

        Member activeMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(activeMember));
        willDoNothing().given(memberDomainService).validateWithdrawalRequirements(activeMember);
        given(memberPort.save(any(Member.class))).willReturn(activeMember);

        // When: 탈퇴 진행
        memberService.withdraw(command);

        // Then: 회원 상태가 WITHDRAWN으로 변경됨
        then(memberPort).should().findById(memberId);
        then(memberDomainService).should().validateWithdrawalRequirements(activeMember);
        then(memberPort).should().save(activeMember);
    }

    @Test
    @DisplayName("시나리오: 존재하지 않는 회원 ID로 탈퇴 시도하면 실패")
    void withdraw_Fail_MemberNotFound() {
        // Given: 존재하지 않는 회원 ID
        Long nonExistentMemberId = 999L;
        MemberWithdrawCommand command = new MemberWithdrawCommand(nonExistentMemberId);

        given(memberPort.findById(nonExistentMemberId)).willReturn(Optional.empty());

        // When & Then: 회원을 찾을 수 없음
        assertThatThrownBy(() -> memberService.withdraw(command))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

        then(memberDomainService).should(never()).validateWithdrawalRequirements(any(Member.class));
        then(memberPort).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("시나리오: 이미 탈퇴한 회원이 다시 탈퇴 시도하면 실패")
    void withdraw_Fail_AlreadyWithdrawn() {
        // Given: 이미 탈퇴한 회원
        Long memberId = 1L;
        MemberWithdrawCommand command = new MemberWithdrawCommand(memberId);

        Member withdrawnMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(10))
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(withdrawnMember));
        willThrow(new BizException(ErrorCode.MEMBER_ALREADY_WITHDRAWN))
                .given(memberDomainService).validateWithdrawalRequirements(withdrawnMember);

        // When & Then: 이미 탈퇴한 회원
        assertThatThrownBy(() -> memberService.withdraw(command))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_ALREADY_WITHDRAWN);

        then(memberPort).should(never()).save(any(Member.class));
    }

    // ========== 탈퇴 철회 시나리오 ==========

    @Test
    @DisplayName("시나리오: 탈퇴 후 7일 이내에 탈퇴 철회")
    void cancelWithdrawal_Success() {
        // Given: 7일 전 탈퇴한 회원
        Long memberId = 1L;
        MemberCancelWithdrawalCommand command = new MemberCancelWithdrawalCommand(memberId);

        Member withdrawnMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(7))
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(withdrawnMember));
        willDoNothing().given(memberDomainService).validateCancelWithdrawalRequirements(withdrawnMember);
        given(memberPort.save(any(Member.class))).willReturn(withdrawnMember);

        // When: 탈퇴 철회 진행
        memberService.cancelWithdrawal(command);

        // Then: 회원 상태가 ACTIVE로 복구됨
        then(memberPort).should().findById(memberId);
        then(memberDomainService).should().validateCancelWithdrawalRequirements(withdrawnMember);
        then(memberPort).should().save(withdrawnMember);
    }

    @Test
    @DisplayName("시나리오: 탈퇴 후 31일 지나서 철회 시도하면 실패")
    void cancelWithdrawal_Fail_ExceededCancellationPeriod() {
        // Given: 31일 전 탈퇴한 회원
        Long memberId = 1L;
        MemberCancelWithdrawalCommand command = new MemberCancelWithdrawalCommand(memberId);

        Member withdrawnMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(31))
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(withdrawnMember));
        willThrow(new BizException(ErrorCode.INVALID_REQUEST, "탈퇴 철회 가능 기간(30일)이 지났습니다."))
                .given(memberDomainService).validateCancelWithdrawalRequirements(withdrawnMember);

        // When & Then: 철회 기간 초과
        assertThatThrownBy(() -> memberService.cancelWithdrawal(command))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("탈퇴 철회 가능 기간(30일)이 지났습니다.");

        then(memberPort).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("시나리오: 활성 회원이 탈퇴 철회를 시도하면 실패")
    void cancelWithdrawal_Fail_ActiveMember() {
        // Given: 활성 상태의 회원
        Long memberId = 1L;
        MemberCancelWithdrawalCommand command = new MemberCancelWithdrawalCommand(memberId);

        Member activeMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(activeMember));
        willThrow(new BizException(ErrorCode.INVALID_REQUEST, "탈퇴 상태가 아닌 회원입니다."))
                .given(memberDomainService).validateCancelWithdrawalRequirements(activeMember);

        // When & Then: 탈퇴 상태가 아님
        assertThatThrownBy(() -> memberService.cancelWithdrawal(command))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("탈퇴 상태가 아닌 회원입니다.");

        then(memberPort).should(never()).save(any(Member.class));
    }

    // ========== 회원 조회 시나리오 ==========

    @Test
    @DisplayName("시나리오: 회원 ID로 회원 정보 조회 성공")
    void findById_Success() {
        // Given: 존재하는 회원 ID
        Long memberId = 1L;
        Member existingMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        given(memberPort.findById(memberId)).willReturn(Optional.of(existingMember));

        // When: 회원 조회
        Member result = memberService.findById(memberId);

        // Then: 회원 정보 반환
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(memberId);
        assertThat(result.getName()).isEqualTo("홍길동");

        then(memberPort).should().findById(memberId);
    }

    @Test
    @DisplayName("시나리오: 존재하지 않는 회원 ID로 조회하면 실패")
    void findById_Fail_MemberNotFound() {
        // Given: 존재하지 않는 회원 ID
        Long nonExistentMemberId = 999L;

        given(memberPort.findById(nonExistentMemberId)).willReturn(Optional.empty());

        // When & Then: 회원을 찾을 수 없음
        assertThatThrownBy(() -> memberService.findById(nonExistentMemberId))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

        then(memberPort).should().findById(nonExistentMemberId);
    }
}