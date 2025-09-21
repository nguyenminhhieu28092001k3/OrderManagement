package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Shipment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp service cho thực thể Giao hàng
 */
public class ShipmentService {

    /**
     * Get all shipments for a specific order
     * @param orderId Order ID
     * @return List of shipments
     */
    public List<Shipment> getShipmentsByOrderId(long orderId) {
        List<Shipment> shipments = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_id, provider, tracking_number, shipped_at, " +
                         "delivered_at, status, created_at FROM shipments " +
                         "WHERE order_id = ? ORDER BY created_at";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Shipment shipment = mapResultSetToShipment(rs);
                shipments.add(shipment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shipments;
    }

    /**
     * Get shipment by ID
     * @param id Shipment ID
     * @return Shipment object if found, null otherwise
     */
    public Shipment getShipmentById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_id, provider, tracking_number, shipped_at, " +
                         "delivered_at, status, created_at FROM shipments WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToShipment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new shipment
     * @param shipment Shipment object with data
     * @return true if successful, false otherwise
     */
    public boolean createShipment(Shipment shipment) {
        try (Connection conn = DbConnection.getConnection()) {
            return createShipment(shipment, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create new shipment with existing connection (for transactions)
     * @param shipment Shipment object with data
     * @param conn Existing database connection
     * @return true if successful, false otherwise
     */
    public boolean createShipment(Shipment shipment, Connection conn) {
        try {
            String sql = "INSERT INTO shipments (order_id, provider, tracking_number, " +
                         "shipped_at, delivered_at, status) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, shipment.getOrderId());
            stmt.setString(2, shipment.getProvider());
            stmt.setString(3, shipment.getTrackingNumber());

            if (shipment.getShippedAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(shipment.getShippedAt()));
            } else {
                stmt.setNull(4, java.sql.Types.TIMESTAMP);
            }

            if (shipment.getDeliveredAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(shipment.getDeliveredAt()));
            } else {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            }

            stmt.setString(6, shipment.getStatus());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    shipment.setId(rs.getLong(1));
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
     * Update existing shipment
     * @param shipment Shipment object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateShipment(Shipment shipment) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE shipments SET provider = ?, tracking_number = ?, " +
                         "shipped_at = ?, delivered_at = ?, status = ? WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, shipment.getProvider());
            stmt.setString(2, shipment.getTrackingNumber());

            if (shipment.getShippedAt() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(shipment.getShippedAt()));
            } else {
                stmt.setNull(3, java.sql.Types.TIMESTAMP);
            }

            if (shipment.getDeliveredAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(shipment.getDeliveredAt()));
            } else {
                stmt.setNull(4, java.sql.Types.TIMESTAMP);
            }

            stmt.setString(5, shipment.getStatus());
            stmt.setLong(6, shipment.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete shipment by ID
     * @param id Shipment ID
     * @return true if successful, false otherwise
     */
    public boolean deleteShipment(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "DELETE FROM shipments WHERE id = ?";
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
     * Delete all shipments for a specific order
     * @param orderId Order ID
     * @param conn Existing database connection
     * @return true if successful, false otherwise
     */
    public boolean deleteShipmentsByOrderId(long orderId, Connection conn) {
        try {
            String sql = "DELETE FROM shipments WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderId);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to map ResultSet to Shipment object
     */
    private Shipment mapResultSetToShipment(ResultSet rs) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setId(rs.getLong("id"));
        shipment.setOrderId(rs.getLong("order_id"));
        shipment.setProvider(rs.getString("provider"));
        shipment.setTrackingNumber(rs.getString("tracking_number"));

        Timestamp shippedAt = rs.getTimestamp("shipped_at");
        if (shippedAt != null) {
            shipment.setShippedAt(shippedAt.toLocalDateTime());
        }

        Timestamp deliveredAt = rs.getTimestamp("delivered_at");
        if (deliveredAt != null) {
            shipment.setDeliveredAt(deliveredAt.toLocalDateTime());
        }

        shipment.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            shipment.setCreatedAt(createdAt.toLocalDateTime());
        }

        return shipment;
    }
}
