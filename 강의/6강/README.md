# 스프링과 문제 해결 - 예외 처리, 반복

## 체크 예외와 인터페이스

서비스 계층은 가급적 특정 구현 기술에 의존하지 않고, 순수하게 유지하는 것이 좋다.
이렇게 하려면 예외에 대한 의존도 함께 해결해야한다.

예를 들어서 서비스가 처리할 수 없는 `SQLException`에 대한 의존을 제거하려면 어떻게 해야할까?

서비스가 처리할 수 없으므로 리포지토리가 던지는 `SQLException` 체크 예외를 런타임 예외로 전환해서 서비스 계층에 던지자.
이렇게 하면 서비스 계층이 해당 예외를 무시할 수 있기 때문에, 특정 구현 기술에 의존하는 부분을 제거하고 서비스 계층을 순수하게 유지할 수 있다.

### 인터페이스 도입

먼저 `MemberRepository`인터페이스도 도입해서 구현 기술을 쉽게 변경할 수 있게 해보자.

#### 인터페이스 도입 그림

![img.png](img.png)

* 이렇게 인터페이스를 도입하면 `MemberService`는 `MemberRepository` 인터페이스에만 의존하면 된다.
* 이제 구현 기술을 변경하고 싶으면 DI를 사용해서 `MemberService` 코드의 변경 없이 구현 기술을 변경할 수 있다.

#### MemberRepository Interface

```java
public interface MemberRepository {
    
    Member save(Member member);
    
    Member findById(String memberId);
    
    void update(String memberId, int money);
    
    void delete(String memberId);
}
```

특정 기술에 종속되지 않는 순수한 인터페이스이다.
이 인터페이스를 기반으로 특정 기술을 사용하는 구현체를 만들면 된다.

### 체크 예외와 인터페이스

잠깐? 기존에는 왜 이런 인터페이스를 만들지 않았을까?
왜냐하면 `SQLException`이 체크 예외이기 때문이다. 여기서 체크 예외가 또 발목을 잡는다.
체크 예외를 사용하려면 인터페이스에도 해당 체크 예외가 선언 되어 있어야 한다.

#### 체크 예외 코드에 인터페이스 도입시 문제점 - 인터페이스

```java
public interface MemberRepository {
    
    Member save(Member member) throws SQLException;
    
    Member findById(String memberId) throws SQLException;
    
    void update(String memberId, int money) throws SQLException;
    
    void delete(String memberId) throws SQLException;
}
```

* 인터페이스의 메서드에 `throws SQLException`이 있는 것을 확인할 수 있다.

#### 체크 예외 코드에 인터페이스 도입시 문제점 - 구현 클래스

```java
@Slf4j
public class MemberRepositoryV3 implements MemberRepositoryEx {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";
    }
}
```

* 인터페이스의 구현체가 체크 예외를 던지려면, 인터페이스 메서드에 먼저 체크 예외를 던지는 부분이 선언되어 있어야 한다.
  그래야 구현 클래스의 메서드도 체크 예외를 던질 수 있다.
    * 쉽게 이야기 해서 `MemberRepositoryV3`가 `throws SQLException`를 하려면
      `MemberRepositoryEx`인터페이스에도 `throws SQLException`이 필요하다.

* 참고로 구현 클래스의 메서드에 선언할 수 있는 예외는 부모 타입에서 던진 예외와 같거나 하위 타입이어야 한다.
    * 예를 들어서 인터페이스 메서드에 `throws Exception`를 선언하면, 구현 클래스 메서드에 `throws SQLException`는 가능하다.
      `SQLException`은 `Exception`의 하위 타입이기 때문이다.

#### 특정 기술에 종속되는 인터페이스

구현 기술을 쉽게 변경하기 위해서 인터페이스를 도입하더라도 `SQLException` 과 같은 특정 구현 기술에
종속적인 체크 예외를 사용하게 되면 인터페이스에도 해당 예외를 포함해야 한다.
하지만 이것은 우리가 원하던 순수한 인터페이스가 아니다. **JDBC 기술에 종속적인 인터페이스**일 뿐이다.

인터페이스를 만드는 목적은 구현체를 쉽게 변경하기 위함인데, 이미 인터페이스가 특정 구현 기술에 오염이 되어 버렸다.
향후 **JDBC가 아닌 다른 기술로 변경한다면 인터페이스 자체를 변경해야 한다.**

#### 런타임 예외와 인터페이스

런타임 예외는 이런 부분에서 자유롭다.
인터페이스에 런타임 예외를 따로 선언하지 않아도 된다.
따라서 인터페이스가 특정 기술에 종속적일 필요가 없다.

## 런타임 예외 적용

### 예제

#### MemberRepository Interface

```java
public interface MemberRepository {

    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}
```

#### MyDbException 런타임 예외

```java
public class MyDbException extends RuntimeException {
    public MyDbException() {
        super();
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}
```

* `RuntimeException`을 상속받았다. 따라서 `MyDbException`은 런타임(언체크) 예외가 된다.

#### MemberRepository V4_1

