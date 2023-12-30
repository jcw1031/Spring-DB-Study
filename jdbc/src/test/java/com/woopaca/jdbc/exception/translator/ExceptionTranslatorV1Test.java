package com.woopaca.jdbc.exception.translator;

import com.woopaca.jdbc.domain.Member;
import com.woopaca.jdbc.repository.exception.MyDBException;
import com.woopaca.jdbc.repository.exception.MyDuplicateKeyException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.woopaca.jdbc.connection.ConnectionConst.PASSWORD;
import static com.woopaca.jdbc.connection.ConnectionConst.URL;
import static com.woopaca.jdbc.connection.ConnectionConst.USERNAME;

@Slf4j
public class ExceptionTranslatorV1Test {

    private Repository repository;
    private Service service;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave() {
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    static class Service {

        private final Repository repository;

        Service(Repository repository) {
            this.repository = repository;
        }

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("save id = {}", memberId);
            } catch (MyDuplicateKeyException exception) {
                log.info("키 중복. 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retry id = {}", retryId);
                repository.save(new Member(retryId, 0));
            } catch (MyDBException exception) {
                log.info("데이터 접근 계층 예외", exception);
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(1000);
        }
    }

    @Slf4j
    static class Repository {

        private final DataSource dataSource;

        Repository(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Member save(Member member) {
            String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)";
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                connection = dataSource.getConnection();
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, member.getMemberId());
                preparedStatement.setInt(2, member.getMoney());
                preparedStatement.executeUpdate();
                return member;
            } catch (SQLException exception) {
                if (exception.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(exception);
                }

                throw new MyDBException(exception);
            } finally {
                JdbcUtils.closeStatement(preparedStatement);
                JdbcUtils.closeConnection(connection);
            }
        }
    }
}
