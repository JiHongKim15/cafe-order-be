package com.cafe.order.application.port.in.member;

import com.cafe.order.domain.member.model.Member;

public interface MemberQueryUseCase {

    Member findById(Long memberId);

}
