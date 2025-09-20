/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.controller;

import app.swing.configuration.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Base controller chứa các CRUD operations cơ bản
 * @author khaim
 */
public abstract class BaseController<T> {
    private final Class<T> entityClass;

    public BaseController(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    /**
     * Thêm mới entity
     * @param entity Entity cần thêm
     * @return ID của entity vừa được thêm
     */
    public Serializable add(T entity){
        Transaction tx = null;
        Serializable id = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();
            id = session.save(entity);
            tx.commit();
        }catch(Exception ex){
            if(tx != null) tx.rollback();
            ex.printStackTrace();
        }
        return id;
    }

    /**
     * Cập nhật entity
     * @param entity Entity cần cập nhật
     */
    public void update(T entity){
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        }catch(Exception ex){
            if(tx != null) tx.rollback();
            ex.printStackTrace();
        }
    }

    /**
     * Xóa entity
     * @param entity Entity cần xóa
     */
    public void remove(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        }
    }

    /**
     * Tìm entity theo ID
     * @param id ID của entity
     * @return Entity tìm được hoặc null
     */
    public T findById(Serializable id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        }
    }

    /**
     * Lấy tất cả entity
     * @return Danh sách tất cả entity
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName()).list();
        }
    }

    /**
     * Tìm entity theo điều kiện
     * @param hqlCondition Điều kiện HQL (WHERE clause)
     * @param params Tham số cho điều kiện
     * @return Danh sách entity thỏa mãn điều kiện
     */
    public List<T> findByCondition(String hqlCondition, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName() + " WHERE " + hqlCondition, entityClass);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Đếm tổng số bản ghi
     * @return Tổng số bản ghi
     */
    public long countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName(), Long.class);
            return query.uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Đếm số bản ghi theo điều kiện
     * @param hqlCondition Điều kiện HQL
     * @param params Tham số cho điều kiện
     * @return Số bản ghi thỏa mãn điều kiện
     */
    public long countByCondition(String hqlCondition, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM " + entityClass.getSimpleName() + " WHERE " + hqlCondition, Long.class);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            return query.uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
