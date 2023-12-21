package com.woopaca.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void catchUncheckedException() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void throwsUncheckedException() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrows)
                .isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException을 상속받은 예외는 언체크 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {

        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * Unchecked 예외는 예외를 잡거나 던지지 않아도 된다. 잡지 않으면 자동으로 밖으로 던져진다.
     */
    static class Service {

        private Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException exception) {
                log.info("예외 처리! message = {}", exception.getMessage(), exception);
            }
        }

        /**
         * 예외를 잡지 않아 자동으로 상위로 넘어간다.
         */
        public void callThrows() {
            repository.call();
        }
    }

    static class Repository {

        public void call() {
            throw new MyUncheckedException("예외!");
        }
    }
}
