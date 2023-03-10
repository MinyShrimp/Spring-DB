package hello.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        assertThat(service.callCatch()).isEqualTo("ex");
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow).isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception 상속 - CheckedException
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * 가상 Service
     * 예외를 잡아서 처리하거나, 밖으로 던져야 한다.
     */
    static class Service {
        static final Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리
         */
        public String callCatch() {
            String errorMessage = null;
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message = {}", e.getMessage(), e);
                errorMessage = e.getMessage();
            }
            return errorMessage;
        }

        /**
         * 체크 예외를 처리하지 않고 밖으로 던짐
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }

    }

    /**
     * 가상 Repository
     * - MyCheckedException 을 던진다
     */
    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
