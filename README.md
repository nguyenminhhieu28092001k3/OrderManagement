## 🛠️ Cài đặt

### 1. Yêu cầu hệ thống
- **Java**: Java 21 hoặc cao hơn
- **Docker**: Docker & Docker Compose (khuyến nghị)
- **Database**: PostgreSQL 15+ (được cài tự động bằng Docker)
- **IDE**: NetBeans (khuyến nghị) hoặc IntelliJ IDEA

### 2. Cài đặt Database

####  Sử dụng Docker🐳
```bash
# Khởi động PostgreSQL với Docker
# Windows
database/start-db.bat

# Linux/Mac
chmod +x database/start-db.sh
./database/start-db.sh

# Hoặc manual
docker-compose up -d postgres
```

### 3. Kiểm tra kết nối Database
```bash
# Test database connection
# Windows
test-db.bat

# Linux/Mac
chmod +x test-db.sh
./test-db.sh
```

### 4. Biên dịch và chạy ứng dụng
```bash
# Windows
run.bat

# Linux/Mac
chmod +x run.sh
./run.sh
```

## 🚀 Sử dụng

### Tài khoản mẫu để test:

| Username | Password | Role | Status | Tên hiển thị |
|----------|----------|------|---------|-------------|
| admin | admin123 | admin | ✅ | Administrator |
| customer_admin1 | customer123 | customer_admin | ✅ | Nguyễn Văn A |
| customer_admin2 | customer123 | customer_admin | ✅ | Trần Thị B |
| staff1 | staff123 | staff | ✅ | Lê Văn C |
| staff2 | staff123 | staff | ✅ | Phạm Thị D |
| staff3 | staff123 | staff | ❌ | Hoàng Văn E |
