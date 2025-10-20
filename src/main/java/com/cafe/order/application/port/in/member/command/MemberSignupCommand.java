package com.cafe.order.application.port.in.member.command;

import com.cafe.order.domain.member.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MemberSignupCommand(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 10, message = "이름은 2-10자 이내여야 합니다.")
    String name,

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10-11자리 숫자여야 합니다.")
    String phoneNumber,

    @NotNull(message = "성별은 필수입니다.")
    Gender gender,

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    LocalDate birthDate
) {}