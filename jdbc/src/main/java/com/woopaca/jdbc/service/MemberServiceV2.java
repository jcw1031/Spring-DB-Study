package com.woopaca.jdbc.service;

import com.woopaca.jdbc.domain.Member;
import com.woopaca.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    private void executeLogic(BusinessLogic<Connection> logic) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            logic.doing(connection); // Business Logic
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        executeLogic((connection) -> {
            Member fromMember = memberRepository.findById(connection, fromId);
            Member toMember = memberRepository.findById(connection, toId);

            memberRepository.update(connection, fromId, fromMember.getMoney() - money);
            validation(toMember);
            memberRepository.update(connection, toId, toMember.getMoney() + money);
        });
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    private void release(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (Exception e) {
                log.info("error = ", e);
            }
        }
    }
}

@FunctionalInterface
interface BusinessLogic<T> {

    void doing(T t) throws Exception;
}
