package com.cafe.order.application.port.out.member;

import com.cafe.order.domain.member.model.Member;

import java.util.Optional;

public interface MemberPort {
    Member save(Member member);
    Optional<Member> findById(Long memberId);
    boolean existsByPhoneNumber(String phoneNumber);
}