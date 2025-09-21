package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Customer;
import app.swing.model.Order;
import app.swing.model.OrderItem;
import app.swing.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp service cho thực thể Đơn hàng
 */
public class OrderService {

    private CustomerService customerService;
    private OrderItemService orderItemService;
    private PaymentService paymentService;
    private ShipmentService shipmentService;

    public OrderService() {
        this.customerService = new CustomerService();
        this.orderItemService = new OrderItemService();
        this.paymentService = new PaymentService();
        this.shipmentService = new ShipmentService();
    }

    /**
     * Get all orders from database
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_number, customer_id, user_id, status, placed_at, " +
                         "delivery_date, subtotal, tax, discount, shipping_fee, total, notes, " +
                         "created_at, updated_at FROM orders ORDER BY placed_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);

                // Load customer information if customer_id is not null
                if (order.getCustomerId() != null) {
                    Customer customer = customerService.getCustomerById(order.getCustomerId());
                    order.setCustomer(customer);
                }

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Get order by ID with all related items, payments, and shipments
     * @param id Order ID
     * @return Order object if found, null otherwise
     */
    public Order getOrderById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_number, customer_id, user_id, status, placed_at, " +
                         "delivery_date, subtotal, tax, discount, shipping_fee, total, notes, " +
                         "created_at, updated_at FROM orders WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);

                // Load related entities
                if (order.getCustomerId() != null) {
                    Customer customer = customerService.getCustomerById(order.getCustomerId());
                    order.setCustomer(customer);
                }

                // Load order items
                List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(id);
                order.setOrderItems(orderItems);

                // Load payments
                order.setPayments(paymentService.getPaymentsByOrderId(id));

