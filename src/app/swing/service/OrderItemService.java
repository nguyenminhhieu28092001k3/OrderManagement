package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.OrderItem;
import app.swing.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for OrderItem entity
 */
public class OrderItemService {

    private ProductService productService;

    public OrderItemService() {
        this.productService = new ProductService();
    }

    /**
     * Get all order items for a specific order
     * @param orderId Order ID
     * @return List of order items
     */
    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        List<OrderItem> orderItems = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_id, product_id, product_name, sku, quantity, " +
                         "unit_price, discount, line_total, created_at, updated_at " +
                         "FROM order_items WHERE order_id = ? ORDER BY id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, orderId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = mapResultSetToOrderItem(rs);

                // Load product if available
                if (item.getProductId() != null) {
                    Product product = productService.getProductById(item.getProductId());
                    item.setProduct(product);
                }

                orderItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderItems;
    }

    /**
     * Get order item by ID
     * @param id Order item ID
     * @return OrderItem object if found, null otherwise
     */
    public OrderItem getOrderItemById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_id, product_id, product_name, sku, quantity, " +
                         "unit_price, discount, line_total, created_at, updated_at " +
                         "FROM order_items WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                OrderItem item = mapResultSetToOrderItem(rs);

                // Load product if available
                if (item.getProductId() != null) {
                    Product product = productService.getProductById(item.getProductId());
                    item.setProduct(product);
                }

                return item;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new order item
     * @param item OrderItem object with data
     * @return true if successful, false otherwise
     */
    public boolean createOrderItem(OrderItem item) {
        try (Connection conn = DbConnection.getConnection()) {
            return createOrderItem(item, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create new order item with existing connection (for transactions)
     * @param item OrderItem object with data
     * @param conn Existing database connection
     * @return true if successful, false otherwise
     */
    public boolean createOrderItem(OrderItem item, Connection conn) {
        try {
            // Calculate line total if not set
            if (item.getLineTotal() == null || item.getLineTotal().doubleValue() == 0) {
                item.calculateLineTotal();
            }

            String sql = "INSERT INTO order_items (order_id, product_id, product_name, sku, " +
                         "quantity, unit_price, discount, line_total) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, item.getOrderId());

            if (item.getProductId() != null) {
                stmt.setLong(2, item.getProductId());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            stmt.setString(3, item.getProductName());
            stmt.setString(4, item.getSku());
            stmt.setInt(5, item.getQuantity());
            stmt.setBigDecimal(6, item.getUnitPrice());
            stmt.setBigDecimal(7, item.getDiscount());
            stmt.setBigDecimal(8, item.getLineTotal());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    item.setId(rs.getLong(1));
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
     * Update existing order item
     * @param item OrderItem object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateOrderItem(OrderItem item) {
        try (Connection conn = DbConnection.getConnection()) {
            // Calculate line total
            item.calculateLineTotal();

            String sql = "UPDATE order_items SET product_id = ?, product_name = ?, sku = ?, " +
                         "quantity = ?, unit_price = ?, discount = ?, line_total = ?, " +
                         "updated_at = now() WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            if (item.getProductId() != null) {
                stmt.setLong(1, item.getProductId());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }

            stmt.setString(2, item.getProductName());
            stmt.setString(3, item.getSku());
            stmt.setInt(4, item.getQuantity());
            stmt.setBigDecimal(5, item.getUnitPrice());
            stmt.setBigDecimal(6, item.getDiscount());
            stmt.setBigDecimal(7, item.getLineTotal());
            stmt.setLong(8, item.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete order item by ID
     * @param id Order item ID
     * @return true if successful, false otherwise
     */
    public boolean deleteOrderItem(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "DELETE FROM order_items WHERE id = ?";
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
     * Delete all order items for a specific order
     * @param orderId Order ID
     * @param conn Existing database connection
     * @return true if successful, false otherwise
     */
    public boolean deleteOrderItemsByOrderId(long orderId, Connection conn) {
        try {
            String sql = "DELETE FROM order_items WHERE order_id = ?";
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
     * Helper method to map ResultSet to OrderItem object
     */
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));

        // Handle NULL product_id
        Long productId = rs.getLong("product_id");
        if (rs.wasNull()) {
            productId = null;
        }
        item.setProductId(productId);

        item.setProductName(rs.getString("product_name"));
        item.setSku(rs.getString("sku"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setDiscount(rs.getBigDecimal("discount"));
        item.setLineTotal(rs.getBigDecimal("line_total"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            item.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return item;
    }
}
