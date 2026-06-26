# 📰 News Scraper

네이버 Open API를 활용한 뉴스 검색 & 게시 프로젝트입니다.  
**클린 아키텍처(Hexagonal Architecture)** 패턴을 적용하여 각 레이어의 관심사를 분리했습니다.

---

## 🏗️ 아키텍처

```
src/oop/search/
├── domain/            ← 핵심 도메인 모델 (외부 의존 없음)
├── application/       ← 유스케이스 & 포트 (인터페이스)
├── infrastructure/    ← 외부 API 어댑터 (네이버, GitHub)
└── presentation/      ← 실행 진입점 (CLI, CI/CD)
```

### 의존성 방향

```
presentation → application ← infrastructure
                   ↓
                domain
```

> **domain**은 어떤 레이어에도 의존하지 않으며, **application**의 인터페이스를 통해 infrastructure와 분리됩니다.

---

## 📦 레이어별 구성

### Domain

| 클래스 | 설명 |
|---|---|
| `NewsResult` | 뉴스 항목 Value Object (Java `record`, 불변) |
| `NewsCategory` | 정렬 옵션 enum (`SIM`: 정확도순, `DATE`: 최신순) |
| `NewsPage` | 페이징 정보 포함 검색 결과 래퍼 (`total`, `start`, `display` + 네비게이션 헬퍼) |

### Application

| 클래스 | 설명 |
|---|---|
| `NewsProvider` | 뉴스 검색 포트 (인터페이스) |
| `NewsPublisher` | 뉴스 게시 포트 (인터페이스) |
| `NewsService` | Provider + Publisher를 중개하는 서비스 (합성) |

### Infrastructure

| 클래스 | 설명 |
|---|---|
| `AbstractHttpClient` | HTTP 클라이언트 추상 클래스 (공통 `HttpClient` 인스턴스) |
| `NaverNewsProvider` | 네이버 뉴스 검색 API 호출 구현체 |
| `GitHubNewsPublisher` | GitHub Issues API를 통한 뉴스 게시 구현체 |
| `JsonParser` | 외부 라이브러리 없이 동작하는 경량 재귀 하강 JSON 파서 |

### Presentation

| 클래스 | 설명 |
|---|---|
| `ConsoleNewsApp` | 대화형 CLI 앱 (키워드 입력 → 검색 → 페이지 네비게이션) |
| `ConsoleNewsPublisher` | 콘솔 출력 Publisher |
| `GitHubNewsApp` | CI/CD(GitHub Actions) 전용 진입점 |

---

## ⚙️ 환경 변수

| 변수 | 설명 | 예시 |
|---|---|---|
| `NAVER_CLIENT_ID` | 네이버 Open API Client ID | `4imDxu...` |
| `NAVER_CLIENT_SECRET` | 네이버 Open API Client Secret | `iU9GXb...` |
| `NEWS_CATEGORY` | 정렬 기준 (`SIM` 또는 `DATE`) | `DATE` |
| `NEWS_QUERY` | 검색 키워드 (GitHubNewsApp용) | `오늘의 뉴스` |
| `NEWS_DISPLAY` | 결과 표시 건수 (GitHubNewsApp용) | `10` |
| `GITHUB_TOKEN` | GitHub Personal Access Token | - |
| `GITHUB_REPOSITORY` | 대상 레포지토리 (`owner/repo`) | `potechip26/news-scraper` |

---

## 🚀 실행 방법

### 콘솔 앱 (대화형)
```bash
# 컴파일
javac -encoding UTF-8 -d out $(find src/oop -name "*.java")

# 실행
java -cp out oop.search.presentation.ConsoleNewsApp
```

### GitHub Actions (자동)
`.github/workflows/news-scraper.yml`로 매 시간 자동 실행되며, 검색 결과를 GitHub Issue로 등록합니다.

---

## 📝 작업 이력

### JSON 파싱 개선
- **Before**: 수동 `String.split()` 기반 파싱 — JSON 구조 변경 시 깨지기 쉬움
- **After**: `JsonParser` (재귀 하강 파서) 도입 — 외부 라이브러리 없이 정확한 JSON 파싱
  - 이스케이프 문자, 유니코드, 중첩 구조 모두 정상 처리
  - `cutText()` 헬퍼 메서드 제거

### 페이징 지원
- `NewsPage` 도메인 모델 추가 (`total`, `start`, `display`, 네비게이션 헬퍼)
- `NewsProvider.fetchNews()` 시그니처에 `start` 파라미터 추가
- `NaverNewsProvider`가 API 응답의 페이징 메타데이터를 파싱하여 `NewsPage`로 반환
- `ConsoleNewsApp`에 페이지 네비게이션 UI 추가 (`n`: 다음, `p`: 이전, `q`: 돌아가기)
- `NewsService.search(query, limit)` 하위 호환 오버로드 유지 → `GitHubNewsApp` 수정 불필요

---

## 🛠️ 기술 스택

- **Java 17** (record, text block, sealed 등 활용)
- **java.net.http.HttpClient** (JDK 내장)
- **외부 라이브러리 없음** — 순수 JDK만 사용
- **IntelliJ IDEA** 프로젝트 (Maven/Gradle 미사용)
