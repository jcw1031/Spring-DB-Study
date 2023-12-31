package com.woopaca.jdbc.repository;

import com.woopaca.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exceptionTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw exceptionTranslator.translate("save", sql, e);
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    @Override
    public Member findById(String memberId) {
        String sql = "SELECT * FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Member(resultSet.getString("member_id"), resultSet.getInt("money"));
            } else {
                throw new NoSuchElementException("member not found (memberId = " + memberId + ")");
            }
        } catch (SQLException e) {
            throw exceptionTranslator.translate("findById", sql, e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw exceptionTranslator.translate("update", sql, e);
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "DELETE FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            int resultSize = preparedStatement.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            throw exceptionTranslator.translate("delete", sql, e);
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("connection = {}, class = {}", connection, connection.getClass());
        return connection;
    }
}
