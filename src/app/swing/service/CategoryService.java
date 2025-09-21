package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp service cho thực thể Danh mục
 */
public class CategoryService {

    /**
     * Get all categories from database
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        Map<Long, Category> categoryMap = new HashMap<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, parent_id, description, created_at, updated_at " +
                         "FROM categories ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // First pass: create category objects
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
                categoryMap.put(category.getId(), category);
            }

            // Second pass: set parent references
            for (Category category : categories) {
                if (category.hasParent()) {
                    Category parent = categoryMap.get(category.getParentId());
                    if (parent != null) {
                        category.setParent(parent);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Get root categories (categories with no parent)
     * @return List of root categories
     */
    public List<Category> getRootCategories() {
        List<Category> rootCategories = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, parent_id, description, created_at, updated_at " +
                         "FROM categories WHERE parent_id IS NULL ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                rootCategories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rootCategories;
    }

    /**
     * Get subcategories for a parent category
     * @param parentId Parent category ID
     * @return List of child categories
     */
    public List<Category> getSubcategories(long parentId) {
        List<Category> subcategories = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, parent_id, description, created_at, updated_at " +
                         "FROM categories WHERE parent_id = ? ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, parentId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                subcategories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subcategories;
    }

    /**
     * Get category by ID
     * @param id Category ID
     * @return Category object if found, null otherwise
     */
    public Category getCategoryById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, parent_id, description, created_at, updated_at " +
                         "FROM categories WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Category category = mapResultSetToCategory(rs);

                // If category has parent, fetch parent data
                if (category.hasParent()) {
                    Category parent = getCategoryById(category.getParentId());
                    if (parent != null) {
                        category.setParent(parent);
                    }
                }

                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new category
     * @param category Category object with data
     * @return true if successful, false otherwise
     */
    public boolean createCategory(Category category) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO categories (name, parent_id, description) VALUES (?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, category.getName());

            if (category.getParentId() != null) {
                stmt.setLong(2, category.getParentId());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            stmt.setString(3, category.getDescription());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    category.setId(rs.getLong(1));
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
     * Update existing category
     * @param category Category object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        try (Connection conn = DbConnection.getConnection()) {
            // Check for circular reference
            if (category.getParentId() != null && category.getParentId() == category.getId()) {
                return false; // Can't set itself as parent
            }

            String sql = "UPDATE categories SET name = ?, parent_id = ?, description = ?, " +
                         "updated_at = now() WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category.getName());

            if (category.getParentId() != null) {
                stmt.setLong(2, category.getParentId());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }

            stmt.setString(3, category.getDescription());
            stmt.setLong(4, category.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete category by ID
     * @param id Category ID
     * @return true if successful, false otherwise
     */
    public boolean deleteCategory(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            // Check if category has child categories
            String checkChildrenSql = "SELECT COUNT(*) FROM categories WHERE parent_id = ?";
            PreparedStatement checkChildrenStmt = conn.prepareStatement(checkChildrenSql);
            checkChildrenStmt.setLong(1, id);
            ResultSet checkChildrenRs = checkChildrenStmt.executeQuery();

            if (checkChildrenRs.next() && checkChildrenRs.getInt(1) > 0) {
                // Category has children, can't delete
                return false;
            }

            // Check if category is being used by products
            String checkProductsSql = "SELECT COUNT(*) FROM products WHERE category_id = ?";
            PreparedStatement checkProductsStmt = conn.prepareStatement(checkProductsSql);
            checkProductsStmt.setLong(1, id);
            ResultSet checkProductsRs = checkProductsStmt.executeQuery();

            if (checkProductsRs.next() && checkProductsRs.getInt(1) > 0) {
                // Category is being used by products, can't delete
                return false;
            }

            // If not being used, proceed with deletion
            String sql = "DELETE FROM categories WHERE id = ?";
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
     * Search categories by name or description
     * @param searchTerm Search term to look for
     * @return List of matching categories
     */
    public List<Category> searchCategories(String searchTerm) {
        List<Category> categories = new ArrayList<>();
        Map<Long, Category> categoryMap = new HashMap<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, name, parent_id, description, created_at, updated_at " +
                         "FROM categories WHERE name ILIKE ? OR description ILIKE ? " +
                         "ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();

            // First pass: create category objects
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
                categoryMap.put(category.getId(), category);
            }

            // Second pass: set parent references
            for (Category category : categories) {
                if (category.hasParent()) {
                    // If parent is in our results, use it
                    Category parent = categoryMap.get(category.getParentId());
                    if (parent != null) {
                        category.setParent(parent);
                    } else {
                        // Otherwise fetch the parent
                        parent = getCategoryById(category.getParentId());
                        if (parent != null) {
                            category.setParent(parent);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Check if category name already exists (for validation)
     * @param name Category name to check
     * @param excludeId Optional ID to exclude from check (for updates)
     * @return true if name already exists, false otherwise
     */
    public boolean nameExists(String name, Long excludeId) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
            if (excludeId != null) {
                sql += " AND id != ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);

            if (excludeId != null) {
                stmt.setLong(2, excludeId);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Helper method to map ResultSet to Category object
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));

        // Handle NULL parent_id
        Long parentId = rs.getLong("parent_id");
        if (rs.wasNull()) {
            parentId = null;
        }
        category.setParentId(parentId);

        category.setDescription(rs.getString("description"));
        category.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        category.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return category;
    }
}
