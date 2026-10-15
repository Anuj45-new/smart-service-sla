package com.smartservice.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC Connection Factory backed by a HikariCP connection pool.
 * Configured dynamically through DatabaseConfig.
 */
public final class JdbcUtil {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);
    private static volatile HikariDataSource dataSource;

    private JdbcUtil() {}

    private static synchronized void initDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(DatabaseConfig.getDriver());
            config.setJdbcUrl(DatabaseConfig.getUrl());
            config.setUsername(DatabaseConfig.getUsername());
            config.setPassword(DatabaseConfig.getPassword());

            config.setMaximumPoolSize(Integer.parseInt(DatabaseConfig.getProperty("hikari.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(DatabaseConfig.getProperty("hikari.minimumIdle", "2")));
            config.setIdleTimeout(Long.parseLong(DatabaseConfig.getProperty("hikari.idleTimeout", "30000")));
            config.setConnectionTimeout(Long.parseLong(DatabaseConfig.getProperty("hikari.connectionTimeout", "10000")));
            config.setMaxLifetime(Long.parseLong(DatabaseConfig.getProperty("hikari.maxLifetime", "600000")));
            config.setPoolName("SmartService-HikariPool");

            // Recommended MySQL performance optimizations for HikariCP
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP DataSource initialized successfully for URL: {}", DatabaseConfig.getUrl());
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP DataSource: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize database connection pool", e);
        }
    }

    /**
     * Obtains a Connection from the Hikari pool.
     * Callers must close it via try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource;
    }

    /**
     * Closes the HikariCP DataSource pool during application shutdown.
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down HikariCP DataSource...");
            dataSource.close();
            dataSource = null;
        }
    }
}
