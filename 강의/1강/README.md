# JDBC 이해

## 프로젝트 생성

### Spring initializer

* https://start.spring.io/
* 프로젝트 선택
    * Project: Gradle - Groovy
    * Language: Java 17
    * Spring Boot: 3.0.2
* Project Metadata
    * Group: hello
    * Artifact: spring-db-1
    * Packaging: Jar
* Dependencies
    * JDBC API, H2 Database, Lombok

### build.gradle

```gradle
dependencies {
    // ...
    // 테스트에서 Lombok 사용
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
}
```

테스트에서 Lombok을 사용하기 위해 두 줄을 추가한다.

## H2 데이터베이스 설정

## JDBC 이해

## JDBC와 최신 데이터 접근 기술

## 데이터베이스 연결

## JDBC 개발 - 등록

## JDBC 개발 - 조회

## JDBC 개발 - 수정, 삭제
