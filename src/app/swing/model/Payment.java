package app.swing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lớp model cho thực thể Thanh toán
 */
public class Payment {
    private long id;
    private long orderId;
    private BigDecimal amount;
    private String method;
    private String reference;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    // Reference to related entities
    private Order order;

    // Constructors
    public Payment() {
        this.amount = BigDecimal.ZERO;
        this.method = "cash";
        this.paidAt = LocalDateTime.now();
    }

    public Payment(long id, long orderId, BigDecimal amount, String method,
                   String reference, LocalDateTime paidAt, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.reference = reference;
        this.paidAt = paidAt;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
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

    public String getMethodDisplay() {
        switch (method) {
            case "cash": return "Tiền mặt";
            case "card": return "Thẻ";
            case "bank_transfer": return "Chuyển khoản";
            case "wallet": return "Ví điện tử";
            case "other": return "Khác";
            default: return method;
        }
    }

    @Override
    public String toString() {
        return getMethodDisplay() + ": " + amount;
    }
}
