package com.cafe.order.adapter.in.web.member.request;

import com.cafe.order.domain.member.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "회원 가입 요청")
public record MemberSignupRequest(
    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 10, message = "이름은 2-10자 이내여야 합니다.")
    String name,

    @Schema(description = "전화번호 (하이픈 포함 가능)", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    String phoneNumber,

    @Schema(description = "성별", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"MALE", "FEMALE"})
    @NotNull(message = "성별은 필수입니다.")
    Gender gender,

    @Schema(description = "생년월일", example = "1990-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    LocalDate birthDate
) {
}