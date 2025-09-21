package app.swing.model;

import java.time.LocalDateTime;

/**
 * Model class for InventoryMovement entity
 */
public class InventoryMovement {
    private long id;
    private long productId;
    private int changeQty;
    private String kind;
    private String referenceType;
    private Long referenceId;
    private String note;
    private Long createdBy;
    private Long userId;
    private LocalDateTime createdAt;

    // Reference objects
    private Product product;
    private User user;

    // Movement kinds enum
    public enum MovementKind {
        PURCHASE("purchase", "Nhập hàng"),
        SALE("sale", "Bán hàng"),
        ADJUSTMENT("adjustment", "Điều chỉnh"),
        RETURN("return", "Trả hàng");

        private String value;
        private String displayName;

        MovementKind(String value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public String getValue() {
            return value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static MovementKind fromValue(String value) {
            for (MovementKind kind : values()) {
                if (kind.value.equals(value)) {
                    return kind;
                }
            }
            return ADJUSTMENT; // default
        }
    }

    // Constructors
    public InventoryMovement() {}

    public InventoryMovement(long id, long productId, int changeQty, String kind,
                           String referenceType, Long referenceId, String note,
                           Long createdBy, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.changeQty = changeQty;
        this.kind = kind;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.note = note;
        this.createdBy = createdBy;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
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

    public MovementKind getMovementKind() {
        return MovementKind.fromValue(kind);
    }

    public void setMovementKind(MovementKind movementKind) {
        this.kind = movementKind.getValue();
    }

    public boolean isIncoming() {
        return changeQty > 0;
    }

    public boolean isOutgoing() {
        return changeQty < 0;
    }

    public String getMovementTypeDisplay() {
        MovementKind movementKind = getMovementKind();
        return movementKind.getDisplayName();
    }

    public String getQuantityDisplay() {
        if (changeQty > 0) {
            return "+" + changeQty;
        }
        return String.valueOf(changeQty);
    }

    @Override
    public String toString() {
        return getMovementTypeDisplay() + " - " + getQuantityDisplay();
    }
}
