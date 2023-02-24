package hello.springdb1.v1.service;

import hello.springdb1.domain.Member;
import hello.springdb1.v1.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

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
