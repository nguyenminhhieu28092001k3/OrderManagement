package app.swing.model;

import java.time.LocalDateTime;

/**
 * Lớp model cho thực thể Giao hàng
 */
public class Shipment {
    private long id;
    private long orderId;
    private String provider;
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String status;
    private LocalDateTime createdAt;

    // Reference to related entities
    private Order order;

    // Constructors
    public Shipment() {
        this.status = "pending";
    }

    public Shipment(long id, long orderId, String provider, String trackingNumber,
                    LocalDateTime shippedAt, LocalDateTime deliveredAt,
                    String status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.provider = provider;
        this.trackingNumber = trackingNumber;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            this.orderId = order.getId();
        }
    }

    public String getStatusDisplay() {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "shipped": return "Đã gửi hàng";
            case "in_transit": return "Đang vận chuyển";
            case "delivered": return "Đã giao hàng";
            case "returned": return "Đã trả lại";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    @Override
    public String toString() {
        return provider + " - " + (trackingNumber != null ? trackingNumber : "Chưa có mã");
    }
}
