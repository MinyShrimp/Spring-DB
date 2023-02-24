package hello.springdb1.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.springdb1.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer";


        try (
                Connection con = dataSource.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)
        ) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();

            // org.h2.jdbc.JdbcSQLSyntaxErrorException
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode = {}", errorCode, e);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer";

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)
        ) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);

            // org.springframework.jdbc.support.sql-error-codes.xml
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            // org.springframework.jdbc.BadSqlGrammarException
            DataAccessException resultEx = exTranslator.translate("select", sql, e);
            log.info("resultEx", resultEx);
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }
}