                // Load shipments
                order.setShipments(shipmentService.getShipmentsByOrderId(id));

                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new order with items, payments, and shipments
     * @param order Order object with data
     * @return true if successful, false otherwise
     */
    public boolean createOrder(Order order) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);

            // Generate order number if not provided
            if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
                order.setOrderNumber(generateOrderNumber());
            }

            // Calculate total if not set
            if (order.getTotal() == null || order.getTotal().doubleValue() == 0) {
                order.calculateTotal();
            }

            // Insert order
            String sql = "INSERT INTO orders (order_number, customer_id, user_id, status, " +
                         "placed_at, delivery_date, subtotal, tax, discount, shipping_fee, " +
                         "total, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, order.getOrderNumber());

            if (order.getCustomerId() != null) {
                stmt.setLong(2, order.getCustomerId());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            if (order.getUserId() != null) {
                stmt.setLong(3, order.getUserId());
            } else {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            stmt.setString(4, order.getStatus());

            if (order.getPlacedAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(order.getPlacedAt()));
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }

            if (order.getDeliveryDate() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(order.getDeliveryDate()));
            } else {
                stmt.setNull(6, java.sql.Types.TIMESTAMP);
            }

            stmt.setBigDecimal(7, order.getSubtotal());
            stmt.setBigDecimal(8, order.getTax());
            stmt.setBigDecimal(9, order.getDiscount());
            stmt.setBigDecimal(10, order.getShippingFee());
            stmt.setBigDecimal(11, order.getTotal());
            stmt.setString(12, order.getNotes());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    long orderId = rs.getLong(1);
                    order.setId(orderId);

                    // Insert order items
                    if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                        for (OrderItem item : order.getOrderItems()) {
                            item.setOrderId(orderId);
                            if (!orderItemService.createOrderItem(item, conn)) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }

                    // Insert payments
                    if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                        for (var payment : order.getPayments()) {
                            payment.setOrderId(orderId);
                            if (!paymentService.createPayment(payment, conn)) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }

                    // Insert shipments
                    if (order.getShipments() != null && !order.getShipments().isEmpty()) {
                        for (var shipment : order.getShipments()) {
                            shipment.setOrderId(orderId);
                            if (!shipmentService.createShipment(shipment, conn)) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update existing order
     * @param order Order object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateOrder(Order order) {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);

            // Calculate total if needed
            order.calculateTotal();

            // Update order
            String sql = "UPDATE orders SET customer_id = ?, user_id = ?, status = ?, " +
                         "placed_at = ?, delivery_date = ?, subtotal = ?, tax = ?, " +
                         "discount = ?, shipping_fee = ?, total = ?, notes = ?, " +
                         "updated_at = now() WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            if (order.getCustomerId() != null) {
                stmt.setLong(1, order.getCustomerId());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }

            if (order.getUserId() != null) {
                stmt.setLong(2, order.getUserId());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            stmt.setString(3, order.getStatus());

            if (order.getPlacedAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(order.getPlacedAt()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            if (order.getDeliveryDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(order.getDeliveryDate()));
            } else {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            }

            stmt.setBigDecimal(6, order.getSubtotal());
            stmt.setBigDecimal(7, order.getTax());
            stmt.setBigDecimal(8, order.getDiscount());
            stmt.setBigDecimal(9, order.getShippingFee());
            stmt.setBigDecimal(10, order.getTotal());
            stmt.setString(11, order.getNotes());
            stmt.setLong(12, order.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Handle order items (delete existing and insert new)
                orderItemService.deleteOrderItemsByOrderId(order.getId(), conn);

                if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                    for (OrderItem item : order.getOrderItems()) {
                        item.setOrderId(order.getId());
                        if (!orderItemService.createOrderItem(item, conn)) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // Handle payments (delete existing and insert new)
                paymentService.deletePaymentsByOrderId(order.getId(), conn);

                if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                    for (var payment : order.getPayments()) {
                        payment.setOrderId(order.getId());
                        if (!paymentService.createPayment(payment, conn)) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // Handle shipments (delete existing and insert new)
                shipmentService.deleteShipmentsByOrderId(order.getId(), conn);

                if (order.getShipments() != null && !order.getShipments().isEmpty()) {
                    for (var shipment : order.getShipments()) {
                        shipment.setOrderId(order.getId());
                        if (!shipmentService.createShipment(shipment, conn)) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Delete order by ID
     * @param id Order ID
     * @return true if successful, false otherwise
     */
    public boolean deleteOrder(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            // Due to CASCADE constraints in the database, deleting the order
            // will automatically delete related order items, payments, and shipments
            String sql = "DELETE FROM orders WHERE id = ?";
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
     * Search orders by order number, customer name, or status
     * @param searchTerm Search term to look for
     * @return List of matching orders
     */
    public List<Order> searchOrders(String searchTerm) {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT o.id, o.order_number, o.customer_id, o.user_id, o.status, " +
                         "o.placed_at, o.delivery_date, o.subtotal, o.tax, o.discount, " +
                         "o.shipping_fee, o.total, o.notes, o.created_at, o.updated_at " +
                         "FROM orders o " +
                         "LEFT JOIN customers c ON o.customer_id = c.id " +
                         "WHERE o.order_number ILIKE ? OR c.name ILIKE ? OR o.status ILIKE ? " +
                         "ORDER BY o.placed_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);

                // Load customer information if customer_id is not null
                if (order.getCustomerId() != null) {
                    Customer customer = customerService.getCustomerById(order.getCustomerId());
                    order.setCustomer(customer);
                }

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Get orders by customer ID
     * @param customerId Customer ID
     * @return List of orders for the customer
     */
    public List<Order> getOrdersByCustomerId(long customerId) {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, order_number, customer_id, user_id, status, placed_at, " +
                         "delivery_date, subtotal, tax, discount, shipping_fee, total, notes, " +
                         "created_at, updated_at FROM orders WHERE customer_id = ? " +
                         "ORDER BY placed_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, customerId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);

                // Load customer information if customer_id is not null
                if (order.getCustomerId() != null) {
                    Customer customer = customerService.getCustomerById(order.getCustomerId());
                    order.setCustomer(customer);
                }

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Generate a unique order number
     * @return Unique order number
     */
    private String generateOrderNumber() {
        // Format: ORD-YYYYMMDD-XXXX (where XXXX is a random number)
        String prefix = "ORD-";
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = 1000 + (int)(Math.random() * 9000); // Random 4-digit number

        return prefix + datePart + "-" + randomPart;
    }

    /**
     * Helper method to map ResultSet to Order object
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("order_number"));

        // Handle NULL customer_id
        Long customerId = rs.getLong("customer_id");
        if (rs.wasNull()) {
            customerId = null;
        }
        order.setCustomerId(customerId);

        // Handle NULL user_id
        Long userId = rs.getLong("user_id");
        if (rs.wasNull()) {
            userId = null;
        }
        order.setUserId(userId);

        order.setStatus(rs.getString("status"));

        Timestamp placedAt = rs.getTimestamp("placed_at");
        if (placedAt != null) {
            order.setPlacedAt(placedAt.toLocalDateTime());
        }

        Timestamp deliveryDate = rs.getTimestamp("delivery_date");
        if (deliveryDate != null) {
            order.setDeliveryDate(deliveryDate.toLocalDateTime());
        }

        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setTax(rs.getBigDecimal("tax"));
        order.setDiscount(rs.getBigDecimal("discount"));
        order.setShippingFee(rs.getBigDecimal("shipping_fee"));
        order.setTotal(rs.getBigDecimal("total"));
        order.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return order;
    }
}
