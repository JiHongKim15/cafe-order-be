package com.cafe.order.adapter.out.persistence.member;

import com.cafe.order.adapter.out.persistence.common.BaseJpaEntity;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;
    
    private LocalDateTime withdrawalDateTime;
    
    @Column(nullable = false)
    private LocalDateTime joinDateTime;
}