package com.cafe.order.application.port.in.member.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MemberWithdrawCommand(
    @NotNull(message = "회원 ID는 필수입니다.")
    @Positive(message = "회원 ID는 양수여야 합니다.")
    Long memberId
) {}