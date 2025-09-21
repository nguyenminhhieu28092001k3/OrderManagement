package app.swing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for Product entity
 */
public class Product {
    private long id;
    private String sku;
    private String name;
    private String description;
    private Long categoryId;
    private Long supplierId;
    private BigDecimal price;
    private BigDecimal cost;
    private int stockQuantity;
    private int reorderLevel;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Reference to category and supplier
    private Category category;
    private Supplier supplier;

    // Constructors
    public Product() {}

    public Product(long id, String sku, String name, String description, Long categoryId,
                   Long supplierId, BigDecimal price, BigDecimal cost, int stockQuantity,
                   int reorderLevel, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.price = price;
        this.cost = cost;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.active = active;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        if (supplier != null) {
            this.supplierId = supplier.getId();
        }
    }

    public BigDecimal getProfit() {
        if (price != null && cost != null) {
            return price.subtract(cost);
        }
        return BigDecimal.ZERO;
    }

    public boolean isLowStock() {
        return stockQuantity <= reorderLevel;
    }

    @Override
    public String toString() {
        return name;
    }
}
