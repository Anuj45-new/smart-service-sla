package com.smartservice.util;

import com.smartservice.service.RequestService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ServletContextListener that initializes and terminates the periodic SLA escalation worker.
 * Checks for breached SLA deadlines every 60 seconds without extra framework bloat.
 */
@WebListener
public class SlaBackgroundWorker implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(SlaBackgroundWorker.class);
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Starting SLA Escalation Background Worker...");

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "sla-escalation-worker");
            thread.setDaemon(true);
            return thread;
        });

        RequestService requestService = new RequestService();

        // Run check every 60 seconds, with an initial delay of 15 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int escalated = requestService.checkAndEscalateBreachedRequests();
                if (escalated > 0) {
                    logger.info("SLA Worker completed run: {} requests escalated.", escalated);
                }
            } catch (Throwable t) {
                logger.error("Unexpected error in SLA escalation background job: {}", t.getMessage(), t);
            }
        }, 15, 60, TimeUnit.SECONDS);

        logger.info("SLA Escalation Background Worker started successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Stopping SLA Escalation Background Worker...");

        if (scheduler != null) {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("SLA scheduler did not terminate within timeout.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Clean up database resources on context undeployment
        HibernateUtil.shutdown();
        JdbcUtil.shutdown();

        logger.info("SLA Escalation Background Worker and database pools shut down cleanly.");
    }
}
