package com.usermanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Manuelle DataSource-Konfiguration fuer SQLite.
 * Spring Boot kann SQLite nicht automatisch konfigurieren,
 * weil SQLite kein standardmaessiges JDBC-Database-Product ist.
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        // Fuer in-memory SQLite verwenden wir DriverManagerDataSource statt HikariCP
        // weil HikariCP Connection Pools mit in-memory SQLite nicht funktioniert
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
}
