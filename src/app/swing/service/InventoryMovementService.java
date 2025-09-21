package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.InventoryMovement;
import app.swing.model.Product;
import app.swing.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for InventoryMovement entity
 */
public class InventoryMovementService {

    private ProductService productService;
    private UserService userService;

    public InventoryMovementService() {
        this.productService = new ProductService();
        this.userService = new UserService();
    }

    /**
     * Get all inventory movements from database
     * @return List of all inventory movements
     */
    public List<InventoryMovement> getAllMovements() {
        List<InventoryMovement> movements = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, product_id, change_qty, kind, reference_type, reference_id, " +
                         "note, created_by, user_id, created_at " +
                         "FROM inventory_movements ORDER BY created_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                movements.add(movement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movements;
    }

    /**
     * Get inventory movements by product
     * @param productId Product ID
     * @return List of movements for the product
     */
    public List<InventoryMovement> getMovementsByProduct(long productId) {
        List<InventoryMovement> movements = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, product_id, change_qty, kind, reference_type, reference_id, " +
                         "note, created_by, user_id, created_at " +
                         "FROM inventory_movements WHERE product_id = ? ORDER BY created_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, productId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                movements.add(movement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movements;
    }

    /**
     * Get inventory movements by user
     * @param userId User ID
     * @return List of movements created by the user
     */
    public List<InventoryMovement> getMovementsByUser(long userId) {
        List<InventoryMovement> movements = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, product_id, change_qty, kind, reference_type, reference_id, " +
                         "note, created_by, user_id, created_at " +
                         "FROM inventory_movements WHERE user_id = ? ORDER BY created_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                movements.add(movement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movements;
    }

    /**
     * Get inventory movement by ID
     * @param id Movement ID
     * @return InventoryMovement object if found, null otherwise
     */
    public InventoryMovement getMovementById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, product_id, change_qty, kind, reference_type, reference_id, " +
                         "note, created_by, user_id, created_at " +
                         "FROM inventory_movements WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                return movement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new inventory movement
     * @param movement InventoryMovement object with data
     * @return true if successful, false otherwise
     */
    public boolean createMovement(InventoryMovement movement) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert inventory movement
            String sql = "INSERT INTO inventory_movements (product_id, change_qty, kind, reference_type, " +
                         "reference_id, note, created_by, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, movement.getProductId());
            stmt.setInt(2, movement.getChangeQty());
            stmt.setString(3, movement.getKind());
            stmt.setString(4, movement.getReferenceType());

            if (movement.getReferenceId() != null) {
                stmt.setLong(5, movement.getReferenceId());
            } else {
                stmt.setNull(5, java.sql.Types.BIGINT);
            }

            stmt.setString(6, movement.getNote());

            if (movement.getCreatedBy() != null) {
                stmt.setLong(7, movement.getCreatedBy());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }

            if (movement.getUserId() != null) {
                stmt.setLong(8, movement.getUserId());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    movement.setId(rs.getLong(1));
                }

                // Update product stock quantity
                updateProductStock(conn, movement.getProductId(), movement.getChangeQty());

                conn.commit(); // Commit transaction
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update existing inventory movement
     * @param movement InventoryMovement object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateMovement(InventoryMovement movement) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Get old movement to calculate stock difference
            InventoryMovement oldMovement = getMovementById(movement.getId());
            if (oldMovement == null) {
                conn.rollback();
                return false;
            }

            // Update inventory movement
            String sql = "UPDATE inventory_movements SET product_id = ?, change_qty = ?, kind = ?, " +
                         "reference_type = ?, reference_id = ?, note = ?, created_by = ?, user_id = ? " +
                         "WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, movement.getProductId());
            stmt.setInt(2, movement.getChangeQty());
            stmt.setString(3, movement.getKind());
            stmt.setString(4, movement.getReferenceType());

            if (movement.getReferenceId() != null) {
                stmt.setLong(5, movement.getReferenceId());
            } else {
                stmt.setNull(5, java.sql.Types.BIGINT);
            }

            stmt.setString(6, movement.getNote());

            if (movement.getCreatedBy() != null) {
                stmt.setLong(7, movement.getCreatedBy());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }

            if (movement.getUserId() != null) {
                stmt.setLong(8, movement.getUserId());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }

