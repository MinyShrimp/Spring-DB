package hello.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unChecked() {
        Controller controller = new Controller();
        assertThatThrownBy(controller::request).isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    /**
     * 가상 컨트롤러
     * - 명시적으로 Exception을 처리하지 않는다.
     */
    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    /**
     * 가상 서비스
     * - 명시적으로 Exception을 처리하지 않는다.
     */
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    /**
     * 가상 네트워크 클라이언트
     * - RuntimeConnectException을 밖으로 던진다.
     */
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }

    }

    /**
     * 가상 레포지토리
     * - RuntimeSQLException을 밖으로 던진다.
     */
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }

        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
