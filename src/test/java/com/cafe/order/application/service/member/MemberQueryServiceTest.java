package com.cafe.order.application.service.member;

import com.cafe.order.application.port.out.member.MemberPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
@DisplayName("MemberQueryService 단위 테스트")
class MemberQueryServiceTest {

    @InjectMocks
    private MemberQueryService memberQueryService;

    @Mock
    private MemberPort memberPort;

    // ========== 회원 조회 시나리오 ==========

    @Test
    @DisplayName("회원 ID로 회원 정보 조회 성공")
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
        Member result = memberQueryService.findById(memberId);

        // Then: 회원 정보 반환
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(memberId);
        assertThat(result.getName()).isEqualTo("홍길동");

        then(memberPort).should().findById(memberId);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 조회하면 실패")
    void findById_Fail_MemberNotFound() {
        // Given: 존재하지 않는 회원 ID
        Long nonExistentMemberId = 999L;

        given(memberPort.findById(nonExistentMemberId)).willReturn(Optional.empty());

        // When & Then: 회원을 찾을 수 없음
        assertThatThrownBy(() -> memberQueryService.findById(nonExistentMemberId))
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

        then(memberPort).should().findById(nonExistentMemberId);
    }
}
