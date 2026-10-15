package com.smartservice.dao;

import com.smartservice.entity.User;
import com.smartservice.enums.Role;
import com.smartservice.exception.AppException;
import com.smartservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
            throw new AppException("Failed to find user by ID", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding user by username: {}", username, e);
            throw new AppException("Failed to find user by username", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding user by email: {}", email, e);
            throw new AppException("Failed to find user by email", e);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User ORDER BY fullName ASC", User.class)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new AppException("Failed to retrieve users", e);
        }
    }

    public List<User> findByRole(Role role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE role = :role ORDER BY fullName ASC", User.class)
                    .setParameter("role", role)
                    .list();
        } catch (Exception e) {
            logger.error("Error finding users by role: {}", role, e);
            throw new AppException("Failed to retrieve users by role", e);
        }
    }

    public void save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error persisting user: {}", user, e);
            throw new AppException("Failed to save user", e);
        }
    }

    public void update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error updating user: {}", user, e);
            throw new AppException("Failed to update user", e);
        }
    }

    public void delete(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(user) ? user : session.merge(user));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error deleting user: {}", user, e);
            throw new AppException("Failed to delete user", e);
        }
    }
}
