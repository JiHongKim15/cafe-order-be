package com.cafe.order.application.service.member;

import com.cafe.order.application.port.in.member.MemberUseCase;
import com.cafe.order.application.port.in.member.command.MemberCancelWithdrawalCommand;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.application.port.in.member.command.MemberWithdrawCommand;
import com.cafe.order.application.port.out.member.MemberPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.service.MemberDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class MemberService implements MemberUseCase {

    private final MemberPort memberPort;
    private final MemberDomainService memberDomainService;

    @Override
    public Member signup(MemberSignupCommand command) {
        memberDomainService.validateMemberRequirements(
            command.name(),
            command.phoneNumber(),
            command.gender(),
            command.birthDate()
        );

        validateDuplicatePhoneNumber(command.phoneNumber());

        Member newMember = Member.createNewMember(
                command.name(),
                command.phoneNumber(),
                command.gender(),
                command.birthDate()
        );

        return memberPort.save(newMember);
    }
    
    @Override
    public void withdraw(MemberWithdrawCommand command) {
        Member member = memberPort.findById(command.memberId())
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));

        memberDomainService.validateWithdrawalRequirements(member);

        member.withdraw();

        memberPort.save(member);
    }

    @Override
    public void cancelWithdrawal(MemberCancelWithdrawalCommand command) {
        Member member = memberPort.findById(command.memberId())
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));

        memberDomainService.validateCancelWithdrawalRequirements(member);

        member.cancelWithdrawal();

        memberPort.save(member);
    }

    @Override
    public Member findById(Long memberId) {
        return memberPort.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDuplicatePhoneNumber(String phoneNumber) {
        if (memberPort.existsByPhoneNumber(phoneNumber)) {
            throw new BizException(ErrorCode.INVALID_REQUEST, "이미 가입된 전화번호입니다.");
        }
    }
}