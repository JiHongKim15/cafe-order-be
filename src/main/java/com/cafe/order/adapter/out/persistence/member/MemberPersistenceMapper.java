package com.cafe.order.adapter.out.persistence.member;

import com.cafe.order.adapter.out.persistence.member.MemberJpaEntity;
import com.cafe.order.domain.member.model.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceMapper {
    
    public MemberJpaEntity toEntity(Member member) {
        return MemberJpaEntity.builder()
                .Id(member.getId())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .gender(member.getGender())
                .birthDate(member.getBirthDate())
                .status(member.getStatus())
                .withdrawalDateTime(member.getWithdrawalDateTime())
                .joinDateTime(member.getJoinDateTime())
                .build();
    }
    
    public Member toDomain(MemberJpaEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .gender(entity.getGender())
                .birthDate(entity.getBirthDate())
                .status(entity.getStatus())
                .withdrawalDateTime(entity.getWithdrawalDateTime())
                .joinDateTime(entity.getJoinDateTime())
                .build();
    }
}