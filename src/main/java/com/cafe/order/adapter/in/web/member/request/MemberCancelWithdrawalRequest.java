package com.cafe.order.adapter.in.web.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "회원 탈퇴 철회 요청")
public record MemberCancelWithdrawalRequest(
    @Schema(description = "탈퇴 철회할 회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "회원 ID는 필수입니다.")
    @Positive(message = "회원 ID는 양수여야 합니다.")
    Long memberId
) {
}
