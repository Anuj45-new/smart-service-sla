package com.smartservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate SessionFactory Singleton.
 * Applies configuration settings from DatabaseConfig (allowing local / env variable overrides).
 */
public final class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {}

    private static synchronized void buildSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            return;
        }

        try {
            Configuration configuration = new Configuration();
            // Load base hibernate.cfg.xml
            configuration.configure("hibernate.cfg.xml");

            // Override with runtime DatabaseConfig (resolving env vars and local properties)
            configuration.setProperty("hibernate.connection.url", DatabaseConfig.getUrl());
            configuration.setProperty("hibernate.connection.username", DatabaseConfig.getUsername());
            configuration.setProperty("hibernate.connection.password", DatabaseConfig.getPassword());
            configuration.setProperty("hibernate.connection.driver_class", DatabaseConfig.getDriver());

            sessionFactory = configuration.buildSessionFactory();
            sessionFactory.getMetamodel()
    .getEntities()
    .forEach(entity ->
        logger.info("Registered Hibernate entity: {}",
                    entity.getName())
    );
            logger.info("Hibernate SessionFactory initialized successfully");
        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed: {}", ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            buildSessionFactory();
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Shutting down Hibernate SessionFactory...");
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