```java
/**
 * 예외 누수 문제 해결
 * 체크 예외를 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * throws SQLException 제거
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_1 implements MemberRepository {
    private final DataSource dataSource;

    /**
     * JdbcUtils 를 이용해 close 메서드 호출
     * - 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
     */
    private void close(
            Connection con,
            Statement stmt,
            ResultSet rs
    ) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    /**
     * 데이터 소스를 이용해 커넥션을 얻어온다.
     * - 실제 데이터 소스는 주입받아서 사용한다.
     * - 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
     */
    private Connection getConnection() throws SQLException {
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = [{}], class = [{}]", con, con.getClass());
        return con;
    }

    /**
     * 회원 저장
     */
    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 회원 조회
     */
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getString("member_id"),
                        rs.getInt("money")
                );
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    /**
     * 회원 정보 수정
     */
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 회원 삭제
     */
    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(con, pstmt, null);
        }
    }
}
```

* `MemberRepository`인터페이스를 구현한다.
* 이 코드에서 핵심은 `SQLException`이라는 체크 예외를 `MyDbException`이라는 런타임 예외로 변환해서 던지는 부분이다.

#### 예외 변환

```java
catch (SQLException e) {
    throw new MyDbException(e);
}
```

* 잘 보면 기존 예외를 생성자를 통해서 포함하고 있는 것을 확인할 수 있다.
  예외는 원인이 되는 예외를 내부에 포함할 수 있는데, 꼭 이렇게 작성해야 한다.
  그래야 예외를 출력했을 때 원인이 되는 기존 예외도 함께 확인할 수 있다.

* `MyDbException`이 내부에 `SQLException`을 포함하고 있다고 이해하면 된다.
  예외를 출력했을 때 스택 트레이스를 통해 둘다 확인할 수 있다.

다음과 같이 기존 예외를 무시하고 작성하면 절대 안된다!

#### 예외 변환 - 기존 예외 무시

```java
catch (SQLException e) {
    throw new MyDbException();
}
```

* 잘 보면 `new MyDbException()`으로 해당 예외만 생성하고 기존에 있는 `SQLException`은 포함하지 않고 무시한다.
* 따라서 `MyDbException`은 내부에 원인이 되는 다른 예외를 포함하지 않는다.
* 이렇게 원인이 되는 예외를 내부에 포함하지 않으면, 예외를 스택 트레이스를 통해 출력했을 때 기존에 원인이 되는 부분을 확인할 수 없다.
    * 만약 `SQLException`에서 문법 오류가 발생했다면 그 부분을 확인할 방법이 없게 된다.

> 주의!<br>
> 예외를 변환할 때는 기존 예외를 꼭! 포함하자.
> 장애가 발생하고 로그에서 진짜 원인이 남지 않는 심각한 문제가 발생할 수 있다.
> 중요한 내용이어서 한번 더 설명했다.

#### MemberService V4

```java
/**
 * 계좌 이체 비즈니스 로직 V4
 * - 예외 누수 문제 해결
 * - SQLException 제거
 * - MemberRepository 인터페이스 의존
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {
    private final MemberRepository repository;

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    @Transactional
    public void accountTransfer(
            String fromId,
            String toId,
            int money
    ) {
        // 비즈니스 로직
        bizLogic(fromId, toId, money);
    }

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    private void bizLogic(
            String fromId,
            String toId,
            int money
    ) {
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

* `MemberRepository`인터페이스에 의존하도록 코드를 변경했다.
* `MemberServiceV3_3`와 비교해서 보면 드디어 메서드에서 `throws SQLException` 부분이 제거된 것을 확인할 수 있다.
* 드디어 순수한 서비스를 완성했다.

#### MemberService V4 Test

```java
/**
 * 예외 누수 문제 해결
 * SQLException 제거
 * <p>
 * MemberRepository 인터페이스 의존
 */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {
    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberServiceV4 memberService;

    /**
     * 각 테스트가 끝나면 데이터 삭제
     */
    @AfterEach()
    void afterEach() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    /**
     * 해당 스프링 Bean이 AOP Proxy로 되어있는지 확인
     */
    @Test
    void AopCheck() {
        log.info("memberService class = {}", memberService.getClass());
        log.info("memberRepository class = {}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        // when
        assertThatThrownBy(
                () -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000)
        ).isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());

        // memberA의 돈만 2000원 줄고, ex의 돈은 10000원 그대로이다.
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

    /**
     * 테스트용 스프링 Bean 등록
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepository memberRepositoryV4_1() {
            return new MemberRepositoryV4_1(dataSource());
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepositoryV4_1());
        }
    }
}
```

* `MemberRepository` 인터페이스를 사용하도록 했다.
* 테스트가 모두 정상 동작하는 것을 확인할 수 있다.

### 정리

* 체크 예외를 런타임 예외로 변환하면서 인터페이스와 서비스 계층의 순수성을 유지할 수 있게 되었다.
* 덕분에 향후 JDBC에서 다른 구현 기술로 변경하더라도 서비스 계층의 코드를 변경하지 않고 유지할 수 있다.

### 남은 문제

리포지토리에서 넘어오는 특정한 예외의 경우 복구를 시도할 수도 있다.
그런데 지금 방식은 항상 `MyDbException`이라는 예외만 넘어오기 때문에 예외를 구분할 수 없는 단점이 있다.
만약 특정 상황에는 예외를 잡아서 복구하고 싶으면 예외를 어떻게 구분해서 처리할 수 있을까?

## 데이터 접근 예외 직접 만들기

## 스프링 예외 추상화 이해

## 스프링 예외 추상화 적용

## JDBC 반목 문제 해결 - JdbcTemplate
