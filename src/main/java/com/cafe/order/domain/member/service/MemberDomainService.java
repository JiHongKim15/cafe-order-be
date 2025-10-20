package com.cafe.order.domain.member.service;

import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Component
public class MemberDomainService {

    public void validateMemberRequirements(String name, String phoneNumber, Gender gender, LocalDate birthDate) {
        validatePhoneNumberFormat(phoneNumber);
        validateNameFormat(name);
    }

    public void validateWithdrawalRequirements(Member member) {
        if (!member.isActive()) {
            throw new BizException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }
    }

    public void validateCancelWithdrawalRequirements(Member member) {
        if (!member.isWithdrawn()) {
            throw new BizException(ErrorCode.MEMBER_NOT_WITHDRAWN);
        }

        LocalDateTime withdrawalDateTime = member.getWithdrawalDateTime();
        if (withdrawalDateTime == null) {
            throw new BizException(ErrorCode.WITHDRAWAL_PERIOD_NOT_EXIST);
        }

        if (!isWithinCancellationPeriod(withdrawalDateTime)) {
            throw new BizException(ErrorCode.WITHDRAWAL_PERIOD_EXPIRED);
        }
    }

    private boolean isWithinCancellationPeriod(LocalDateTime withdrawalDateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limitDateTime = withdrawalDateTime.plusDays(30);
        return now.isBefore(limitDateTime) || now.isEqual(limitDateTime);
    }

    private void validatePhoneNumberFormat(String phoneNumber) {
        if (!phoneNumber.matches("^01[016789]\\d{7,8}$")) {
            throw new BizException(ErrorCode.INVALID_REQUEST, "전화번호 형식이 올바르지 않습니다.");
        }
    }
    
    private void validateNameFormat(String name) {
        if (name.length() < 2 || name.length() > 10) {
            throw new BizException(ErrorCode.INVALID_REQUEST, "이름은 2-10자 이내여야 합니다.");
        }
    }
}