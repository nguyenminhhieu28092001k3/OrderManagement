/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.controller;

import app.swing.configuration.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * Base controller chứa logic search và pagination
 * @author khaim
 */
public abstract class BaseSearchController<T> {
    private final Class<T> entityClass;

    public BaseSearchController(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Lấy danh sách có phân trang
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param pageSize Số lượng bản ghi trên mỗi trang
     * @return Danh sách entity theo trang
     */
    @SuppressWarnings("unchecked")
    public List<T> findAllWithPagination(int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy danh sách có phân trang với điều kiện
     * @param hqlCondition Điều kiện HQL (WHERE clause)
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param pageSize Số lượng bản ghi trên mỗi trang
     * @param params Tham số cho điều kiện
     * @return Danh sách entity theo trang và điều kiện
     */
    @SuppressWarnings("unchecked")
    public List<T> findByConditionWithPagination(String hqlCondition, int page, int pageSize, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName() + " WHERE " + hqlCondition, entityClass);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            // Set pagination
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
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

    /**
     * Tính tổng số trang
     * @param pageSize Số lượng bản ghi trên mỗi trang
     * @return Tổng số trang
     */
    public int getTotalPages(int pageSize) {
        long totalRecords = countAll();
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    /**
     * Tính tổng số trang theo điều kiện
     * @param hqlCondition Điều kiện HQL
     * @param pageSize Số lượng bản ghi trên mỗi trang
     * @param params Tham số cho điều kiện
     * @return Tổng số trang
     */
    public int getTotalPagesByCondition(String hqlCondition, int pageSize, Object... params) {
        long totalRecords = countByCondition(hqlCondition, params);
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    /**
     * Lấy danh sách có phân trang và sắp xếp
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param pageSize Số lượng bản ghi trên mỗi trang
     * @param orderBy Trường sắp xếp
     * @param ascending true = tăng dần, false = giảm dần
     * @return Danh sách entity đã sắp xếp và phân trang
     */
    @SuppressWarnings("unchecked")
    public List<T> findAllWithPaginationAndSort(int page, int pageSize, String orderBy, boolean ascending) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            String hql = "FROM " + entityClass.getSimpleName() + " ORDER BY " + orderBy + " " + order;
            
            Query<T> query = session.createQuery(hql, entityClass);
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Thực hiện phân trang với HQL custom (có thể có JOIN)
     * @param customHql HQL query tùy chỉnh (không có LIMIT/OFFSET)
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param pageSize Số lượng bản ghi trên mỗi trang
     * @param params Tham số cho query
     * @return Danh sách entity theo trang
     */
    @SuppressWarnings("unchecked")
    public List<T> findWithCustomHQLPagination(String customHql, int page, int pageSize, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery(customHql, entityClass);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            // Set pagination
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Đếm số bản ghi với HQL custom
     * @param countHql HQL query để đếm
     * @param params Tham số cho query
     * @return Số lượng bản ghi
     */
    public long countWithCustomHQL(String countHql, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(countHql, Long.class);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            return query.uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Phân trang với JOIN sử dụng INNER JOIN
     * @param joinEntity Tên entity được join
     * @param joinCondition Điều kiện join (ON clause)
     * @param whereCondition Điều kiện WHERE (có thể null)
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @param params Tham số
     * @return Danh sách entity
     */
    @SuppressWarnings("unchecked")
    public List<T> findWithInnerJoinPagination(String joinEntity, String joinCondition, String whereCondition, int page, int pageSize, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder();
            hql.append("SELECT DISTINCT e FROM ").append(entityClass.getSimpleName()).append(" e ");
            hql.append("INNER JOIN ").append(joinEntity).append(" j ");
            hql.append("ON ").append(joinCondition);
            
            if (whereCondition != null && !whereCondition.trim().isEmpty()) {
                hql.append(" WHERE ").append(whereCondition);
            }
            
            Query<T> query = session.createQuery(hql.toString(), entityClass);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Đếm bản ghi với INNER JOIN
     * @param joinEntity Tên entity được join
     * @param joinCondition Điều kiện join
     * @param whereCondition Điều kiện WHERE
     * @param params Tham số
     * @return Số lượng bản ghi
     */
    public long countWithInnerJoin(String joinEntity, String joinCondition, String whereCondition, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder();
            hql.append("SELECT COUNT(DISTINCT e) FROM ").append(entityClass.getSimpleName()).append(" e ");
            hql.append("INNER JOIN ").append(joinEntity).append(" j ");
            hql.append("ON ").append(joinCondition);
            
            if (whereCondition != null && !whereCondition.trim().isEmpty()) {
                hql.append(" WHERE ").append(whereCondition);
            }
            
            Query<Long> query = session.createQuery(hql.toString(), Long.class);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            return query.uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Phân trang với LEFT JOIN
     * @param joinEntity Tên entity được join
     * @param joinCondition Điều kiện join
     * @param whereCondition Điều kiện WHERE
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @param params Tham số
     * @return Danh sách entity
     */
    @SuppressWarnings("unchecked")
    public List<T> findWithLeftJoinPagination(String joinEntity, String joinCondition, String whereCondition, int page, int pageSize, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder();
            hql.append("SELECT DISTINCT e FROM ").append(entityClass.getSimpleName()).append(" e ");
            hql.append("LEFT JOIN ").append(joinEntity).append(" j ");
            hql.append("ON ").append(joinCondition);
            
            if (whereCondition != null && !whereCondition.trim().isEmpty()) {
                hql.append(" WHERE ").append(whereCondition);
            }
            
            Query<T> query = session.createQuery(hql.toString(), entityClass);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Thực hiện phân trang với Native SQL (cho các truy vấn SQL phức tạp)
     * @param nativeSql Native SQL query
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @param params Tham số
     * @return Danh sách entity
     */
    @SuppressWarnings("unchecked")
    public List<T> findWithNativeSQLPagination(String nativeSql, int page, int pageSize, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createNativeQuery(nativeSql, entityClass);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]); // Native SQL sử dụng 1-based index
            }
            
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Đếm với Native SQL
     * @param countSql Native SQL để đếm
     * @param params Tham số
     * @return Số lượng bản ghi
     */
    public long countWithNativeSQL(String countSql, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Number> query = session.createNativeQuery(countSql);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            
            Number result = query.uniqueResult();
            return result != null ? result.longValue() : 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Generic method cho phân trang với nhiều JOIN
     * @param selectClause SELECT clause (ví dụ: "DISTINCT e")
     * @param fromClause FROM clause với JOIN (ví dụ: "SanPham e INNER JOIN DoanhNghiep d ON e.idDoanhNghiep = d.id")
     * @param whereClause WHERE clause (có thể null)
     * @param orderByClause ORDER BY clause (có thể null)
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @param params Tham số
     * @return Danh sách entity
     */
    @SuppressWarnings("unchecked")
    public List<T> findWithComplexJoinPagination(String selectClause, String fromClause, String whereClause, 
                                                String orderByClause, int page, int pageSize, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder();
            hql.append("SELECT ").append(selectClause).append(" FROM ").append(fromClause);
            
            if (whereClause != null && !whereClause.trim().isEmpty()) {
                hql.append(" WHERE ").append(whereClause);
            }
            
            if (orderByClause != null && !orderByClause.trim().isEmpty()) {
                hql.append(" ORDER BY ").append(orderByClause);
            }
            
            Query<T> query = session.createQuery(hql.toString(), entityClass);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            
            return query.list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Đếm với complex JOIN
     * @param countFromClause FROM clause cho count
     * @param whereClause WHERE clause
     * @param params Tham số
     * @return Số lượng bản ghi
     */
    public long countWithComplexJoin(String countFromClause, String whereClause, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder();
            hql.append("SELECT COUNT(DISTINCT e) FROM ").append(countFromClause);
            
            if (whereClause != null && !whereClause.trim().isEmpty()) {
                hql.append(" WHERE ").append(whereClause);
            }
            
            Query<Long> query = session.createQuery(hql.toString(), Long.class);
            
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            
            return query.uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Inner class để trả về kết quả phân trang
     */
    public static class PageResult<T> {
        private List<T> data;
        private int currentPage;
        private int pageSize;
        private long totalRecords;
        private int totalPages;

        public PageResult(List<T> data, int currentPage, int pageSize, long totalRecords) {
            this.data = data;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalRecords = totalRecords;
            this.totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        }

        // Getters
        public List<T> getData() { return data; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
        public long getTotalRecords() { return totalRecords; }
        public int getTotalPages() { return totalPages; }
        public boolean hasNext() { return currentPage < totalPages - 1; }
        public boolean hasPrevious() { return currentPage > 0; }
    }

    /**
     * Lấy kết quả phân trang đầy đủ thông tin
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @return PageResult chứa đầy đủ thông tin phân trang
     */
    public PageResult<T> getPageResult(int page, int pageSize) {
        List<T> data = findAllWithPagination(page, pageSize);
        long totalRecords = countAll();
        return new PageResult<>(data, page, pageSize, totalRecords);
    }

    /**
     * Lấy kết quả phân trang với điều kiện
     * @param hqlCondition Điều kiện HQL
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @param params Tham số
     * @return PageResult chứa đầy đủ thông tin phân trang
     */
    public PageResult<T> getPageResultByCondition(String hqlCondition, int page, int pageSize, Object... params) {
        List<T> data = findByConditionWithPagination(hqlCondition, page, pageSize, params);
        long totalRecords = countByCondition(hqlCondition, params);
        return new PageResult<>(data, page, pageSize, totalRecords);
    }

    /**
     * Lấy kết quả phân trang với HQL custom
     * @param customHql HQL query tùy chỉnh
     * @param countHql HQL query để đếm
     * @param page Trang hiện tại
     * @param pageSize Kích thước trang
     * @param params Tham số
     * @return PageResult với HQL custom
     */
    public PageResult<T> getPageResultWithCustomHQL(String customHql, String countHql, int page, int pageSize, Object... params) {
        List<T> data = findWithCustomHQLPagination(customHql, page, pageSize, params);
        long totalRecords = countWithCustomHQL(countHql, params);
        return new PageResult<>(data, page, pageSize, totalRecords);
    }

    /**
     * Tìm kiếm entity theo ID
     * @param id ID của entity
     * @return Entity nếu tìm thấy, null nếu không tìm thấy
     */
    public T findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
