package hello.springdb1.repository;

import hello.springdb1.connection.DBConnectionUtil;
import hello.springdb1.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {
    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

    /**
     * 회원 저장
     */
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

    /**
     * 회원 조회
     */
    public Member findById(
            String memberId
    ) throws SQLException, NoSuchElementException {
        String sql = "select * from member where member_id = ?";

        try (
                Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql);
        ) {
            pstmt.setString(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Member(
                            rs.getString("member_id"),
                            rs.getInt("money")
                    );
                } else {
                    throw new NoSuchElementException("member not found memberId = " + memberId);
                }
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }
}
