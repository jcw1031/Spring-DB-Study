package com.woopaca.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.woopaca.jdbc.connection.ConnectionConst.PASSWORD;
import static com.woopaca.jdbc.connection.ConnectionConst.URL;
import static com.woopaca.jdbc.connection.ConnectionConst.USERNAME;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection connectionA = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection connectionB = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connectionA = {}, class = {}", connectionA, connectionA.getClass());
        log.info("connectionB = {}, class = {}", connectionB, connectionB.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource - 항상 새로운 커넥션 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection connectionA = dataSource.getConnection();
        Connection connectionB = dataSource.getConnection();
        log.info("connectionA = {}, class = {}", connectionA, connectionA.getClass());
        log.info("connectionB = {}, class = {}", connectionB, connectionB.getClass());
    }
}
