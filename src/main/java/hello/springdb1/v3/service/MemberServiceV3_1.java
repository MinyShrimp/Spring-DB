package hello.springdb1.v3.service;

import hello.springdb1.domain.Member;
import hello.springdb1.v3.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 계좌 이체 비즈니스 로직 V3
 * - 트랜잭션 매니저 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 repository;

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    public void accountTransfer(
            String fromId,
            String toId,
            int money
    ) throws SQLException {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비즈니스 로직
            bizLogic(fromId, toId, money);

            // 커밋
            transactionManager.commit(status);
        } catch (Exception e) {
            // 롤백
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }

    /**
     * fromId -> toId
     * money 만큼의 돈을 전송
     */
    private void bizLogic(
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
