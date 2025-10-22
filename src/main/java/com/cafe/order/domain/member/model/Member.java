package com.cafe.order.domain.member.model;

import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class Member {
    private Long id;
    private String name;
    private String phoneNumber;
    private Gender gender;
    private LocalDate birthDate;
    private MemberStatus status;
    private LocalDateTime withdrawalDateTime;
    private LocalDateTime joinDateTime;

    public static Member createNewMember(String name, String phoneNumber, Gender gender, LocalDate birthDate) {
        return Member.builder()
                .id(null)
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthDate(birthDate)
                .status(MemberStatus.ACTIVE)
                .joinDateTime(LocalDateTime.now())
                .build();
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.withdrawalDateTime = LocalDateTime.now();
    }

    public void cancelWithdrawal() {
        this.status = MemberStatus.ACTIVE;
        this.withdrawalDateTime = null;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

}