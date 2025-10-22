# API 명세서
## Swagger UI

애플리케이션 실행 후 아래 주소에서 인터랙티브 API 문서를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui/index.html
```

## 공통 응답 형식

### 성공 응답
```json
{
  "success": true,
  "data": { ... },
  "message": null,
  "errorCode": null
}
```

### 실패 응답
```json
{
  "success": false,
  "data": null,
  "message": "회원을 찾을 수 없습니다.",
  "errorCode": "M001"
}
```

---

## 회원 API

### 1. 회원 가입

회원 정보를 등록합니다.

**요청**
```http
POST /api/members/signup
Content-Type: application/json

{
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "gender": "MALE",
  "birthDate": "1990-01-01"
}
```

**요청 필드**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | String | O | 회원 이름 (2-10자) |
| phoneNumber | String | O | 전화번호 (하이픈 포함 가능, 010-XXXX-XXXX 형식) |
| gender | String | O | 성별 (MALE, FEMALE) |
| birthDate | String | O | 생년월일 (yyyy-MM-dd, 과거 날짜) |

**응답 (200 OK)**
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "gender": "MALE",
    "birthDate": "1990-01-01",
    "status": "ACTIVE",
    "joinDateTime": "2025-10-22T10:00:00"
  },
  "message": null,
  "errorCode": null
}
```

**응답 필드**
| 필드 | 타입 | 설명 |
|------|------|------|
| memberId | Long | 회원 ID |
| name | String | 회원 이름 |
| phoneNumber | String | 전화번호 |
| gender | String | 성별 (MALE, FEMALE) |
| birthDate | String | 생년월일 |
| status | String | 회원 상태 (ACTIVE, WITHDRAWN) |
| joinDateTime | String | 가입 일시 |

**에러 코드**
| 코드 | 설명 |
|------|------|
| M001 | 회원을 찾을 수 없습니다 |
| M002 | 이미 탈퇴한 회원입니다 |

---

### 2. 회원 탈퇴

회원의 상태를 WITHDRAWN(탈퇴)으로 변경합니다.

**요청**
```http
PATCH /api/members/withdraw
Content-Type: application/json

{
  "memberId": 1
}
```

**요청 필드**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| memberId | Long | O | 탈퇴할 회원 ID (양수) |

**응답 (200 OK)**
```json
{
  "success": true,
  "data": null,
  "message": null,
  "errorCode": null
}
```

**에러 코드**
| 코드 | 설명 |
|------|------|
| M001 | 회원을 찾을 수 없습니다 |
| M002 | 이미 탈퇴한 회원입니다 |

---

### 3. 회원 탈퇴 철회

탈퇴한 회원의 상태를 다시 ACTIVE로 변경합니다. (탈퇴 후 30일 이내 가능)

**요청**
```http
PATCH /api/members/cancel-withdrawal
Content-Type: application/json

{
  "memberId": 1
}
```

**요청 필드**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| memberId | Long | O | 탈퇴 철회할 회원 ID (양수) |

**응답 (200 OK)**
```json
{
  "success": true,
  "data": null,
  "message": null,
  "errorCode": null
}
```

**에러 코드**
| 코드 | 설명 |
|------|------|
| M001 | 회원을 찾을 수 없습니다 |
| M003 | 탈퇴한 회원이 아닙니다 |
| M004 | 탈퇴 일시 정보가 없습니다 |
| M005 | 탈퇴 철회 기간(30일)이 경과되었습니다 |

---

## 주문 API

### 1. 주문 생성

새로운 주문을 생성하고 결제를 진행합니다.

**요청**
```http
POST /api/orders
Content-Type: application/json

{
  "memberId": 1,
  "orderLines": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**요청 필드**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| memberId | Long | O | 회원 ID (양수) |
| orderLines | Array | O | 주문 상품 목록 (최소 1개) |
| orderLines[].productId | Long | O | 상품 ID (양수) |
| orderLines[].quantity | Integer | O | 주문 수량 (1 이상) |

**응답 (200 OK)**
```json
{
  "success": true,
  "data": {
    "orderId": 1,
    "memberId": 1,
    "orderLines": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ],
    "status": "COMPLETED",
    "paymentId": "550e8400-e29b-41d4-a716-446655440000",
    "orderDateTime": "2025-10-22T10:00:00"
  },
  "message": null,
  "errorCode": null
}
```

**응답 필드**
| 필드 | 타입 | 설명 |
|------|------|------|
| orderId | Long | 주문 ID |
| memberId | Long | 회원 ID |
| orderLines | Array | 주문 상품 목록 |
| orderLines[].productId | Long | 상품 ID |
| orderLines[].quantity | Integer | 주문 수량 |
| status | String | 주문 상태 (COMPLETED, CANCELLED) |
| paymentId | String | 결제 ID (UUID 형식) |
| orderDateTime | String | 주문 일시 |

**에러 코드**
| 코드 | 설명 |
|------|------|
| M001 | 회원을 찾을 수 없습니다 |
| O003 | 주문할 상품이 없습니다 |
| O004 | 활성화된 회원만 주문할 수 있습니다 |
| P001 | 상품을 찾을 수 없습니다 |
| PAY001 | 결제 처리에 실패했습니다 |

---

### 2. 주문 취소

완료된 주문을 취소하고 결제를 취소합니다.

**요청**
```http
PATCH /api/orders/cancel
Content-Type: application/json

{
  "orderId": 1
}
```

**요청 필드**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| orderId | Long | O | 취소할 주문 ID (양수) |

**응답 (200 OK)**
```json
{
  "success": true,
  "data": null,
  "message": null,
  "errorCode": null
}
```

**에러 코드**
| 코드 | 설명 |
|------|------|
| O001 | 주문을 찾을 수 없습니다 |
| O002 | 이미 취소된 주문입니다 |
| PAY000 | 결제 정보를 찾을 수 없습니다 |
| PAY001 | 결제 처리에 실패했습니다 |

---

## 에러 코드 전체 목록

### 공통 에러 (E)
| 코드 | 설명 |
|------|------|
| E001 | 내부 서버 오류가 발생했습니다 |
| E002 | 잘못된 요청입니다 |

### 회원 관련 (M)
| 코드 | 설명 |
|------|------|
| M001 | 회원을 찾을 수 없습니다 |
| M002 | 이미 탈퇴한 회원입니다 |
| M003 | 탈퇴한 회원이 아닙니다 |
| M004 | 탈퇴 일시 정보가 없습니다 |
| M005 | 탈퇴 철회 기간(30일)이 경과되었습니다 |

### 주문 관련 (O)
| 코드 | 설명 |
|------|------|
| O001 | 주문을 찾을 수 없습니다 |
| O002 | 이미 취소된 주문입니다 |
| O003 | 주문할 상품이 없습니다 |
| O004 | 활성화된 회원만 주문할 수 있습니다 |

### 상품 관련 (P)
| 코드 | 설명 |
|------|------|
| P001 | 상품을 찾을 수 없습니다 |
| P002 | 판매 중단된 상품입니다 |

### 결제 관련 (PAY)
| 코드 | 설명 |
|------|------|
| PAY000 | 결제 정보를 찾을 수 없습니다 |
| PAY001 | 결제 처리에 실패했습니다 |
| PAY002 | 결제가 취소되었습니다 |
