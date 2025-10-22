package com.cafe.order.application.service.member;

import com.cafe.order.application.port.in.member.MemberQueryUseCase;
import com.cafe.order.application.port.out.member.MemberPort;
import com.cafe.order.common.BizException;
import com.cafe.order.common.ErrorCode;
import com.cafe.order.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService implements MemberQueryUseCase {

    private final MemberPort memberPort;

    @Override
    public Member findById(Long memberId) {
        return memberPort.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
