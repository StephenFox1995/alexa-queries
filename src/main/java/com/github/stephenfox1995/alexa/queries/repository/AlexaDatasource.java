package com.github.stephenfox1995.alexa.queries.repository;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class AlexaDatasource {

    private static HikariDataSource dataSource;

    private AlexaDatasource() {}

    public static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:mysql://database:3306/alexa");
            dataSource.setUsername("alexa");
            dataSource.setPassword("password");
        }
        return dataSource;
    }
}
