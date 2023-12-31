package com.woopaca.jdbc.exception.translator;

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

import static com.woopaca.jdbc.connection.ConnectionConst.PASSWORD;
import static com.woopaca.jdbc.connection.ConnectionConst.URL;
import static com.woopaca.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SpringExceptionTranslator {

    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "SELECT bad grammar";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch (SQLException exception) {
            int errorCode = exception.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode = {}", errorCode);
            log.info("error", exception);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "SELECT bad grammar";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch (SQLException exception) {
            assertThat(exception.getErrorCode()).isEqualTo(42122);

            SQLErrorCodeSQLExceptionTranslator exceptionTranslator =
                    new SQLErrorCodeSQLExceptionTranslator(dataSource);
            // BadSqlGrammarExceptionz
            DataAccessException translatedException = exceptionTranslator.translate("select", sql, exception);
            log.info("translatedException", translatedException);
            assertThat(translatedException).isInstanceOf(BadSqlGrammarException.class);
        }
    }
}
