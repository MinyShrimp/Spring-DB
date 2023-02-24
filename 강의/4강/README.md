# 스프링과 문제 해결 - 트랜잭션

## 문제점들

### 애플리케이션 구조

![img.png](img.png)

여러가지 애플리케이션 구조가 있지만, 가장 단순하면서 많이 사용하는 방법은 역할에 따라 3가지 계층으로 나누는 것이다.

* 프레젠테이션 계층
    * UI와 관련된 처리 담당
    * 웹 요청과 응답
    * 사용자 요청을 검증
    * 주 사용 기술: 서블릿과 HTTP 같은 웹 기술, 스프링 MVC
* 서비스 계층
    * 비즈니스 로직을 담당
    * 주 사용 기술: 가급적 특정 기술에 의존하지 않고, 순수 자바 코드로 작성
* 데이터 접근 계층
    * 실제 데이터베이스에 접근하는 코드
    * 주 사용 기술: JDBC, JPA, File, Redis, Mongo ...

#### 순수한 서비스 계층

* 여기서 가장 중요한 곳은 어디일까? 바로 핵심 비즈니스 로직이 들어있는 서비스 계층이다.
    * 시간이 흘러서 UI(웹)와 관련된 부분이 변하고, 데이터 저장 기술을 다른 기술로 변경해도, 비즈니스 로직은 최대한 변경없이 유지되어야 한다.

* 이렇게 하려면 서비스 계층을 특정 기술에 종속적이지 않게 개발해야 한다.
    * 이렇게 계층을 나눈 이유도 **서비스 계층을 최대한 순수하게 유지**하기 위한 목적이 크다.
      기술에 종속적인 부분은 프레젠테이션 계층, 데이터 접근 계층에서 가지고 간다.
    * **프레젠테이션 계층**은 클라이언트가 접근하는 UI와 관련된 기술인 웹, 서블릿, HTTP와 관련된 부분을 담당해준다.
      그래서 서비스 계층을 이런 UI와 관련된 기술로부터 보호해준다.
      예를 들어서 HTTP API 를 사용하다가 GRPC 같은 기술로 변경해도 프레젠테이션 계층의 코드만 변경하고, 서비스 계층은 변경하지 않아도 된다.
    * **데이터 접근 계층**은 데이터를 저장하고 관리하는 기술을 담당해준다.
      그래서 JDBC, JPA와 같은 구체적인 데이터 접근 기술로부터 서비스 계층을 보호해준다.
      예를 들어서 JDBC를 사용하다가 JPA 로 변경해도 서비스 계층은 변경하지 않아도 된다.
      물론 서비스 계층에서 데이터 접근 계층을 직접 접근하는 것이 아니라, 인터페이스를 제공하고 서비스 계층은 이 인터페이스에 의존하는 것이 좋다.
      그래야 서비스 코드의 변경 없이 `JdbcRepository`를 `JpaRepository`로 변경할 수 있다.

* 서비스 계층이 특정 기술에 종속되지 않기 때문에 비즈니스 로직을 유지보수 하기도 쉽고, 테스트 하기도 쉽다.

* 정리하자면 서비스 계층은 가급적 비즈니스 로직만 구현하고 특정 구현 기술에 직접 의존해서는 안된다.
  이렇게 하면 향후 구현 기술이 변경될 때 변경의 영향 범위를 최소화 할 수 있다.

### 문제점들

서비스 계층을 순수하게 유지하려면 어떻게 해야할까? 지금까지 개발한 `MemberService` 코드들을 살펴보자.

#### MemberService V1

```java
/**
 * 계좌 이체 비즈니스 로직 V1
 */
@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 repository;

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    public void accountTransfer(
            String fromId,
            String toId,
            int money
    ) throws SQLException {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        repository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        repository.update(toId, toMember.getMoney() + money);
    }

    /**
     * 대상 회원의 ID가 ex 인지 검증
     */
    private void validation(
            Member toMember
    ) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
```

* `MemberServiceV1`은 특정 기술에 종속적이지 않고, 순수한 비즈니스 로직만 존재한다.
* 특정 기술과 관련된 코드가 거의 없어서 코드가 깔끔하고, 유지보수 하기 쉽다.
* 향후 비즈니스 로직의 변경이 필요하면 이 부분을 변경하면 된다.

사실 여기에도 남은 문제가 있다. (지금 단계에서 이 문제들은 이런게 있구나 참고만 하고 넘어가자)

* `SQLException`이라는 JDBC 기술에 의존한다는 점이다.
* 이 부분은 `memberRepository`에서 올라오는 예외이기 때문에 `memberRepository`에서 해결해야 한다.
    * 이 부분은 뒤에서 예외를 다룰 때 알아보자.
* `MemberRepositoryV1`이라는 구체 클래스에 직접 의존하고 있다.
    * `MemberRepository` 인터페이스를 도입하면 향후 `MemberService`의 코드의 변경 없이 다른 구현 기술로 손쉽게 변경할 수 있다.

#### MemberService V2

