package app.swing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for Order entity
 */
public class Order {
    private long id;
    private String orderNumber;
    private Long customerId;
    private Long userId;
    private String status;
    private LocalDateTime placedAt;
    private LocalDateTime deliveryDate;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal shippingFee;
    private BigDecimal total;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Reference to related entities
    private Customer customer;
    private User user;
    private List<OrderItem> orderItems;
    private List<Payment> payments;
    private List<Shipment> shipments;

    // Constructors
    public Order() {
        this.orderItems = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.shipments = new ArrayList<>();
        this.status = "pending";
        this.placedAt = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.shippingFee = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    public Order(long id, String orderNumber, Long customerId, Long userId, String status,
                 LocalDateTime placedAt, LocalDateTime deliveryDate, BigDecimal subtotal,
                 BigDecimal tax, BigDecimal discount, BigDecimal shippingFee, BigDecimal total,
                 String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.userId = userId;
        this.status = status;
        this.placedAt = placedAt;
        this.deliveryDate = deliveryDate;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.shippingFee = shippingFee;
        this.total = total;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.shipments = new ArrayList<>();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = (long) user.getId();
        }
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem item) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(item);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void addPayment(Payment payment) {
        if (this.payments == null) {
            this.payments = new ArrayList<>();
        }
        this.payments.add(payment);
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public void addShipment(Shipment shipment) {
        if (this.shipments == null) {
            this.shipments = new ArrayList<>();
        }
        this.shipments.add(shipment);
    }

    public BigDecimal calculateTotal() {
        // Calculate subtotal from order items
        BigDecimal newSubtotal = BigDecimal.ZERO;
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                newSubtotal = newSubtotal.add(item.getLineTotal());
            }
        }

        // Set subtotal
        this.subtotal = newSubtotal;

        // Calculate total
        BigDecimal newTotal = newSubtotal;

        // Add tax
        if (tax != null) {
            newTotal = newTotal.add(tax);
        }

        // Subtract discount
        if (discount != null) {
            newTotal = newTotal.subtract(discount);
        }

        // Add shipping fee
        if (shippingFee != null) {
            newTotal = newTotal.add(shippingFee);
        }

        // Set and return total
        this.total = newTotal;
        return newTotal;
    }

    public String getStatusDisplay() {
        switch (status) {
            case "draft": return "Nháp";
            case "pending": return "Chờ xử lý";
            case "paid": return "Đã thanh toán";
            case "shipped": return "Đã gửi hàng";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            case "refunded": return "Đã hoàn tiền";
            default: return status;
        }
    }

    @Override
    public String toString() {
        return orderNumber;
    }
}
