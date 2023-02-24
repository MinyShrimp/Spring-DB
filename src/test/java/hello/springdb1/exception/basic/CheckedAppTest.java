package hello.springdb1.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();

        assertThatThrownBy(controller::request).isInstanceOf(Exception.class);
    }

    /**
     * 가상 컨트롤러
     * - Service의 SQLException, ConnectException을 밖으로 던진다.
     */
    static class Controller {
        Service service = new Service();

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    /**
     * 가상 서비스
     * - Repository와 NetworkClient의 Exception을 밖으로 던진다.
     */
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    /**
     * 가상 네트워크 클라이언트
     * - ConnectException을 밖으로 던진다.
     */
    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }

    }

    /**
     * 가상 레포지토리
     * - SQLException을 밖으로 던진다.
     */
    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
