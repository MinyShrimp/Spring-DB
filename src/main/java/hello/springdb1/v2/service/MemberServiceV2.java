package hello.springdb1.v2.service;

import hello.springdb1.domain.Member;
import hello.springdb1.v2.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