            stmt.setLong(9, movement.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Adjust product stock: reverse old movement and apply new movement
                int stockAdjustment = movement.getChangeQty() - oldMovement.getChangeQty();

                // If product changed, handle both products
                if (movement.getProductId() != oldMovement.getProductId()) {
                    // Reverse old product stock
                    updateProductStock(conn, oldMovement.getProductId(), -oldMovement.getChangeQty());
                    // Apply new product stock
                    updateProductStock(conn, movement.getProductId(), movement.getChangeQty());
                } else {
                    // Same product, just apply the difference
                    updateProductStock(conn, movement.getProductId(), stockAdjustment);
                }

                conn.commit(); // Commit transaction
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete inventory movement by ID
     * @param id Movement ID
     * @return true if successful, false otherwise
     */
    public boolean deleteMovement(long id) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Get movement to reverse stock change
            InventoryMovement movement = getMovementById(id);
            if (movement == null) {
                conn.rollback();
                return false;
            }

            // Delete the movement
            String sql = "DELETE FROM inventory_movements WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Reverse the stock change
                updateProductStock(conn, movement.getProductId(), -movement.getChangeQty());

                conn.commit(); // Commit transaction
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Search inventory movements
     * @param searchTerm Search term
     * @return List of matching movements
     */
    public List<InventoryMovement> searchMovements(String searchTerm) {
        List<InventoryMovement> movements = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT im.id, im.product_id, im.change_qty, im.kind, im.reference_type, " +
                         "im.reference_id, im.note, im.created_by, im.user_id, im.created_at " +
                         "FROM inventory_movements im " +
                         "JOIN products p ON im.product_id = p.id " +
                         "WHERE p.name ILIKE ? OR p.sku ILIKE ? OR im.note ILIKE ? OR im.kind ILIKE ? " +
                         "ORDER BY im.created_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                movements.add(movement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movements;
    }

    /**
     * Get movements by kind
     * @param kind Movement kind (purchase, sale, adjustment, return)
     * @return List of movements of the specified kind
     */
    public List<InventoryMovement> getMovementsByKind(String kind) {
        List<InventoryMovement> movements = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, product_id, change_qty, kind, reference_type, reference_id, " +
                         "note, created_by, user_id, created_at " +
                         "FROM inventory_movements WHERE kind = ? ORDER BY created_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kind);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                movements.add(movement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movements;
    }

    /**
     * Get recent inventory movements (last 30 days)
     * @return List of recent movements
     */
    public List<InventoryMovement> getRecentMovements() {
        List<InventoryMovement> movements = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, product_id, change_qty, kind, reference_type, reference_id, " +
                         "note, created_by, user_id, created_at " +
                         "FROM inventory_movements " +
                         "WHERE created_at >= CURRENT_DATE - INTERVAL '30 days' " +
                         "ORDER BY created_at DESC LIMIT 100";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryMovement movement = mapResultSetToMovement(rs);
                loadMovementRelations(movement);
                movements.add(movement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movements;
    }

    /**
     * Update product stock quantity
     * @param conn Database connection
     * @param productId Product ID
     * @param changeQty Quantity change (positive for increase, negative for decrease)
     * @throws SQLException if update fails
     */
    private void updateProductStock(Connection conn, long productId, int changeQty) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ?, updated_at = now() WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, changeQty);
        stmt.setLong(2, productId);

        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Failed to update product stock for product ID: " + productId);
        }
    }

    /**
     * Helper method to map ResultSet to InventoryMovement object
     */
    private InventoryMovement mapResultSetToMovement(ResultSet rs) throws SQLException {
        InventoryMovement movement = new InventoryMovement();
        movement.setId(rs.getLong("id"));
        movement.setProductId(rs.getLong("product_id"));
        movement.setChangeQty(rs.getInt("change_qty"));
        movement.setKind(rs.getString("kind"));
        movement.setReferenceType(rs.getString("reference_type"));

        // Handle NULL reference_id
        Long referenceId = rs.getLong("reference_id");
        if (rs.wasNull()) {
            referenceId = null;
        }
        movement.setReferenceId(referenceId);

        movement.setNote(rs.getString("note"));

        // Handle NULL created_by
        Long createdBy = rs.getLong("created_by");
        if (rs.wasNull()) {
            createdBy = null;
        }
        movement.setCreatedBy(createdBy);

        // Handle NULL user_id
        Long userId = rs.getLong("user_id");
        if (rs.wasNull()) {
            userId = null;
        }
        movement.setUserId(userId);

        movement.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return movement;
    }

    /**
     * Helper method to load product and user for a movement
     */
    private void loadMovementRelations(InventoryMovement movement) {
        // Load product
        Product product = productService.getProductById(movement.getProductId());
        if (product != null) {
            movement.setProduct(product);
        }

        // Load user if available
        if (movement.getUserId() != null) {
            User user = userService.getUserById(movement.getUserId().intValue());
            if (user != null) {
                movement.setUser(user);
            }
        }
    }
}
