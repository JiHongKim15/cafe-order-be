package com.cafe.order.application.port.in.member;

import com.cafe.order.application.port.in.member.command.MemberCancelWithdrawalCommand;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.application.port.in.member.command.MemberWithdrawCommand;
import com.cafe.order.domain.member.model.Member;
import jakarta.validation.Valid;

public interface MemberUseCase {

    Member signup(@Valid MemberSignupCommand command);

    void withdraw(@Valid MemberWithdrawCommand command);

    void cancelWithdrawal(@Valid MemberCancelWithdrawalCommand command);

    Member findById(Long memberId);

}