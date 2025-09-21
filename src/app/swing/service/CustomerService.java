package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp service cho thực thể Khách hàng
 */
public class CustomerService {

    /**
     * Lấy tất cả khách hàng từ cơ sở dữ liệu
     * @return Danh sách tất cả khách hàng
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Database connection is null!");
                return customers;
            }

            String sql = "SELECT id, code, name, email, phone, address, note, is_active, " +
                         "created_at, updated_at FROM customers ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
                count++;
            }

            System.out.println("CustomerService: Successfully loaded " + count + " customers from database");

        } catch (SQLException e) {
            System.err.println("SQL Error in getAllCustomers: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getAllCustomers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Get only active customers
     * @return List of active customers
     */
    public List<Customer> getActiveCustomers() {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Database connection is null!");
                return customers;
            }

            String sql = "SELECT id, code, name, email, phone, address, note, is_active, " +
                         "created_at, updated_at FROM customers WHERE is_active = true " +
                         "ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
                count++;
            }

            System.out.println("CustomerService: Successfully loaded " + count + " active customers from database");

        } catch (SQLException e) {
            System.err.println("SQL Error in getActiveCustomers: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getActiveCustomers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Get customer by ID
     * @param id Customer ID
     * @return Customer object if found, null otherwise
     */
    public Customer getCustomerById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, code, name, email, phone, address, note, is_active, " +
                         "created_at, updated_at FROM customers WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new customer
     * @param customer Customer object with data
     * @return true if successful, false otherwise
     */
    public boolean createCustomer(Customer customer) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO customers (code, name, email, phone, address, note, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, customer.getCode());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getNote());
            stmt.setBoolean(7, customer.isActive());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    customer.setId(rs.getLong(1));
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing customer
     * @param customer Customer object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE customers SET code = ?, name = ?, email = ?, phone = ?, " +
                         "address = ?, note = ?, is_active = ?, updated_at = now() " +
                         "WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, customer.getCode());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getNote());
            stmt.setBoolean(7, customer.isActive());
            stmt.setLong(8, customer.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete customer by ID
     * @param id Customer ID
     * @return true if successful, false otherwise
     */
    public boolean deleteCustomer(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            // Check if customer is referenced by orders
            String checkSql = "SELECT COUNT(*) FROM orders WHERE customer_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setLong(1, id);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                // Customer is used by orders, can't delete
                return false;
            }

            // If not being used, proceed with deletion
            String sql = "DELETE FROM customers WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Search customers by name, code, email or phone
     * @param searchTerm Search term to look for
     * @return List of matching customers
     */
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, code, name, email, phone, address, note, is_active, " +
                         "created_at, updated_at FROM customers " +
                         "WHERE name ILIKE ? OR code ILIKE ? OR email ILIKE ? OR phone ILIKE ? " +
                         "ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Check if customer code already exists (for validation)
     * @param code Customer code to check
     * @param excludeId Optional ID to exclude from check (for updates)
     * @return true if code already exists, false otherwise
     */
    public boolean codeExists(String code, Long excludeId) {
        // If code is null or empty, it's valid
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM customers WHERE code = ?";
            if (excludeId != null) {
                sql += " AND id != ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);

            if (excludeId != null) {
                stmt.setLong(2, excludeId);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if customer email already exists (for validation)
     * @param email Customer email to check
     * @param excludeId Optional ID to exclude from check (for updates)
     * @return true if email already exists, false otherwise
     */
    public boolean emailExists(String email, Long excludeId) {
        // If email is null or empty, it's valid
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
            if (excludeId != null) {
                sql += " AND id != ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            if (excludeId != null) {
                stmt.setLong(2, excludeId);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Helper method to map ResultSet to Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setCode(rs.getString("code"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setNote(rs.getString("note"));
        customer.setActive(rs.getBoolean("is_active"));

        // Handle potential null timestamps
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            customer.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            customer.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }

        return customer;
    }
}
