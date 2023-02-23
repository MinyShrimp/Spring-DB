package hello.springdb1.repository;

import hello.springdb1.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0", 10000);
        Member savedMember = repository.save(member);
        log.info("member = {}", member);
        log.info("savedMember = {}", savedMember);
    }
}