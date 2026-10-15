package com.smartservice.dao;

import com.smartservice.entity.Escalation;
import com.smartservice.exception.AppException;
import com.smartservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class EscalationDao {

    private static final Logger logger = LoggerFactory.getLogger(EscalationDao.class);

    public Optional<Escalation> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Escalation.class, id));
        } catch (Exception e) {
            logger.error("Error finding escalation by ID: {}", id, e);
            throw new AppException("Failed to find escalation by ID", e);
        }
    }

    public Optional<Escalation> findByRequestId(Long requestId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Escalation e WHERE e.request.id = :requestId",
                    Escalation.class)
                    .setParameter("requestId", requestId)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding escalation for request ID: {}", requestId, e);
            throw new AppException("Failed to retrieve escalation", e);
        }
    }

    public List<Escalation> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Escalation ORDER BY breachedAt DESC", Escalation.class)
                    .list();
        } catch (Exception e) {
            logger.error("Error retrieving all escalations", e);
            throw new AppException("Failed to retrieve escalations", e);
        }
    }

    public void save(Escalation escalation) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(escalation);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error saving escalation: {}", escalation, e);
            throw new AppException("Failed to save escalation", e);
        }
    }
}
