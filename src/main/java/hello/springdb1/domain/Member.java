package hello.springdb1.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class Member {
    private final String memberId;
    private final int money;
}