```java
/**
 * 계좌 이체 비즈니스 로직 V2
 * - 트랜잭션 - 파라미터 연동 및 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 repository;

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    public void accountTransfer(
            String fromId,
            String toId,
            int money
    ) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);
            bizLogic(con, fromId, toId, money);
            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            release(con);
        }
    }

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    private void bizLogic(
            Connection con,
            String fromId,
            String toId,
            int money
    ) throws SQLException {
        Member fromMember = repository.findById(con, fromId);
        Member toMember = repository.findById(con, toId);

        repository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        repository.update(con, toId, toMember.getMoney() + money);
    }

    /**
     * 대상 회원의 ID가 ex 인지 검증
     */
    private void validation(
            Member toMember
    ) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    /**
     * 커넥션 종료
     */
    private void release(
            Connection con
    ) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }

        }
    }
}
```

* 트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작하는 것이 좋다.
* 그런데 문제는 트랜잭션을 사용하기 위해서 `javax.sql.DataSource`, `java.sql.Connection`, `java.sql.SQLException` 같은 **JDBC 기술에 의존**해야 한다는
  점이다.
* 트랜잭션을 사용하기 위해 JDBC 기술에 의존한다.
    * 결과적으로 비즈니스 로직보다 JDBC를 사용해서 트랜잭션을 처리하는 코드가 더 많다.
* 향후 JDBC에서 JPA 같은 다른 기술로 바꾸어 사용하게 되면 서비스 코드도 모두 함께 변경해야 한다.
    * JPA는 트랜잭션을 사용하는 코드가 JDBC와 다르다.
* 핵심 비즈니스 로직과 JDBC 기술이 섞여 있어서 유지보수 하기 어렵다.

### 문제 정리

지금까지 우리가 개발한 애플리케이션의 문제점은 크게 3가지이다.

* 트랜잭션 문제
* 예외 누수 문제
* JDBC 반복 문제

#### 트랜잭션 문제

가장 큰 문제는 트랜잭션을 적용하면서 생긴 다음과 같은 문제들이다.

* **JDBC 구현 기술이 서비스 계층에 누수되는 문제**
    * 트랜잭션을 적용하기 위해 JDBC 구현 기술이 서비스 계층에 누수되었다.
    * 서비스 계층은 순수해야 한다.
        * 구현 기술을 변경해도 서비스 계층 코드는 최대한 유지할 수 있어야 한다. (변화에 대응)
        * 그래서 데이터 접근 계층에 JDBC 코드를 다 몰아두는 것이다.
        * 물론 데이터 접근 계층의 구현 기술이 변경될 수도 있으니 데이터 접근 계층은 인터페이스를 제공하는 것이 좋다.
    * 서비스 계층은 특정 기술에 종속되지 않아야 한다.
        * 지금까지 그렇게 노력해서 데이터 접근 계층으로 JDBC 관련 코드를 모았는데,
          트랜잭션을 적용하면서 결국 서비스 계층에 JDBC 구현 기술의 누수가 발생했다.
* **트랜잭션 동기화 문제**
    * 같은 트랜잭션을 유지하기 위해 커넥션을 파라미터로 넘겨야 한다.
    * 이때 파생되는 문제들도 있다. 똑같은 기능도 트랜잭션용 기능과 트랜잭션을 유지하지 않아도 되는 기능으로 분리해야 한다.
* **트랜잭션 적용 반복 문제**
    * 트랜잭션 적용 코드를 보면 반복이 많다. `try`, `catch`, `finally`...

#### 예외 누수

* 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파된다.
* `SQLException`은 체크 예외이기 때문에 데이터 접근 계층을 호출한 서비스 계층에서 해당 예외를 잡아서 처리하거나 명시적으로 `throws`를 통해서 다시 밖으로 던져야한다.
* `SQLException`은 JDBC 전용 기술이다.
    * 향후 JPA나 다른 데이터 접근 기술을 사용하면, 그에 맞는 다른 예외로 변경해야 하고, 결국 서비스 코드도 수정해야 한다.

#### JDBC 반복 문제

* 지금까지 작성한 `MemberRepository`코드는 순수한 JDBC를 사용했다.
* 이 코드들은 유사한 코드의 반복이 너무 많다.
    * `try`, `catch`, `finally`...
    * 커넥션을 열고, `PreparedStatement`를 사용하고, 결과를 매핑하고... 실행하고, 커넥션과 리소스를 정리한다.

### 스프링과 문제 해결

스프링은 서비스 계층을 순수하게 유지하면서, 지금까지 이야기한 문제들을 해결할 수 있는 다양한 방법과 기술들을 제공한다.
지금부터 스프링을 사용해서 우리 애플리케이션이 가진 문제들을 하나씩 해결해보자.

## 트랜잭션 추상화

## 트랜잭션 동기화

## 트랜잭션 문제 해결 - 트랜잭션 매니저 1

## 트랜잭션 문제 해결 - 트랜잭션 매니저 2

## 트랜잭션 문제 해결 - 트랜잭션 템플릿

## 트랜잭션 문제 해결 - 트랜잭션 AOP 이해

## 트랜잭션 문제 해결 - 트랜잭션 AOP 적용

## 트랜잭션 문제 해결 - 트랜잭션 AOP 정리

## 스프링 부트의 자동 리소스 등록
