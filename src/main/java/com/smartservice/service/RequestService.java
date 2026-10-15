package com.smartservice.service;

import com.smartservice.dao.CategoryDao;
import com.smartservice.dao.EscalationDao;
import com.smartservice.dao.RequestHistoryDao;
import com.smartservice.dao.ServiceRequestDao;
import com.smartservice.dao.UserDao;
import com.smartservice.dto.RequestCreateDto;
import com.smartservice.entity.Category;
import com.smartservice.entity.Escalation;
import com.smartservice.entity.RequestHistory;
import com.smartservice.entity.ServiceRequest;
import com.smartservice.entity.User;
import com.smartservice.enums.Role;
import com.smartservice.enums.Status;
import com.smartservice.exception.AppException;
import com.smartservice.util.HibernateUtil;
import com.smartservice.util.SlaCalculator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final ServiceRequestDao requestDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;
    private final RequestHistoryDao historyDao;
    private final EscalationDao escalationDao;

    public RequestService() {
        this.requestDao = new ServiceRequestDao();
        this.categoryDao = new CategoryDao();
        this.userDao = new UserDao();
        this.historyDao = new RequestHistoryDao();
        this.escalationDao = new EscalationDao();
    }

    public RequestService(ServiceRequestDao requestDao, CategoryDao categoryDao,
                          UserDao userDao, RequestHistoryDao historyDao,
                          EscalationDao escalationDao) {
        this.requestDao = requestDao;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
        this.historyDao = historyDao;
        this.escalationDao = escalationDao;
    }

    /**
     * Creates a new service request and logs the CREATED audit history entry atomically.
     */
    public ServiceRequest createRequest(RequestCreateDto dto, Long creatorId) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new AppException("Request title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new AppException("Request description is required");
        }
        if (dto.getCategoryId() == null) {
            throw new AppException("Category must be selected");
        }
        if (dto.getPriority() == null) {
            throw new AppException("Priority must be selected");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            User creator = session.get(User.class, creatorId);
            if (creator == null) {
                throw new AppException("Creator user not found with ID: " + creatorId);
            }

            Category category = session.get(Category.class, dto.getCategoryId());
            if (category == null) {
                throw new AppException("Category not found with ID: " + dto.getCategoryId());
            }

            LocalDateTime now = LocalDateTime.now();
            int slaHours = dto.getPriority().getDefaultSlaHours();
            LocalDateTime slaDeadline = SlaCalculator.calculateDeadline(now, dto.getPriority());

            ServiceRequest request = new ServiceRequest();
            request.setTitle(dto.getTitle().trim());
            request.setDescription(dto.getDescription().trim());
            request.setCategory(category);
            request.setPriority(dto.getPriority());
            request.setStatus(Status.OPEN);
            request.setCreatedBy(creator);
            request.setSlaHours(slaHours);
            request.setSlaDeadline(slaDeadline);
            request.setSlaBreached(false);
            request.setCreatedAt(now);
            request.setUpdatedAt(now);

            session.persist(request);

            RequestHistory history = new RequestHistory(
                    request,
                    "CREATED",
                    null,
                    Status.OPEN.name(),
                    "Service request created with SLA deadline: " + slaDeadline,
                    creator
            );
            session.persist(history);

            tx.commit();
            logger.info("Created ServiceRequest ID: {} by user: {}", request.getId(), creator.getUsername());
            return request;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to create service request", e);
            throw new AppException("Failed to create service request: " + e.getMessage(), e);
        }
    }

    /**
     * Assigns a service request to an agent and records audit history atomically.
     */
    public ServiceRequest assignRequest(Long requestId, Long agentId, Long adminId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            ServiceRequest request = session.get(ServiceRequest.class, requestId);
            if (request == null) {
                throw new AppException("Service request not found: " + requestId);
            }

            User agent = session.get(User.class, agentId);
            if (agent == null || agent.getRole() != Role.AGENT) {
                throw new AppException("Invalid agent ID: " + agentId);
            }

            User admin = session.get(User.class, adminId);
            if (admin == null || admin.getRole() != Role.ADMIN) {
                throw new AppException("Assignment requires ADMIN privileges");
            }

            String oldAssignee = request.getAssignedTo() != null ? request.getAssignedTo().getFullName() : "Unassigned";
            request.setAssignedTo(agent);

            if (request.getStatus() == Status.OPEN) {
                request.setStatus(Status.ASSIGNED);
            }
            request.setUpdatedAt(LocalDateTime.now());
            session.merge(request);

            RequestHistory history = new RequestHistory(
                    request,
                    "ASSIGNED",
                    oldAssignee,
                    agent.getFullName(),
                    "Assigned to agent " + agent.getFullName() + " by " + admin.getUsername(),
                    admin
            );
            session.persist(history);

            tx.commit();
            logger.info("Assigned Request ID {} to agent {}", requestId, agent.getUsername());
            return request;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to assign service request ID: {}", requestId, e);
            throw new AppException("Failed to assign service request: " + e.getMessage(), e);
        }
    }

    /**
     * Updates status of a request and logs audit history atomically.
     */
    public ServiceRequest updateStatus(Long requestId, Status newStatus, String remarks, Long userId) {
        if (newStatus == null) {
            throw new AppException("New status cannot be null");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            ServiceRequest request = session.get(ServiceRequest.class, requestId);
            if (request == null) {
                throw new AppException("Service request not found: " + requestId);
            }

            User user = session.get(User.class, userId);
            if (user == null) {
                throw new AppException("User not found: " + userId);
            }

            String oldStatus = request.getStatus().name();
            request.setStatus(newStatus);
            request.setUpdatedAt(LocalDateTime.now());

            if ((newStatus == Status.RESOLVED || newStatus == Status.CLOSED) && request.getResolvedAt() == null) {
                request.setResolvedAt(LocalDateTime.now());
            }

            session.merge(request);

            RequestHistory history = new RequestHistory(
                    request,
                    "STATUS_CHANGE",
                    oldStatus,
                    newStatus.name(),
                    remarks != null && !remarks.trim().isEmpty() ? remarks.trim() : "Status updated to " + newStatus,
                    user
            );
            session.persist(history);

            tx.commit();
            logger.info("Updated Request ID {} status from {} to {}", requestId, oldStatus, newStatus);
            return request;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to update status for request ID: {}", requestId, e);
            throw new AppException("Failed to update status: " + e.getMessage(), e);
        }
    }

    /**
     * Checks all pending breached requests, marks them as breached, creates an Escalation,
     * changes status to ESCALATED, and appends RequestHistory.
     * Guaranteed to prevent double escalation for the same request.
     */
    public int checkAndEscalateBreachedRequests() {
        LocalDateTime now = LocalDateTime.now();
        List<ServiceRequest> breachedCandidates = requestDao.findPendingBreachedRequests(now);

        if (breachedCandidates.isEmpty()) {
            return 0;
        }

        int escalatedCount = 0;

        for (ServiceRequest candidate : breachedCandidates) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                ServiceRequest request = session.get(ServiceRequest.class, candidate.getId());
                if (request == null) {
                    tx.rollback();
                    continue;
                }

                // Guard check: ensure request hasn't been resolved or already escalated concurrently
                if (request.isSlaBreached() || request.getStatus().isTerminal()) {
                    tx.rollback();
                    continue;
                }

                String oldStatus = request.getStatus().name();
                request.setSlaBreached(true);
                request.setStatus(Status.ESCALATED);
                request.setUpdatedAt(now);
                session.merge(request);

                // Create Escalation record
                Escalation escalation = new Escalation(
                        request,
                        "SLA deadline breached. Allocated duration: " + request.getSlaHours() + "h",
                        request.getSlaDeadline()
                );
                session.persist(escalation);

                // Audit history: performed by creator or system
                User performer = request.getCreatedBy();
                RequestHistory history = new RequestHistory(
                        request,
                        "ESCALATED",
                        oldStatus,
                        Status.ESCALATED.name(),
                        "System SLA breach triggered. Deadline was " + request.getSlaDeadline(),
                        performer
                );
                session.persist(history);

                tx.commit();
                escalatedCount++;
                logger.warn("Escalated SLA breached request ID: {} (deadline was: {})", request.getId(), request.getSlaDeadline());
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                logger.error("Error escalating request ID: {}", candidate.getId(), e);
            }
        }

        return escalatedCount;
    }

    public ServiceRequest getRequestById(Long id) {
        return requestDao.findById(id)
                .orElseThrow(() -> new AppException("Service request not found: " + id));
    }

    public List<ServiceRequest> getAllRequests() {
        return requestDao.findAll();
    }

    public List<ServiceRequest> getRequestsByUser(Long userId) {
        return requestDao.findByCreatedBy(userId);
    }

    public List<ServiceRequest> getRequestsForAgent(Long agentId) {
        return requestDao.findByAssignedTo(agentId);
    }

    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public List<RequestHistory> getRequestHistory(Long requestId) {
        return historyDao.findByRequestId(requestId);
    }
}
