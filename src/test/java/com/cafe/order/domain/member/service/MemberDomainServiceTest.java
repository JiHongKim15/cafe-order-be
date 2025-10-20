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


@DisplayName("MemberDomainService 도메인 규칙 테스트")
class MemberDomainServiceTest {

    private MemberDomainService memberDomainService;

    @BeforeEach
    void setUp() {
        memberDomainService = new MemberDomainService();
    }

    // ========== 회원 가입 요구사항 검증 시나리오 ==========

    @Test
    @DisplayName("시나리오: 올바른 정보로 회원가입하면 검증 통과")
    void validateMemberRequirements_Success_WithValidInformation() {
        // Given: 유효한 회원 정보
        String validName = "홍길동";
        String validPhoneNumber = "01012345678";
        Gender gender = Gender.MALE;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // When & Then: 검증 통과
        assertThatCode(() ->
                memberDomainService.validateMemberRequirements(validName, validPhoneNumber, gender, birthDate))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 전화번호 형식이 잘못되면 실패 - 010으로 시작하지 않음")
    void validateMemberRequirements_Fail_InvalidPhoneNumberPrefix() {
        // Given: 잘못된 전화번호 (011로 시작하지만 하이픈 있음)
        String invalidPhoneNumber = "011-1234-5678";
        String validName = "홍길동";

        // When & Then: 전화번호 형식 오류
        assertThatThrownBy(() ->
                memberDomainService.validateMemberRequirements(
                        validName,
                        invalidPhoneNumber,
                        Gender.MALE,
                        LocalDate.of(1990, 1, 1)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("전화번호 형식이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("시나리오: 전화번호에 하이픈이 포함되어 있으면 실패")
    void validateMemberRequirements_Fail_PhoneNumberWithHyphen() {
        // Given: 하이픈이 포함된 전화번호
        String phoneWithHyphen = "010-1234-5678";
        String validName = "홍길동";

        // When & Then: 전화번호 형식 오류
        assertThatThrownBy(() ->
                memberDomainService.validateMemberRequirements(
                        validName,
                        phoneWithHyphen,
                        Gender.FEMALE,
                        LocalDate.of(1995, 5, 15)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("전화번호 형식이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("시나리오: 이름이 너무 짧으면 실패 - 1자")
    void validateMemberRequirements_Fail_NameTooShort() {
        // Given: 1자 이름
        String shortName = "김";
        String validPhoneNumber = "01012345678";

        // When & Then: 이름 길이 오류
        assertThatThrownBy(() ->
                memberDomainService.validateMemberRequirements(
                        shortName,
                        validPhoneNumber,
                        Gender.MALE,
                        LocalDate.of(2000, 1, 1)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이름은 2-10자 이내여야 합니다.");
    }

    @Test
    @DisplayName("시나리오: 이름이 너무 길면 실패 - 11자")
    void validateMemberRequirements_Fail_NameTooLong() {
        // Given: 11자 이름
        String longName = "가나다라마바사아자차카"; // 11자
        String validPhoneNumber = "01012345678";

        // When & Then: 이름 길이 오류
        assertThatThrownBy(() ->
                memberDomainService.validateMemberRequirements(
                        longName,
                        validPhoneNumber,
                        Gender.FEMALE,
                        LocalDate.of(1985, 12, 25)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이름은 2-10자 이내여야 합니다.");
    }

    @Test
    @DisplayName("시나리오: 경계값 - 이름 2자는 통과")
    void validateMemberRequirements_Success_NameMinimumLength() {
        // Given: 최소 길이 이름 (2자)
        String minimumName = "홍길";
        String validPhoneNumber = "01012345678";

        // When & Then: 검증 통과
        assertThatCode(() ->
                memberDomainService.validateMemberRequirements(
                        minimumName,
                        validPhoneNumber,
                        Gender.MALE,
                        LocalDate.of(1990, 1, 1)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 경계값 - 이름 10자는 통과")
    void validateMemberRequirements_Success_NameMaximumLength() {
        // Given: 최대 길이 이름 (10자)
        String maximumName = "가나다라마바사아자차"; // 10자
        String validPhoneNumber = "01012345678";

        // When & Then: 검증 통과
        assertThatCode(() ->
                memberDomainService.validateMemberRequirements(
                        maximumName,
                        validPhoneNumber,
                        Gender.FEMALE,
                        LocalDate.of(1995, 6, 15)))
                .doesNotThrowAnyException();
    }

    // ========== 회원 탈퇴 요구사항 검증 시나리오 ==========

    @Test
    @DisplayName("시나리오: 활성 회원이 탈퇴하면 검증 통과")
    void validateWithdrawalRequirements_Success_ActiveMemberWithdraws() {
        // Given: 활성 상태 회원
        Member activeMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        // When & Then: 탈퇴 검증 통과
        assertThatCode(() -> memberDomainService.validateWithdrawalRequirements(activeMember))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 이미 탈퇴한 회원이 다시 탈퇴 시도하면 실패")
    void validateWithdrawalRequirements_Fail_AlreadyWithdrawnMember() {
        // Given: 이미 탈퇴한 회원
        Member withdrawnMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(LocalDateTime.now().minusDays(10))
                .build();

        // When & Then: 이미 탈퇴한 회원은 다시 탈퇴 불가
        assertThatThrownBy(() -> memberDomainService.validateWithdrawalRequirements(withdrawnMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
    }

    // ========== 탈퇴 철회 요구사항 검증 시나리오 ==========

    @Test
    @DisplayName("시나리오: 탈퇴 후 7일 이내에 철회하면 검증 통과")
    void validateCancelWithdrawalRequirements_Success_WithinSevenDays() {
        // Given: 7일 전 탈퇴한 회원
        LocalDateTime withdrawalDateTime = LocalDateTime.now().minusDays(7);
        Member withdrawnMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(withdrawalDateTime)
                .build();

        // When & Then: 30일 이내이므로 철회 가능
        assertThatCode(() -> memberDomainService.validateCancelWithdrawalRequirements(withdrawnMember))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 탈퇴 후 29일째에 철회하면 검증 통과")
    void validateCancelWithdrawalRequirements_Success_Day29() {
        // Given: 29일 전 탈퇴한 회원
        LocalDateTime withdrawalDateTime = LocalDateTime.now().minusDays(29);
        Member withdrawnMember = Member.builder()
                .id(1L)
                .name("김철수")
                .phoneNumber("01087654321")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1992, 3, 10))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(withdrawalDateTime)
                .build();

        // When & Then: 30일 이내이므로 철회 가능
        assertThatCode(() -> memberDomainService.validateCancelWithdrawalRequirements(withdrawnMember))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 탈퇴 후 29일과 23시간 - 30일 이내이므로 철회 가능")
    void validateCancelWithdrawalRequirements_Success_Within30Days() {
        // Given: 29일 23시간 전 탈퇴한 회원 (확실히 30일 이내)
        LocalDateTime withdrawalDateTime = LocalDateTime.now().minusDays(29).minusHours(23);
        Member withdrawnMember = Member.builder()
                .id(1L)
                .name("이영희")
                .phoneNumber("01011112222")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1988, 8, 20))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(withdrawalDateTime)
                .build();

        // When & Then: 30일 이내이므로 철회 가능
        assertThatCode(() -> memberDomainService.validateCancelWithdrawalRequirements(withdrawnMember))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시나리오: 탈퇴 후 31일이 지나면 철회 불가")
    void validateCancelWithdrawalRequirements_Fail_After30Days() {
        // Given: 31일 + 1시간 전 탈퇴한 회원 (확실히 30일 초과)
        LocalDateTime withdrawalDateTime = LocalDateTime.now().minusDays(31).minusHours(1);
        Member withdrawnMember = Member.builder()
                .id(1L)
                .name("박지성")
                .phoneNumber("01033334444")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1985, 5, 5))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(withdrawalDateTime)
                .build();

        // When & Then: 30일이 지나면 철회 불가
        assertThatThrownBy(() -> memberDomainService.validateCancelWithdrawalRequirements(withdrawnMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WITHDRAWAL_PERIOD_EXPIRED);
    }

    @Test
    @DisplayName("시나리오: 활성 회원이 탈퇴 철회를 시도하면 실패")
    void validateCancelWithdrawalRequirements_Fail_ActiveMemberTriesToCancel() {
        // Given: 활성 상태 회원 (탈퇴하지 않음)
        Member activeMember = Member.builder()
                .id(1L)
                .name("홍길동")
                .phoneNumber("01012345678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(MemberStatus.ACTIVE)
                .build();

        // When & Then: 탈퇴 상태가 아니면 철회 불가
        assertThatThrownBy(() -> memberDomainService.validateCancelWithdrawalRequirements(activeMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_WITHDRAWN);
    }

    @Test
    @DisplayName("시나리오: 탈퇴 일시 정보가 없는 회원이 철회 시도하면 실패")
    void validateCancelWithdrawalRequirements_Fail_NoWithdrawalDateTime() {
        // Given: 탈퇴 상태이지만 탈퇴 일시가 없는 회원 (데이터 무결성 문제)
        Member brokenMember = Member.builder()
                .id(1L)
                .name("테스트")
                .phoneNumber("01099998888")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1995, 12, 31))
                .status(MemberStatus.WITHDRAWN)
                .withdrawalDateTime(null)  // 탈퇴 일시 없음
                .build();

        // When & Then: 탈퇴 일시 정보가 없으면 철회 불가
        assertThatThrownBy(() -> memberDomainService.validateCancelWithdrawalRequirements(brokenMember))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WITHDRAWAL_PERIOD_NOT_EXIST);
    }
}
