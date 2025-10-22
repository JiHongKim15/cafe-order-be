# Cafe Order Backend

**헥사고날 아키텍처**와 **도메인 주도 설계(DDD)**를 적용한 카페 주문 시스템 백엔드 프로젝트입니다.

## 📋 목차
- [프로젝트 개요](#-프로젝트-개요)
- [아키텍처 설계](#-아키텍처-설계)
- [도메인 모델 설계](#-도메인-모델-설계)
- [핵심 설계 결정사항](#-핵심-설계-결정사항)
- [문제해결 전략](#-문제해결-전략)
- [API 명세](#-api-명세)
- [프로젝트 실행 방법](#-프로젝트-실행-방법)

---

## 프로젝트 개요

이 프로젝트는 **실무 수준의 설계 원칙과 패턴**을 적용하여 구현한 카페 주문 시스템입니다.
단순한 CRUD를 넘어서 **확장 가능하고 유지보수가 용이한 구조**를 목표로 합니다.

### 개발 방향성과 의도

#### 1. **아키텍처 중심 설계**
- 헥사고날 아키텍처를 통한 의존성 역전과 테스트 용이성 확보
- 도메인 주도 설계(DDD)를 통한 비즈니스 로직의 명확한 표현
- CQRS 패턴을 통한 Command와 Query의 명확한 분리

#### 2. **설계 원칙**
- Clean Code 기반의 단일 책임 원칙 준수
- Domain Service를 통한 복잡한 비즈니스 로직 분리

#### 3. **확장성과 유지보수성**
- Port-Adapter 패턴을 통한 외부 시스템과의 느슨한 결합

### 구현 기능
- **회원 관리**: 회원 가입, 탈퇴, 탈퇴 철회 (30일 이내)
- **주문 관리**: 주문 생성, 주문 취소
- **결제 관리**: 외부 결제 API 연동, 결제 취소
- **상품 관리**: 상품 조회 및 검증

---

## 🏗 아키텍처 설계

### 헥사고날 아키텍처 (Ports and Adapters)

```
┌─────────────────────────────────────────────────────────┐
│                     Adapter (in)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐      │
│  │   REST API  │  │   Request   │  │   Response  │      │
│  │ Controllers │──│   Mapper    │──│   Mapper    │      │
│  └─────────────┘  └─────────────┘  └─────────────┘      │
└───────────────────────┬─────────────────────────────────┘
                        │ Inbound Port
┌───────────────────────▼─────────────────────────────────┐
│                   Application Layer                     │
│  ┌──────────────────────────────────────────────────┐   │
│  │            Use Case Interface                    │   │
│  │   (MemberCommandUseCase, OrderCommandUseCase)    │   │
│  └────────────────────┬─────────────────────────────┘   │
│  ┌────────────────────▼─────────────────────────────┐   │
│  │             Application Service                  │   │
│  │          (트랜잭션 관리, 유즈케이스 조율)               │   │
│  └──────────────────────────────────────────────────┘   │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                    Domain Layer                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │           Domain Model                           │   │
│  │   (Member, Order, OrderLine, Payment, Product)   │   │
│  └────────────────────┬─────────────────────────────┘   │
│  ┌────────────────────▼─────────────────────────────┐   │
│  │          Domain Service                          │   │
│  │    (복잡한 비즈니스 로직, 도메인 검증)                   │   │
│  └──────────────────────────────────────────────────┘   │
└───────────────────────┬─────────────────────────────────┘
                        │ Outbound Port
┌───────────────────────▼─────────────────────────────────┐
│                   Adapter (out)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐      │
│  │ Persistence │  │  External   │  │     JPA     │      │    
│  │   Adapter   │  │   Payment   │  │   Entity    │      │
│  │             │  │   Adapter   │  │             │      │
│  └─────────────┘  └─────────────┘  └─────────────┘      │
└─────────────────────────────────────────────────────────┘
```

### 계층별 역할

#### 1. Adapter Layer (Infrastructure)
- **Inbound Adapter**: REST API Controller, Request/Response Mapper
- **Outbound Adapter**: JPA Repository, External API Client
- **역할**: 외부 세계와 애플리케이션의 인터페이스

#### 2. Application Layer
- **Use Case Interface**: 애플리케이스의 기능 정의 (Port)
- **Application Service**: 유즈케이스 구현
- **역할**: 도메인 객체 조율, 비즈니스 플로우 제어

#### 3. Domain Layer
- **Domain Model**: 핵심 비즈니스 개념
- **Domain Service**: 도메인에 관련된 서비스 구현
- **역할**: 비즈니스 규칙과 정책 표현

---

## 도메인 모델 설계

### Member (회원)

**핵심 비즈니스 규칙**:
- 탈퇴 후 30일 이내 철회 가능
- 탈퇴한 회원은 주문 불가
- 전화번호 중복 불가


### Order (주문)

**핵심 비즈니스 규칙**:
- 주문은 결제 성공 후 생성
- 이미 취소된 주문은 재취소 불가
- 활성화된 회원만 주문 가능


### OrderLine (주문 상품)

**설계 이유**:
- 상품 ID와 수량을 명확하게 분리
- 주문에 따른 상품 관리


### Product (상품)

**핵심 비즈니스 규칙**:
- 상품명과 가격 정보 관리
- 주문 시 상품 존재 여부 검증 필요


### Payment (결제)

**핵심 비즈니스 규칙**:
- 결제 성공 저장


---

## 핵심 설계 결정사항

### 1. CQRS 패턴 적용

**문제**: 하나의 UseCase가 너무 많은 책임을 가짐
**해결**: Command와 Query 분리

```java
// Command: 데이터 변경 작업
public interface MemberCommandUseCase {
    Member signup(MemberSignupCommand command);
    void withdraw(MemberWithdrawCommand command);
    void cancelWithdrawal(MemberCancelWithdrawalCommand command);
}

// Query: 데이터 조회 작업
public interface MemberQueryUseCase {
    Member findById(Long memberId);
}
```

**장점**:
- 단일 책임 원칙 (SRP) 준수
- 읽기/쓰기 트랜잭션 분리 가능
- 각 UseCase의 역할이 명확


### 2. Domain Service 분리

**복잡한 비즈니스 로직을 Domain Service로 분리**:

```java
@Component
public class MemberDomainService {
    public void validateCancelWithdrawalRequirements(Member member) {
        if (!member.isWithdrawn()) {
            throw new BizException(ErrorCode.MEMBER_NOT_WITHDRAWN);
        }
        
        if (!isWithinCancellationPeriod(member.getWithdrawalDateTime())) {
            throw new BizException(ErrorCode.WITHDRAWAL_PERIOD_EXPIRED);
        }
    }
}
```

**장점**:
- 도메인 로직의 응집도 향상
- 테스트 용이성 증대
- 비즈니스 규칙의 명확한 표현

---

## 문제해결

### 1. Payment API 처리 전략

**외부 결제 API 호출 시 안정성 확보**:

**Resilience4j 설정**:
```yaml
resilience4j:
  timelimiter:
    instances:
      payment:
        timeout-duration: 5s
        cancel-running-future: true
```

**장점**:
- 타임아웃으로 무한 대기 방지
- 외부 API 장애 시 빠른 실패 처리
- 시스템 안정성 향상

### 2. 테스트 전략: 계층별 책임에 맞는 테스트

**계층별 테스트 작성**:

**결정**: **레이어별 책임에 따라 테스트 범위를 차별화**

```
Controller Layer     → HTTP 매핑, 예외 변환만 검증
Application Service  → 비즈니스 플로우의 성공/실패 검증  
Domain Service       → 도메인 규칙의 대표 케이스 검증
```

**구체적 전략**:

#### Controller Test
```java
@Test
@DisplayName("회원가입 성공 시 200 OK 반환")
void signup_Success() {
    // HTTP 상태코드와 응답 구조만 검증
    // 비즈니스 로직은 Service에서 검증됨
}
```
- HTTP 요청/응답 매핑 확인
- 예외 발생 시 적절한 상태코드 반환 확인
- 비즈니스 로직은 테스트하지 않음

#### Application Service Test  
```java
@Test
@DisplayName("회원가입 성공 시 회원 생성 및 저장")
void signup_Success() {
    // 전체 플로우 1개 + 주요 예외 1-2개만 테스트
}
```
- 핵심 비즈니스 플로우의 성공 케이스
- 각 메서드당 1-2개의 대표 실패 케이스
- **세부 검증 로직은 Domain Service에서 검증됨**

#### Domain Service Test
```java
@Test
@DisplayName("전화번호 검증 실패 - 형식 오류")
void validatePhoneNumber_Fail() {
    // 도메인 규칙의 대표적인 성공/실패 케이스
}
```
- 도메인 규칙의 핵심 검증 로직
- 대표 케이스로 도메인 규칙 증명


---

## 🔌 API 명세

### 회원 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/members` | 회원 가입 |
| DELETE | `/api/members/{memberId}` | 회원 탈퇴 |
| POST | `/api/members/{memberId}/cancel-withdrawal` | 탈퇴 철회 |

### 주문 API
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | 주문 생성 |
| POST | `/api/orders/cancel` | 주문 취소 |

---

### Swagger UI 접속
애플리케이션 실행 후 브라우저에서 접속:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 정리

### 헥사고날 아키텍처를 선택한 이유
1. **의존성 역전**: 도메인이 인프라에 의존하지 않음
2. **테스트 용이성**: Mock을 이용한 단위 테스트 작성 쉬움
3. **변경 용이성**: 외부 시스템(DB, API) 변경 시 도메인 영향 없음

### DDD 적용 경험
1. **Aggregate**: Order - OrderLine 관계에서 Order를 Root로 설정
2. **Domain Service**: 여러 엔티티가 관련된 검증 로직 분리

### 설계 시 고민했던 부분

#### 1. **도메인 모델 vs JPA 엔티티 분리**
**고민**: 도메인 모델에 JPA 어노테이션을 직접 넣을지, 별도 엔티티로 분리할지?

**결정**: 완전히 분리하고 Mapper로 변환
```java
// Domain Model - 순수 비즈니스 로직만 포함
@Getter
@Builder
public class Member {
    private Long id;
    private String name;
    // JPA 어노테이션 없음
}

// JPA Entity - 영속성 관련 기술만 포함
@Entity
@Table(name = "members")
public class MemberJpaEntity extends BaseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

**이유**:
- 도메인 레이어가 JPA에 의존하지 않음 (의존성 역전)
- 테스트 시 JPA 없이 도메인 로직만 테스트 가능
- 도메인 모델 변경 시 DB 스키마 영향 최소화
- 헥사고날 아키텍처 원칙 준수

**트레이드오프**:
- Mapper 코드 추가로 보일러플레이트 증가
- 하지만 유지보수성과 테스트 용이성이 더 중요

---

#### 2. **Command 객체를 record로 구현**
**고민**: DTO를 class로 만들지 record로 만들지?

**결정**: Command/Request/Response는 모두 record로 구현
```java
public record CreateOrderCommand(
    @NotNull Long memberId,
    @NotEmpty List<@Valid OrderLineCommand> orderLines
) {}
```

**이유**:
- 불변성 보장 (final 필드)
- 간결한 코드
- Command 객체는 데이터 전달이 목적이므로 record가 적합

---

#### 3. **Mapper 계층 분리**
**고민**: Controller와 Service 사이에 Mapper가 필요한가?

**결정**: 3가지 Mapper 구현
- **WebMapper**: Request/Response ↔ Command/Domain
- **PersistenceMapper**: Domain ↔ JpaEntity  
- **Domain Factory Method**: Command → Domain

**이유**:
- 각 계층이 다른 계층의 DTO에 의존하지 않음
- API 스펙 변경 시 도메인 영향 없음
- DB 스키마 변경 시 도메인 영향 없음
- 명확한 책임 분리
