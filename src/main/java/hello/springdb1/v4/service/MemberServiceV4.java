package hello.springdb1.v4.service;

import hello.springdb1.domain.Member;
import hello.springdb1.v4.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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
