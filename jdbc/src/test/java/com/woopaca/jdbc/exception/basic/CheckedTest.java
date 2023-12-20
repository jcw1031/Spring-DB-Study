package com.woopaca.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    @Test
    void catchException() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void throwsException() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrows)
                .isInstanceOf(MyCheckedException.class);
    }

    static class MyCheckedException extends Exception {

        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {

        private Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (Exception exception) {
                log.info("예외 처리! message = {}", exception.getMessage(), exception);
            }
        }

        public void callThrows() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {

        public void call() throws MyCheckedException {
            throw new MyCheckedException("Exception!");
        }
    }
}
