package com.cafe.order.adapter.out.persistence.member;

import com.cafe.order.application.port.out.member.MemberPort;
import com.cafe.order.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {
    
    private final MemberJpaRepository memberJpaRepository;
    private final MemberPersistenceMapper memberPersistenceMapper;
    
    @Override
    public Member save(Member member) {
        MemberJpaEntity entity = memberPersistenceMapper.toEntity(member);
        MemberJpaEntity savedEntity = memberJpaRepository.save(entity);
        return memberPersistenceMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Member> findById(Long memberId) {
        return memberJpaRepository.findById(memberId)
                .map(memberPersistenceMapper::toDomain);
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.existsByPhoneNumber(phoneNumber);
    }
}