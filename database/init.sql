-- ================================
-- Full schema: create từ đầu
-- ================================

-- Khách hàng
CREATE TABLE IF NOT EXISTS customers (
  id BIGSERIAL PRIMARY KEY,
  code TEXT UNIQUE,
  name TEXT NOT NULL,
  email TEXT UNIQUE,
  phone TEXT,
  address TEXT,
  note TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers (name);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers (email);

-- Nhà cung cấp
CREATE TABLE IF NOT EXISTS suppliers (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  contact_name TEXT,
  email TEXT,
  phone TEXT,
  address TEXT,
  note TEXT,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

-- Danh mục sản phẩm (tree)
CREATE TABLE IF NOT EXISTS categories (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  parent_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
  description TEXT,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_categories_name ON categories (name);

-- Sản phẩm
CREATE TABLE IF NOT EXISTS products (
  id BIGSERIAL PRIMARY KEY,
  sku TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  description TEXT,
  category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
  supplier_id BIGINT REFERENCES suppliers(id) ON DELETE SET NULL,
  price NUMERIC(12,2) NOT NULL DEFAULT 0.00,
  cost NUMERIC(12,2) DEFAULT 0.00,
  stock_quantity INTEGER NOT NULL DEFAULT 0,
  reorder_level INTEGER DEFAULT 0,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_products_sku ON products (sku);
CREATE INDEX IF NOT EXISTS idx_products_name ON products (name);

-- Users (note: BIGSERIAL to align với BIGINT refs)
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL, -- <-- lưu dưới dạng hash trong production
  role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'customer_admin', 'staff')),
  status BOOLEAN DEFAULT true,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);






-- Orders (thêm user_id tham chiếu users.id)
CREATE TABLE IF NOT EXISTS orders (
  id BIGSERIAL PRIMARY KEY,
  order_number TEXT UNIQUE NOT NULL,
  customer_id BIGINT REFERENCES customers(id) ON DELETE SET NULL,
  user_id BIGINT REFERENCES users(id) ON DELETE SET NULL, -- người tạo/điều hành đơn (mới)
  status VARCHAR(20) NOT NULL DEFAULT 'pending'
    CHECK (status IN ('draft','pending','paid','shipped','completed','cancelled','refunded')),
  placed_at TIMESTAMPTZ DEFAULT now(),
  delivery_date TIMESTAMPTZ,
  subtotal NUMERIC(12,2) DEFAULT 0.00,
  tax NUMERIC(12,2) DEFAULT 0.00,
  discount NUMERIC(12,2) DEFAULT 0.00,
  shipping_fee NUMERIC(12,2) DEFAULT 0.00,
  total NUMERIC(12,2) DEFAULT 0.00,
  notes TEXT,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders (order_number);
CREATE INDEX IF NOT EXISTS idx_orders_placed_at ON orders (placed_at);
CREATE INDEX IF NOT EXISTS idx_orders_customer ON orders (customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders (user_id);

-- Order items
CREATE TABLE IF NOT EXISTS order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
  product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
  product_name TEXT,
  sku TEXT,
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  unit_price NUMERIC(12,2) NOT NULL DEFAULT 0.00,
  discount NUMERIC(12,2) DEFAULT 0.00,
  line_total NUMERIC(12,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items (order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items (product_id);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
  amount NUMERIC(12,2) NOT NULL,
  method VARCHAR(20) NOT NULL DEFAULT 'card'
    CHECK (method IN ('cash','card','bank_transfer','wallet','other')),
  reference TEXT,
  paid_at TIMESTAMPTZ DEFAULT now(),
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments (order_id);

-- Shipments
CREATE TABLE IF NOT EXISTS shipments (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
  provider TEXT,
  tracking_number TEXT,
  shipped_at TIMESTAMPTZ,
  delivered_at TIMESTAMPTZ,
  status VARCHAR(20) NOT NULL DEFAULT 'pending'
    CHECK (status IN ('pending','shipped','in_transit','delivered','returned','cancelled')),
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_shipments_order_id ON shipments (order_id);
CREATE INDEX IF NOT EXISTS idx_shipments_tracking ON shipments (tracking_number);

-- Inventory movements (thêm user_id, và created_by chuyển sang BIGINT cho consistency)
CREATE TABLE IF NOT EXISTS inventory_movements (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
  change_qty INTEGER NOT NULL,
  kind VARCHAR(20) NOT NULL DEFAULT 'adjustment'
    CHECK (kind IN ('purchase','sale','adjustment','return')),
  reference_type TEXT,
  reference_id BIGINT,
  note TEXT,
  created_by BIGINT,                 -- có thể FK tới users.id nếu muốn
  user_id BIGINT REFERENCES users(id) ON DELETE SET NULL, -- <-- thêm
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_inventory_product_id ON inventory_movements (product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_created_at ON inventory_movements (created_at);
CREATE INDEX IF NOT EXISTS idx_inventory_user_id ON inventory_movements (user_id);

-- ================================
-- Sample seed của users (demo)
-- ================================
INSERT INTO users (username, password, role, status, full_name, email)
-- LƯU Ý: password dưới đây chỉ ví dụ; hãy thay bằng hash thực tế
VALUES
  ('admin', 'admin123', 'admin', true, 'Administrator', 'admin@example.com'),
  ('customer_admin1', 'customer123', 'customer_admin', true, 'Nguyễn Văn A', 'customer1@example.com'),
  ('customer_admin2', 'customer123', 'customer_admin', true, 'Trần Thị B', 'customer2@example.com'),
  ('staff1', 'staff123', 'staff', true, 'Lê Văn C', 'staff1@example.com'),
  ('staff2', 'staff123', 'staff', true, 'Phạm Thị D', 'staff2@example.com'),
  ('staff3', 'staff123', 'staff', false, 'Hoàng Văn E', 'staff3@example.com')
ON CONFLICT DO NOTHING;
