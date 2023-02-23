package hello.springdb1.repository;

import hello.springdb1.connection.DBConnectionUtil;
import hello.springdb1.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {
    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

    public Member save(
            Member member
    ) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        try (
                Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)
        ) {
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }
}
