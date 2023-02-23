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

### 설치

* https://www.h2database.com/html/download-archive.html
    * 현재 자신의 스프링 부트의 버전에 맞는 버전을 설치하자.
    * Spring Boot 3.0.2의 경우, **h2 2.1.214** 버전이다.
* 설치한 폴더의 bin 폴더에 들어가서 아래의 명령어를 입력한다.
    * 권한 설정: `chmod 755 h2.sh`
    * 실행: `./h2.sh`
* 브라우저가 실행되면 URL에 IP 부분을 `localhost`로 변경한다.
* JDBC URL에 `jdbc:h2:~/test`를 입력한다. (최초 한번)
    * `~` 경로에 `~/test.mv.db` 파일이 생성되었는지 확인한다.
* 이후 부터는 JDBC URL에 `jdbc:h2:tcp://localhost/~/test`를 입력하여 접속한다.

### 테이블 생성

```roomsql
drop table member if exists cascade;
create table member (
    member_id varchar(10),
    money integer not null default 0,
    primary key (member_id)
);

insert into member(member_id, money) values ('hi1',10000);
insert into member(member_id, money) values ('hi2',20000);
```

### 테이블 확인

```roomsql
select * from member;
```

## JDBC 이해

## JDBC와 최신 데이터 접근 기술

## 데이터베이스 연결

## JDBC 개발 - 등록

## JDBC 개발 - 조회

## JDBC 개발 - 수정, 삭제
