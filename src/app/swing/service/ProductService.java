package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.Category;
import app.swing.model.Product;
import app.swing.model.Supplier;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Product entity
 */
public class ProductService {

    private CategoryService categoryService;
    private SupplierService supplierService;

    public ProductService() {
        this.categoryService = new CategoryService();
        this.supplierService = new SupplierService();
    }

    /**
     * Get all products from database
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p ORDER BY p.name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Get only active products
     * @return List of active products
     */
    public List<Product> getActiveProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p WHERE p.is_active = true ORDER BY p.name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Get products by category
     * @param categoryId Category ID
     * @return List of products in the category
     */
    public List<Product> getProductsByCategory(long categoryId) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p WHERE p.category_id = ? ORDER BY p.name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, categoryId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Get products by supplier
     * @param supplierId Supplier ID
     * @return List of products from the supplier
     */
    public List<Product> getProductsBySupplier(long supplierId) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p WHERE p.supplier_id = ? ORDER BY p.name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, supplierId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Get product by ID
     * @param id Product ID
     * @return Product object if found, null otherwise
     */
    public Product getProductById(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p WHERE p.id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get product by SKU
     * @param sku Product SKU
     * @return Product object if found, null otherwise
     */
    public Product getProductBySku(String sku) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p WHERE p.sku = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sku);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new product
     * @param product Product object with data
     * @return true if successful, false otherwise
     */
    public boolean createProduct(Product product) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO products (sku, name, description, category_id, supplier_id, " +
                         "price, cost, stock_quantity, reorder_level, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());

            if (product.getCategoryId() != null) {
                stmt.setLong(4, product.getCategoryId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }

            if (product.getSupplierId() != null) {
                stmt.setLong(5, product.getSupplierId());
            } else {
                stmt.setNull(5, java.sql.Types.BIGINT);
            }

            stmt.setBigDecimal(6, product.getPrice());
            stmt.setBigDecimal(7, product.getCost());
            stmt.setInt(8, product.getStockQuantity());
            stmt.setInt(9, product.getReorderLevel());
            stmt.setBoolean(10, product.isActive());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    product.setId(rs.getLong(1));
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
     * Update existing product
     * @param product Product object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateProduct(Product product) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE products SET sku = ?, name = ?, description = ?, " +
                         "category_id = ?, supplier_id = ?, price = ?, cost = ?, " +
                         "stock_quantity = ?, reorder_level = ?, is_active = ?, " +
                         "updated_at = now() WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());

            if (product.getCategoryId() != null) {
                stmt.setLong(4, product.getCategoryId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }

            if (product.getSupplierId() != null) {
                stmt.setLong(5, product.getSupplierId());
            } else {
                stmt.setNull(5, java.sql.Types.BIGINT);
            }

            stmt.setBigDecimal(6, product.getPrice());
            stmt.setBigDecimal(7, product.getCost());
            stmt.setInt(8, product.getStockQuantity());
            stmt.setInt(9, product.getReorderLevel());
            stmt.setBoolean(10, product.isActive());
            stmt.setLong(11, product.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete product by ID
     * @param id Product ID
     * @return true if successful, false otherwise
     */
    public boolean deleteProduct(long id) {
        try (Connection conn = DbConnection.getConnection()) {
            // Check if product is referenced by order items
            String checkSql = "SELECT COUNT(*) FROM order_items WHERE product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setLong(1, id);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                // Product is used in orders, can't delete
                return false;
            }

            // Check if product has inventory movements
            String checkMovementsSql = "SELECT COUNT(*) FROM inventory_movements WHERE product_id = ?";
            PreparedStatement checkMovementsStmt = conn.prepareStatement(checkMovementsSql);
            checkMovementsStmt.setLong(1, id);
            ResultSet checkMovementsRs = checkMovementsStmt.executeQuery();

            if (checkMovementsRs.next() && checkMovementsRs.getInt(1) > 0) {
                // Product has inventory movements, can't delete
                return false;
            }

            // If not being used, proceed with deletion
            String sql = "DELETE FROM products WHERE id = ?";
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
     * Update product stock quantity
     * @param id Product ID
     * @param newQuantity New stock quantity
     * @return true if successful, false otherwise
     */
    public boolean updateStockQuantity(long id, int newQuantity) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "UPDATE products SET stock_quantity = ?, updated_at = now() WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newQuantity);
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Search products by name, SKU or description
     * @param searchTerm Search term to look for
     * @return List of matching products
     */
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p " +
                         "WHERE p.name ILIKE ? OR p.sku ILIKE ? OR p.description ILIKE ? " +
                         "ORDER BY p.name";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Check if product SKU already exists (for validation)
     * @param sku Product SKU to check
     * @param excludeId Optional ID to exclude from check (for updates)
     * @return true if SKU already exists, false otherwise
     */
    public boolean skuExists(String sku, Long excludeId) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM products WHERE sku = ?";
            if (excludeId != null) {
                sql += " AND id != ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sku);

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
     * Get low stock products
     * @return List of products with stock quantity <= reorder level
     */
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT p.id, p.sku, p.name, p.description, p.category_id, p.supplier_id, " +
                         "p.price, p.cost, p.stock_quantity, p.reorder_level, p.is_active, " +
                         "p.created_at, p.updated_at " +
                         "FROM products p " +
                         "WHERE p.is_active = true AND p.stock_quantity <= p.reorder_level " +
                         "ORDER BY (p.stock_quantity - p.reorder_level) ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductRelations(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Helper method to map ResultSet to Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setSku(rs.getString("sku"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));

        // Handle NULL category_id
        long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            product.setCategoryId(categoryId);
        }

        // Handle NULL supplier_id
        long supplierId = rs.getLong("supplier_id");
        if (!rs.wasNull()) {
            product.setSupplierId(supplierId);
        }

        product.setPrice(rs.getBigDecimal("price"));
        product.setCost(rs.getBigDecimal("cost"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setReorderLevel(rs.getInt("reorder_level"));
        product.setActive(rs.getBoolean("is_active"));
        product.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        product.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return product;
    }

    /**
     * Helper method to load category and supplier for a product
     */
    private void loadProductRelations(Product product) {
        // Load category if available
        if (product.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(product.getCategoryId());
            if (category != null) {
                product.setCategory(category);
            }
        }

        // Load supplier if available
        if (product.getSupplierId() != null) {
            Supplier supplier = supplierService.getSupplierById(product.getSupplierId());
            if (supplier != null) {
                product.setSupplier(supplier);
            }
        }
    }
}
