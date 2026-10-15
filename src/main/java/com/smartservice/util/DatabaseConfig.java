package com.smartservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized database configuration loader.
 * Priority order for configuration values:
 * 1. Environment variables (DB_URL, DB_USERNAME, DB_PASSWORD, DB_DRIVER)
 * 2. Local uncommitted file 'db.local.properties' (in working dir or classpath)
 * 3. Base resource file 'db.properties'
 */
public final class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties props = new Properties();

    static {
        loadConfiguration();
    }

    private DatabaseConfig() {}

    private static void loadConfiguration() {
        // 1. Load base db.properties from classpath
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                logger.info("Loaded base db.properties from classpath");
            }
        } catch (Exception e) {
            logger.warn("Could not load db.properties: {}", e.getMessage());
        }

        // 2. Overlay db.local.properties from classpath if present
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.local.properties")) {
            if (is != null) {
                props.load(is);
                logger.info("Loaded overlay db.local.properties from classpath");
            }
        } catch (Exception e) {
            logger.debug("No classpath db.local.properties found");
        }

        // 3. Overlay db.local.properties from working directory if present
        File localFile = new File("db.local.properties");
        if (localFile.exists() && localFile.isFile()) {
            try (FileInputStream fis = new FileInputStream(localFile)) {
                props.load(fis);
                logger.info("Loaded overlay db.local.properties from local directory");
            } catch (Exception e) {
                logger.warn("Failed reading local db.local.properties: {}", e.getMessage());
            }
        }

        // 4. Override with Environment Variables if present
        overrideFromEnv("DB_URL", "db.url");
        overrideFromEnv("DB_USERNAME", "db.username");
        overrideFromEnv("DB_PASSWORD", "db.password");
        overrideFromEnv("DB_DRIVER", "db.driver");
    }

    private static void overrideFromEnv(String envVar, String propKey) {
        String val = System.getenv(envVar);
        if (val != null && !val.trim().isEmpty()) {
            props.setProperty(propKey, val.trim());
            logger.info("Overridden property '{}' from environment variable '{}'", propKey, envVar);
        }
    }

    public static String getUrl() {
        return props.getProperty("db.url", "jdbc:mysql://localhost:3306/smart_service_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    }

    public static String getUsername() {
        return props.getProperty("db.username", "root");
    }

    public static String getPassword() {
        return props.getProperty("db.password", "");
    }

    public static String getDriver() {
        return props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll(props);
        return copy;
    }
}
