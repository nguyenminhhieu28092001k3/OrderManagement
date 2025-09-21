package app.swing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for OrderItem entity
 */
public class OrderItem {
    private long id;
    private long orderId;
    private Long productId;
    private String productName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal lineTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Reference to related entities
    private Order order;
    private Product product;

    // Constructors
    public OrderItem() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.lineTotal = BigDecimal.ZERO;
    }

    public OrderItem(long id, long orderId, Long productId, String productName, String sku,
                     int quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal lineTotal,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.lineTotal = lineTotal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            this.orderId = order.getId();
        }
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
            this.productName = product.getName();
            this.sku = product.getSku();
            this.unitPrice = product.getPrice();
            calculateLineTotal();
        }
    }

    public void calculateLineTotal() {
        if (unitPrice != null && quantity > 0) {
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

            // Apply discount if exists
            if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                total = total.subtract(discount);
            }

            // Ensure total is not negative
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }

            this.lineTotal = total;
        } else {
            this.lineTotal = BigDecimal.ZERO;
        }
    }

    @Override
    public String toString() {
        return productName + " (" + quantity + ")";
    }
}
