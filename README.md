# Free Remote Controller

무료 안드로이드 리모컨 애플리케이션

## 주요 기능

- TV, 에어컨, 셋톱박스 등 다양한 기기 제어
- IR 블라스터 지원 (하드웨어가 있는 경우)
- Wi-Fi를 통한 스마트 기기 제어
- 사용자 정의 리모컨 레이아웃
- 기기별 프리셋 저장

## 기술 스택

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Database**: Room
- **Network**: Retrofit + OkHttp

## 프로젝트 구조

```
app/
├── src/main/java/com/freeremote/
│   ├── data/           # 데이터 레이어
│   ├── domain/         # 도메인 레이어
│   ├── presentation/   # 프레젠테이션 레이어
│   └── di/            # 의존성 주입
```

## 개발 환경 설정

1. Android Studio Hedgehog 이상
2. Minimum SDK: 24 (Android 7.0)
3. Target SDK: 34 (Android 14)

## 라이선스

MIT License