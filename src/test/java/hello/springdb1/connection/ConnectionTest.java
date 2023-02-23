package hello.springdb1.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.springdb1.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    /**
     * 커넥션 사용
     */
    private static void useDataSource(
            DataSource dataSource
    ) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("con1: class = {}, connection = {}", con1.getClass(), con1);
        log.info("con2: class = {}, connection = {}", con2.getClass(), con2);
    }

    /**
     * DriverManager
     */
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("con1: class = {}, connection = {}", con1.getClass(), con1);
        log.info("con2: class = {}, connection = {}", con2.getClass(), con2);
    }

    /**
     * DataSourceDriverManager
     * - 항상 새로운 커넥션 획득
     */
    @Test
    void dataSourceDriverManager() throws SQLException {
        // 설정
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 사용
        useDataSource(dataSource);
    }

    /**
     * DataSourceConnectionPool
     * - 커넥션 풀링: HikariProxyConnection(Proxy) -> JdbcConnection(Target)
     */
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();

        // 설정
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        // 사용
        useDataSource(dataSource);

        // 커넥션 풀에서 커넥션 생성 시간 대기
        Thread.sleep(1000);
    }
}
