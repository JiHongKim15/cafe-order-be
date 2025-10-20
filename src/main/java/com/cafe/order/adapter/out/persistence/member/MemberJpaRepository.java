package com.cafe.order.adapter.out.persistence.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
}