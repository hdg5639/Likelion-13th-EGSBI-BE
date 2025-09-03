# Eventory Backend - 맞춤형 행사 추천 서비스

## 📋 프로젝트 개요
Eventory는 마이크로서비스 아키텍처로 구현된 맞춤형 행사 추천 서비스의 백엔드입니다. Spring Boot 3.5.4 기반으로 개발되었으며, 여러 독립적인 서비스로 구성되어 있습니다.

## 🏗️ 아키텍처
### 마이크로서비스 구조
- **Gateway**: API 게이트웨이 및 라우팅
- **Aggregator**: 서비스 간 데이터 통합 및 Swagger 문서 통합
- **Event**: 행사 정보 관리 및 검색
- **Activity**: 사용자 활동 관리 (북마크, 히스토리, 리뷰)
- **AI**: AI 기반 추천 및 코멘트 생성
- **User**: 사용자 관리 및 인증
- **Image**: 이미지 업로드 및 관리

## 🚀 주요 기능

### Event 서비스
- **행사 CRUD**: 행사 등록, 조회, 수정, 삭제
- **풀텍스트 검색**: 행사명, 설명, 주소 대상 MySQL Boolean Mode 검색 (`+토큰*` 형태)
- **카테고리별 조회**: 다양한 카테고리 기반 행사 필터링
- **북마크 조회**: 사용자별 북마크된 행사 목록
- **히스토리 조회**: 사용자 행사 조회 기록
- **인기순 조회**: 북마크 수 기반 인기 행사 순위
- **24시간 내 시작 행사**: 알림용 행사 목록 제공
- **QR 코드 생성**: Google ZXing 라이브러리로 행사 참여용 QR 코드 생성
- **지오코딩**: Google Maps Services API로 주소 ↔ 좌표 변환
- **이미지 관리**: MultipartFile 기반 이미지 업로드 및 처리

### Activity 서비스
- **북마크 시스템**: 
  - 행사 북마크 추가/해제 (토글 방식)
  - 사용자별 북마크 목록 관리
- **조회 히스토리**: 
  - 사용자 행사 조회 기록 관리
  - 48시간 중복 방지 로직 (동일 행사 재조회 시 시간만 갱신)
- **리뷰 시스템**:
  - 행사별 사용자 리뷰 CRUD
  - 별점 평가 (1~5점)
  - 사용자별 평균 평점 계산
  - 행사별 리뷰 목록 조회

### AI 서비스
- **리뷰 요약**: 
  - 행사 리뷰들을 AI로 요약하여 한 줄 평가 생성 (28자 이내)
  - Spring AI + OpenAI 모델 활용
- **맞춤 코멘트**: 사용자 취향 기반 AI 코멘트 생성
- **자연어 처리**: ChatClient를 활용한 텍스트 처리 및 후처리

### User 서비스
- **사용자 인증**:
  - JWT 토큰 기반 로그인/로그아웃
  - 토큰 재발급 (renew) 기능
  - Spring Security + OAuth2 JOSE 지원
- **회원가입 시스템**:
  - 이메일 인증 코드 발송 및 검증
  - 프로필 이미지 업로드 지원 (MultipartFile)
  - 닉네임, 전화번호, 위치 정보 관리
- **사용자 프로필**:
  - 사용자 정보 조회 및 수정
  - 위치 정보 설정/삭제
  - 프로필 이미지 관리
- **이메일 서비스**:
  - Spring Boot Mail 연동
  - 인증 코드 발송
  - 행사 알림 메일 발송

### Image 서비스
- **이미지 업로드**: 행사 포스터, 프로필 사진 등 이미지 파일 관리
- **이미지 처리**: 리사이징, 포맷 변환 등 이미지 최적화
- **파일 저장소**: 클라우드 스토리지 또는 로컬 파일 시스템 연동

### Aggregator 서비스
- **동적 Swagger 통합**: 
  - Eureka에 등록된 서비스들의 API 문서 자동 통합
  - `DynamicSwaggerRegistry`로 런타임 서비스 감지
- **서비스 디스커버리**: 활성화된 마이크로서비스 자동 감지 및 문서화
- **API 게이트웨이 연동**: Gateway를 통한 통합 API 문서 제공

## 🛠️ 기술 스택

### 핵심 프레임워크
- **Spring Boot**: 3.5.4 (마이크로서비스 기반)
- **Spring Cloud**: 2025.0.0 (마이크로서비스 인프라)
- **Spring Security**: 보안 및 JWT 인증
- **Spring Data JPA**: 데이터 액세스 계층
- **QueryDSL**: 5.1.0 (타입 세이프 쿼리)

### 데이터베이스
- **MariaDB**: 3.3.3 (메인 데이터베이스, 풀텍스트 검색 지원)
- **MySQL**: 호환 드라이버 지원
- **H2**: 테스트 환경용 인메모리 DB
- **JPA/Hibernate**: ORM 프레임워크

