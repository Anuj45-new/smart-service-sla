package com.smartservice.dao;

import com.smartservice.entity.ServiceRequest;
import com.smartservice.enums.Status;
import com.smartservice.exception.AppException;
import com.smartservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServiceRequestDao {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRequestDao.class);

    public Optional<ServiceRequest> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ServiceRequest request = session.get(ServiceRequest.class, id);
            if (request != null) {
                request.getHistoryList().size();
            }
            return Optional.ofNullable(request);
        } catch (Exception e) {
            logger.error("Error finding service request by ID: {}", id, e);
            throw new AppException("Failed to find service request by ID", e);
        }
    }

    public List<ServiceRequest> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ServiceRequest ORDER BY createdAt DESC", ServiceRequest.class)
                    .list();
        } catch (Exception e) {
            logger.error("Error retrieving all service requests", e);
            throw new AppException("Failed to retrieve service requests", e);
        }
    }

    public List<ServiceRequest> findByCreatedBy(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ServiceRequest r WHERE r.createdBy.id = :userId ORDER BY r.createdAt DESC", ServiceRequest.class)
                    .setParameter("userId", userId)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding requests by creator ID: {}", userId, e);
            throw new AppException("Failed to retrieve requests by creator", e);
        }
    }

    public List<ServiceRequest> findByAssignedTo(Long agentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ServiceRequest r WHERE r.assignedTo.id = :agentId ORDER BY r.createdAt DESC", ServiceRequest.class)
                    .setParameter("agentId", agentId)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding requests by assignee ID: {}", agentId, e);
            throw new AppException("Failed to retrieve requests by assignee", e);
        }
    }

    public List<ServiceRequest> findByStatus(Status status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ServiceRequest r WHERE r.status = :status ORDER BY r.createdAt DESC", ServiceRequest.class)
                    .setParameter("status", status)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding requests by status: {}", status, e);
            throw new AppException("Failed to retrieve requests by status", e);
        }
    }

    public List<ServiceRequest> findPendingBreachedRequests(LocalDateTime now) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ServiceRequest r " +
                    "WHERE r.status NOT IN (:resolved, :closed) " +
                    "  AND r.slaDeadline < :now " +
                    "  AND r.isSlaBreached = false", ServiceRequest.class)
                    .setParameter("resolved", Status.RESOLVED)
                    .setParameter("closed", Status.CLOSED)
                    .setParameter("now", now)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding pending breached requests at: {}", now, e);
            throw new AppException("Failed to find breached requests", e);
        }
    }

    public void save(ServiceRequest request) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(request);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error saving service request: {}", request, e);
            throw new AppException("Failed to save service request", e);
        }
    }

    public void update(ServiceRequest request) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(request);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error updating service request: {}", request, e);
            throw new AppException("Failed to update service request", e);
        }
    }

    public void delete(ServiceRequest request) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(request) ? request : session.merge(request));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error deleting service request: {}", request, e);
            throw new AppException("Failed to delete service request", e);
        }
    }
}
