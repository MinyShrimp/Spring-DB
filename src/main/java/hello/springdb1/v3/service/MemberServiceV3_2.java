package hello.springdb1.v3.service;

import hello.springdb1.domain.Member;
import hello.springdb1.v3.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 계좌 이체 비즈니스 로직 V3-2
 * - 트랜잭션 템플릿 사용
 */
@Slf4j
public class MemberServiceV3_2 {
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 repository;

    public MemberServiceV3_2(
            PlatformTransactionManager transactionManager,
            MemberRepositoryV3 repository
    ) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.repository = repository;
    }

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
        txTemplate.executeWithoutResult((status) -> {
            try {
                // 비즈니스 로직
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
