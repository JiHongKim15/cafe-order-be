package com.cafe.order.adapter.in.web.member.mapper;

import com.cafe.order.adapter.in.web.member.request.MemberCancelWithdrawalRequest;
import com.cafe.order.adapter.in.web.member.request.MemberSignupRequest;
import com.cafe.order.adapter.in.web.member.request.MemberWithdrawRequest;
import com.cafe.order.adapter.in.web.member.response.MemberSignupResponse;
import com.cafe.order.application.port.in.member.command.MemberCancelWithdrawalCommand;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.application.port.in.member.command.MemberWithdrawCommand;
import com.cafe.order.domain.member.model.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberWebMapper {

    public MemberSignupCommand toCommand(MemberSignupRequest request) {
        return new MemberSignupCommand(
            request.name(),
            normalizePhoneNumber(request.phoneNumber()),
            request.gender(),
            request.birthDate()
        );
    }

    public MemberWithdrawCommand toWithdrawCommand(MemberWithdrawRequest request) {
        return new MemberWithdrawCommand(request.memberId());
    }

    public MemberCancelWithdrawalCommand toCancelWithdrawalCommand(MemberCancelWithdrawalRequest request) {
        return new MemberCancelWithdrawalCommand(request.memberId());
    }

    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("-", "");
    }

    public MemberSignupResponse toResponse(Member member) {
        return new MemberSignupResponse(
            member.getId(),
            member.getName(),
            member.getPhoneNumber(),
            member.getGender(),
            member.getBirthDate(),
            member.getStatus(),
            member.getJoinDateTime()
        );
    }
}