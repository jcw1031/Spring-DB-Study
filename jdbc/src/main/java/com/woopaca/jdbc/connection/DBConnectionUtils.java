package com.woopaca.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.woopaca.jdbc.connection.ConnectionConst.PASSWORD;
import static com.woopaca.jdbc.connection.ConnectionConst.URL;
import static com.woopaca.jdbc.connection.ConnectionConst.USERNAME;

@Slf4j
public class DBConnectionUtils {

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("connection = {}, class = {}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
