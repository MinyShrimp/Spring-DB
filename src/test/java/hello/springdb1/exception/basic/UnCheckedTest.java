package hello.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        assertThat(service.callCatch()).isEqualTo("ex");
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow).isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException 을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * 가상 Service
     * UncheckedException 은 예외를 잡아도 되고, (명시적으로) 던지지 않아도 된다.
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
            } catch (MyUncheckedException e) {
                log.info("예외 처리, message = {}", e.getMessage(), e);
                errorMessage = e.getMessage();
            }
            return errorMessage;
        }

        /**
         * (명시적으로) 던지지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }

    }

    /**
     * 가상 Repository
     * - MyUncheckedException 을 던진다
     */
    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
