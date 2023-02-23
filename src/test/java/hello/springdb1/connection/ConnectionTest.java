package hello.springdb1.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.springdb1.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
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
     */
    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource - 항상 새로운 커넥션 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("con1: class = {}, connection = {}", con1.getClass(), con1);
        log.info("con2: class = {}, connection = {}", con2.getClass(), con2);
    }
}
