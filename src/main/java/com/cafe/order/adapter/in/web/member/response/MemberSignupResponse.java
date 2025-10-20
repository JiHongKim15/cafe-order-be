package com.cafe.order.adapter.in.web.member.response;

import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberSignupResponse(
    Long memberId,
    String name,
    String phoneNumber,
    Gender gender,
    LocalDate birthDate,
    MemberStatus status,
    LocalDateTime joinDateTime
) {
}