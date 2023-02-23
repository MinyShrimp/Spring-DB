package hello.springdb1.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.springdb1.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.springdb1.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀링: HikariProxyConnection -> JdbcConnection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV0", 10000);
        Member savedMember = repository.save(member);
        log.info("member = {}", member);
        log.info("savedMember = {}", savedMember);
        assertThat(savedMember).isEqualTo(member);

        // findById
        Member findMember = repository.findById(savedMember.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(findMember).isEqualTo(savedMember);

        // update
        // money: 10,000 -> 20,000
        repository.update(savedMember.getMemberId(), 20000);
        Member updatedMember = repository.findById(savedMember.getMemberId());
        log.info("updatedMember = {}", updatedMember);
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // delete
        repository.delete(savedMember.getMemberId());
        assertThatThrownBy(
                () -> repository.findById(savedMember.getMemberId())
        ).isInstanceOf(NoSuchElementException.class);
    }
}
