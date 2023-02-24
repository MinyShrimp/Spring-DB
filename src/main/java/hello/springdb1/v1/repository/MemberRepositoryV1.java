package hello.springdb1.v1.repository;

import hello.springdb1.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC
 * - DataSource 사용
 * - JdbcUtils 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV1 {
    private final DataSource dataSource;

    /**
     * JdbcUtils 를 이용해 close 메서드 호출
     */
    private void close(
            Connection con,
            Statement stmt,
            ResultSet rs
    ) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    /**
     * 데이터 소스를 이용해 커넥션을 얻어온다.
     * - 실제 데이터 소스는 주입받아서 사용한다.
     */
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection = [{}], class = [{}]", con, con.getClass());
        return con;
    }

    /**
     * 회원 저장
     */
    public Member save(
            Member member
    ) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 회원 조회
     */
    public Member findById(
            String memberId
    ) throws SQLException, NoSuchElementException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getString("member_id"),
                        rs.getInt("money")
                );
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    /**
     * 회원 정보 수정
     */
    public void update(
            String memberId, int money
    ) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 회원 삭제
     */
    public void delete(
            String memberId
    ) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
}
