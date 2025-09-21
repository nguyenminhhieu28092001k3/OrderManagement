package app.swing.model;

import java.time.LocalDateTime;

/**
 * Model class for Category entity
 */
public class Category {
    private long id;
    private String name;
    private Long parentId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Parent category reference (optional)
    private Category parent;

    // Constructors
    public Category() {}

    public Category(long id, String name, Long parentId, String description,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
        if (parent != null) {
            this.parentId = parent.getId();
        }
    }

    public boolean hasParent() {
        return parentId != null && parentId > 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getFullPath() {
        if (parent != null) {
            return parent.getFullPath() + " > " + name;
        }
        return name;
    }
}
