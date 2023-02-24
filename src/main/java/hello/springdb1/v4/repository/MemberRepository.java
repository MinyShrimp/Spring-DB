package hello.springdb1.v4.repository;

import hello.springdb1.domain.Member;

public interface MemberRepository {

    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}
