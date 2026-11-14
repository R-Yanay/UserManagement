package com.usermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

/**
 * Manuelle DataSource-Konfiguration fuer SQLite.
 * Spring Boot kann SQLite nicht automatisch konfigurieren,
 * weil SQLite kein standardmaessiges JDBC-Database-Product ist.
 *
 * Fuer in-memory SQLite (Tests) wird SingleConnectionDataSource verwendet,
 * damit alle Hibernate-Operationen dieselbe Verbindung nutzen.
 *
 * Fuer dateibasiertes SQLite (Produktion) wird DriverManagerDataSource verwendet,
 * was parallele Zugriffe (z.B. IntelliJ Database-Tool) erlaubt.
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        // In-memory SQLite (Tests) braucht SingleConnectionDataSource,
        // damit alle Verbindungen dieselbe Datenbank teilen.
        if (url.contains(":memory:")) {
            SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
            dataSource.setUrl(url);
            dataSource.setDriverClassName(driverClassName);
            dataSource.setSuppressClose(true);
            return dataSource;
        }
        
        // Dateibasiertes SQLite (Produktion) mit DriverManagerDataSource
        // fuer parallele Zugriffe (z.B. IntelliJ Database-Tool waehrend des Betriebs).
        // Die URL muss den WAL-Modus enthalten (?journal_mode=WAL).
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
}
