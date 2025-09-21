package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Payment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp service cho thực thể Thanh toán
 */
public class PaymentService {

    /**
     * Get all payments for a specific order
     * @param orderId Order ID
     * @return List of payments
     */
    public List<Payment> getPaymentsByOrderId(long orderId) {
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_id, amount, method, reference, paid_at, created_at " +
                         "FROM payments WHERE order_id = ? ORDER BY paid_at";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment payment = mapResultSetToPayment(rs);
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Get payment by ID
     * @param id Payment ID
     * @return Payment object if found, null otherwise
     */
    public Payment getPaymentById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_id, amount, method, reference, paid_at, created_at " +
                         "FROM payments WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new payment
     * @param payment Payment object with data
     * @return true if successful, false otherwise
     */
    public boolean createPayment(Payment payment) {
        try (Connection conn = DbConnection.getConnection()) {
            return createPayment(payment, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create new payment with existing connection (for transactions)
     * @param payment Payment object with data
     * @param conn Existing database connection
     * @return true if successful, false otherwise
     */
    public boolean createPayment(Payment payment, Connection conn) {
        try {
            String sql = "INSERT INTO payments (order_id, amount, method, reference, paid_at) " +
                         "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, payment.getOrderId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getMethod());
            stmt.setString(4, payment.getReference());

            if (payment.getPaidAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaidAt()));
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    payment.setId(rs.getLong(1));
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
     * Update existing payment
     * @param payment Payment object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updatePayment(Payment payment) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE payments SET amount = ?, method = ?, reference = ?, paid_at = ? " +
                         "WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBigDecimal(1, payment.getAmount());
            stmt.setString(2, payment.getMethod());
            stmt.setString(3, payment.getReference());

            if (payment.getPaidAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(payment.getPaidAt()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            stmt.setLong(5, payment.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete payment by ID
     * @param id Payment ID
     * @return true if successful, false otherwise
     */
    public boolean deletePayment(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "DELETE FROM payments WHERE id = ?";
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
     * Delete all payments for a specific order
     * @param orderId Order ID
     * @param conn Existing database connection
     * @return true if successful, false otherwise
     */
    public boolean deletePaymentsByOrderId(long orderId, Connection conn) {
        try {
            String sql = "DELETE FROM payments WHERE order_id = ?";
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
     * Helper method to map ResultSet to Payment object
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setOrderId(rs.getLong("order_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setMethod(rs.getString("method"));
        payment.setReference(rs.getString("reference"));

        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) {
            payment.setPaidAt(paidAt.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            payment.setCreatedAt(createdAt.toLocalDateTime());
        }

        return payment;
    }
}
