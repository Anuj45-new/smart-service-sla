package com.smartservice.dao;

import com.smartservice.entity.RequestHistory;
import com.smartservice.exception.AppException;
import com.smartservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class RequestHistoryDao {

    private static final Logger logger = LoggerFactory.getLogger(RequestHistoryDao.class);

    public Optional<RequestHistory> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(RequestHistory.class, id));
        } catch (Exception e) {
            logger.error("Error finding request history by ID: {}", id, e);
            throw new AppException("Failed to find request history by ID", e);
        }
    }

    public List<RequestHistory> findByRequestId(Long requestId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM RequestHistory h WHERE h.request.id = :requestId ORDER BY h.performedAt DESC",
                    RequestHistory.class)
                    .setParameter("requestId", requestId)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding history for request ID: {}", requestId, e);
            throw new AppException("Failed to retrieve request history", e);
        }
    }

    public void save(RequestHistory history) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(history);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error saving request history: {}", history, e);
            throw new AppException("Failed to save request history", e);
        }
    }
}
