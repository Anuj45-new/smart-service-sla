package com.smartservice.dao;

import com.smartservice.entity.Category;
import com.smartservice.exception.AppException;
import com.smartservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CategoryDao {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDao.class);

    public Category findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Category.class, id);
        } catch (Exception e) {
            logger.error("Error finding category by ID: {}", id, e);
            throw new AppException("Failed to find category by ID", e);
        }
    }

    public Optional<Category> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Category WHERE name = :name", Category.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding category by name: {}", name, e);
            throw new AppException("Failed to find category by name", e);
        }
    }

    public List<Category> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Category ORDER BY name ASC", Category.class)
                    .list();
        } catch (Exception e) {
            logger.error("Error retrieving all categories", e);
            throw new AppException("Failed to retrieve categories", e);
        }
    }

    public void save(Category category) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(category);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error saving category: {}", category, e);
            throw new AppException("Failed to save category", e);
        }
    }

    public void update(Category category) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(category);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error updating category: {}", category, e);
            throw new AppException("Failed to update category", e);
        }
    }

    public void delete(Category category) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(category) ? category : session.merge(category));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error deleting category: {}", category, e);
            throw new AppException("Failed to delete category", e);
        }
    }
}