### 마이크로서비스 인프라
- **Netflix Eureka**: 서비스 디스커버리 및 등록
- **Spring Cloud Gateway**: API 게이트웨이 및 라우팅
- **RestTemplate**: 서비스 간 HTTP 통신
- **LoadBalancer**: 클라이언트 사이드 로드 밸런싱

### AI/ML 및 외부 서비스
- **Spring AI**: 1.0.1 (AI 기능 통합)
- **OpenAI Integration**: ChatClient 기반 텍스트 생성
- **Google ZXing**: 3.5.3 (QR 코드 생성)
- **Google Maps Services**: 2.2.0 (지오코딩)

### API 문서화
- **SpringDoc OpenAPI**: 2.8.9 (Swagger/OpenAPI 3.0)
- **Swagger UI**: API 문서 웹 인터페이스

### 보안 및 인증
- **Spring Security OAuth2 JOSE**: JWT 토큰 처리
- **Password Encoding**: BCrypt 암호화
- **CORS**: Cross-Origin 요청 처리

### 이메일 및 알림
- **Spring Boot Mail**: 이메일 발송 기능
- **JavaMail**: SMTP 프로토콜 지원

### 빌드 및 배포
- **Gradle**: 8.14.3 (빌드 관리 도구)
- **Java**: JDK 17 (LTS)
- **Docker**: 컨테이너화 지원

### 개발 도구
- **Lombok**: 보일러플레이트 코드 자동 생성
- **MapStruct**: 1.5.5 (객체 매핑)
- **Spring Boot DevTools**: 개발 편의 도구

## 📚 API 문서

### Swagger UI 접근
```
http://localhost:8080/swagger-ui/index.html
```

### 주요 API 엔드포인트

#### Event API (`/api/event`)
- `GET /all`: 전체 행사 조회
- `GET /search?q={query}`: 풀텍스트 검색
- `GET /bookmarks`: 북마크 행사 조회
- `GET /histories`: 조회 히스토리
- `GET /popular`: 인기 행사 순위  
- `GET /upcoming`: 24시간 내 시작 행사
- `GET /category/{category}`: 카테고리별 조회
- `POST /`: 행사 등록 (이미지 포함)
- `PUT /{id}`: 행사 수정
- `DELETE /{id}`: 행사 삭제
- `POST /qr/join?eventId={id}`: QR 코드 생성

#### Activity API (`/api/activity`)
- `POST /bookmark/toggle`: 북마크 토글
- `POST /history/add`: 조회 기록 저장  
- `GET /history/list`: 조회 히스토리 목록
- `POST /review`: 리뷰 작성
- `GET /review/eventlist?eventId={id}`: 행사별 리뷰 조회
- `GET /review/rating`: 사용자 평균 평점 조회

#### User API (`/api/user`)
- `POST /signup`: 회원가입 (이미지 포함)
- `POST /login`: 로그인
- `POST /renew`: 토큰 재발급
- `GET /info?userId={id}`: 사용자 정보 조회
- `PUT /update`: 사용자 정보 수정
- `DELETE /delete/location`: 위치 정보 삭제

#### Email API (`/api/user/email`)
- `POST /send/code?email={email}`: 인증 코드 발송
- `POST /verify/code?email={email}&inputCode={code}`: 이메일 인증
- `POST /notify/new/{organizerId}`: 새 행사 알림
- `POST /notify/update/{eventId}`: 행사 수정 알림

#### AI API (`/api/ai`)
- `POST /review-summary`: 리뷰 요약 생성 (28자 이내)
- `POST /comment`: 맞춤 코멘트 생성

## ⚙️ 실행 조건

### 필수 요구사항
- **Java**: JDK 17 이상
- **Gradle**: 7.6.0
- **MariaDB**: 12.0.2



## 🔍 핵심 특징

### 마이크로서비스 패턴
- **서비스 분리**: 각 도메인별 독립적인 서비스 구성
- **통신**: RestTemplate 기반 동기 통신  
- **문서화**: Aggregator를 통한 통합 API 문서 관리
- **확장성**: 서비스별 독립적인 스케일링 가능

### 보안 시스템
- **JWT 인증**: 상태 없는 토큰 기반 인증
- **토큰 갱신**: 만료 전 자동 토큰 재발급  
- **비밀번호**: BCrypt 해싱
- **CORS**: 프론트엔드 연동 지원

### 데이터 처리
- **풀텍스트 검색**: MySQL Boolean Mode로 고성능 검색
- **중복 방지**: 48시간 내 동일 행사 조회 기록 최적화
- **페이지네이션**: Pageable 인터페이스 활용
- **트랜잭션**: `@Transactional` 어노테이션으로 데이터 일관성 보장

### AI 통합
- **Spring AI**: OpenAI 모델과의 원활한 연동
- **텍스트 처리**: 리뷰 요약, 코멘트 생성 등 자연어 처리
- **후처리**: 길이 제한, 포맷팅 등 비즈니스 로직 적용


## 📄 라이선스
This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 지원
문의사항이나 버그 리포트는 Issues 탭을 통해 제출해 주세요.