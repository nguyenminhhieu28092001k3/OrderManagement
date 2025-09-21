package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Supplier entity
 */
public class SupplierService {

    /**
     * Get all suppliers from database
     * @return List of all suppliers
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, contact_name, email, phone, address, note, " +
                        "created_at, updated_at FROM suppliers ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    /**
     * Get supplier by ID
     * @param id Supplier ID
     * @return Supplier object if found, null otherwise
     */
    public Supplier getSupplierById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, contact_name, email, phone, address, note, " +
                        "created_at, updated_at FROM suppliers WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSupplier(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new supplier
     * @param supplier Supplier object with data
     * @return true if successful, false otherwise
     */
    public boolean createSupplier(Supplier supplier) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO suppliers (name, contact_name, email, phone, address, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactName());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getNote());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    supplier.setId(rs.getLong(1));
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
     * Update existing supplier
     * @param supplier Supplier object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateSupplier(Supplier supplier) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE suppliers SET name = ?, contact_name = ?, email = ?, phone = ?, " +
                        "address = ?, note = ?, updated_at = now() WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactName());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getNote());
            stmt.setLong(7, supplier.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete supplier by ID
     * @param id Supplier ID
     * @return true if successful, false otherwise
     */
    public boolean deleteSupplier(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            // Check if supplier is being used by products
            String checkSql = "SELECT COUNT(*) FROM products WHERE supplier_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setLong(1, id);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                // Supplier is being used by products, can't delete
                return false;
            }

            // If not being used, proceed with deletion
            String sql = "DELETE FROM suppliers WHERE id = ?";
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
     * Search suppliers by name or contact information
     * @param searchTerm Search term to look for
     * @return List of matching suppliers
     */
    public List<Supplier> searchSuppliers(String searchTerm) {
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, contact_name, email, phone, address, note, " +
                        "created_at, updated_at FROM suppliers " +
                        "WHERE name ILIKE ? OR contact_name ILIKE ? OR email ILIKE ? OR phone ILIKE ? " +
                        "ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    /**
     * Check if supplier name already exists (for validation)
     * @param name Supplier name to check
     * @param excludeId Optional ID to exclude from check (for updates)
     * @return true if name already exists, false otherwise
     */
    public boolean nameExists(String name, Long excludeId) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM suppliers WHERE name = ?";
            if (excludeId != null) {
                sql += " AND id != ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);

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
     * Helper method to map ResultSet to Supplier object
     */
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getLong("id"));
        supplier.setName(rs.getString("name"));
        supplier.setContactName(rs.getString("contact_name"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        supplier.setNote(rs.getString("note"));
        supplier.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        supplier.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return supplier;
    }
}
