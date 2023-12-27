package com.woopaca.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class UncheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    @Test
    void printException() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception exception) {
//            exception.printStackTrace();
            log.info("예외!", exception);
        }
    }

    static class Controller {

        private final Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {

        private final Repository repository = new Repository();
        private final NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {

        public void call() {
            throw new RuntimeConnectException("연결 실패!");
        }
    }

    static class Repository {

        public void call() {
            try {
                runSQL();
            } catch (SQLException exception) {
                throw new RuntimeSQLException(exception);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("예외!");
        }
    }

    static class RuntimeConnectException extends RuntimeException {

        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {

        public RuntimeSQLException() {
            super();
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
