# App Swing - Ứng dụng quản lý sản phẩm

Ứng dụng desktop được xây dựng bằng Java Swing với Hibernate ORM để quản lý sản phẩm.

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Tính năng](#tính-năng)
- [Cấu hình cơ sở dữ liệu](#cấu-hình-cơ-sở-dữ-liệu)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Kiến trúc ứng dụng](#kiến-trúc-ứng-dụng)

## 🔍 Tổng quan

Ứng dụng App Swing là một hệ thống quản lý sản phẩm được phát triển cho môn học IE303 - Java. Ứng dụng cung cấp giao diện người dùng hiện đại với FlatLaf look-and-feel và tích hợp cơ sở dữ liệu PostgreSQL thông qua Hibernate ORM.

## 🛠 Công nghệ sử dụng

- **Java SE**: Ngôn ngữ lập trình chính
- **Java Swing**: Framework GUI cho ứng dụng desktop
- **Hibernate 5.6.0**: ORM framework cho quản lý cơ sở dữ liệu
- **PostgreSQL**: Hệ quản trị cơ sở dữ liệu
- **FlatLaf 3.6**: Modern look and feel cho Swing
- **NetBeans**: IDE phát triển

### Dependencies chính:
- `hibernate-core-5.6.0.Final.jar`
- `postgresql-42.7.6.jar`
- `flatlaf-3.6.jar`
- `javax.persistence-api-2.2.jar`

## 📁 Cấu trúc dự án

```
app-swing/
├── src/
│   ├── app/swing/
│   │   ├── AppSwing.java                 # Main class
│   │   ├── configuration/
│   │   │   ├── DbConnection.java         # Database connection
│   │   │   └── HibernateUtil.java        # Hibernate configuration
│   │   ├── controller/
│   │   │   ├── BaseController.java       # Base CRUD controller
│   │   │   ├── BaseSearchController.java # Base search & pagination
│   │   │   └── SanPhamController.java    # Product controller
│   │   ├── model/
│   │   │   └── SanPham.java             # Product entity
│   │   └── view/
│   │       ├── MainFrame.java           # Main window
│   │       ├── HomeView.java            # Home tab
│   │       ├── LoadingView.java         # Loading screen
│   │       └── panel/
│   │           └── QuanLySanPhamView.java # Product management
│   └── hibernate.cfg.xml                # Hibernate configuration
├── build/                               # Compiled classes
├── nbproject/                           # NetBeans project files
└── *.jar                               # Required libraries
```

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống:
- Java JDK 8 trở lên
- PostgreSQL Database
- NetBeans IDE (khuyến nghị)

### Bước 1: Clone project
```bash
git clone <repository-url>
cd app-swing
```

### Bước 2: Cấu hình database
1. Tạo database PostgreSQL với tên `your_database_name`
2. Cập nhật thông tin kết nối trong `src/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/your_database_name</property>
<property name="hibernate.connection.username">your_username</property>
<property name="hibernate.connection.password">your_password</property>
```

### Bước 3: Chạy ứng dụng

**Sử dụng NetBeans:**
- Mở project trong NetBeans
- Build and Run (F6)

**Sử dụng command line:**
```bash
# Build project
javac -cp "hibernate-core-5.6.0.Final.jar;postgresql-42.7.6.jar;flatlaf-3.6.jar;javax.persistence-api-2.2.jar;..." -d build/classes src/app/swing/**/*.java

# Run application
java -cp "build/classes;hibernate-core-5.6.0.Final.jar;postgresql-42.7.6.jar;flatlaf-3.6.jar;..." app.swing.AppSwing
```

**Sử dụng VS Code Task:**
```bash
# Sử dụng task có sẵn trong workspace
# Chạy task "Build and Run Java App"
```

## ✨ Tính năng

### Quản lý sản phẩm:
- ✅ Thêm sản phẩm mới
- ✅ Xem danh sách sản phẩm
- ✅ Cập nhật thông tin sản phẩm
- ✅ Xóa sản phẩm
- ✅ Tìm kiếm sản phẩm theo tên
- ✅ Lọc sản phẩm theo khoảng giá
- ✅ Phân trang dữ liệu
- ✅ Sắp xếp theo giá

### Giao diện:
- ✅ Modern UI với FlatLaf
- ✅ Tabbed interface
- ✅ Table view với pagination
- ✅ Loading screen
- ✅ Responsive design

## 🗄 Cấu hình cơ sở dữ liệu

### Bảng `san_pham`:
```sql
CREATE TABLE san_pham (
    id SERIAL PRIMARY KEY,
    id_doanh_nghiep INTEGER NOT NULL,
    ten VARCHAR(255) NOT NULL,
    mota TEXT,
    gia DOUBLE PRECISION NOT NULL,
    ngay_tao TIMESTAMP,
    ngay_cap_nhat TIMESTAMP
);
```

### Entity Mapping:
- `@Entity` với `@Table(name = "san_pham")`
- Auto-generated ID với `@GeneratedValue`
- Timestamp fields cho audit trail
- Validation constraints

## 📖 Hướng dẫn sử dụng

### 1. Khởi động ứng dụng
- Ứng dụng sẽ hiển thị MainFrame với các tab
- Tab "Quản lý sản phẩm" cho phép thao tác với dữ liệu

### 2. Thêm sản phẩm mới
- Nhấn nút "Thêm mẫu" để thêm dữ liệu test
- Hoặc sử dụng form nhập liệu (tùy chỉnh thêm)

### 3. Xem và tìm kiếm
- Dữ liệu hiển thị trong bảng với pagination
- Sử dụng các nút Previous/Next để điều hướng
- Tìm kiếm theo tên hoặc khoảng giá

### 4. Cập nhật/Xóa
- Double-click vào row để chỉnh sửa
- Right-click để có menu context (tùy chỉnh thêm)

## 🏗 Kiến trúc ứng dụng

### Model-View-Controller (MVC):
- **Model**: `SanPham.java` - JPA Entity
- **View**: Swing components (`MainFrame`, `QuanLySanPhamView`)
- **Controller**: Separated logic
  - `BaseController` - CRUD operations
  - `BaseSearchController` - Search & pagination
  - `SanPhamController` - Business logic

### Design Patterns:
- **Singleton**: `HibernateUtil` cho SessionFactory
- **Data Access Object**: Controller classes
- **Observer**: Event handling trong Swing
- **Template Method**: Base controllers

### Layered Architecture:
```
┌─────────────────┐
│   Presentation  │ ← Swing Views
├─────────────────┤
│    Controller   │ ← Business Logic
├─────────────────┤
│      Model      │ ← JPA Entities
├─────────────────┤
│   Data Access   │ ← Hibernate ORM
├─────────────────┤
│    Database     │ ← PostgreSQL
└─────────────────┘
```

## 🔧 Phát triển và mở rộng

### Thêm entity mới:
1. Tạo class entity với JPA annotations
2. Tạo controller kế thừa từ `BaseController`
3. Tạo view tương ứng
4. Cập nhật `hibernate.cfg.xml`

### Tùy chỉnh UI:
- Sử dụng FlatLaf themes
- Tùy chỉnh trong `MainFrame.initFlatLaf()`
- Thêm custom components

### Database Migration:
- Sử dụng `hibernate.hbm2ddl.auto=update`
- Hoặc manual SQL scripts

## 📝 Ghi chú

- Project được phát triển cho môn IE303 - Java tại UIT
- Sử dụng Hibernate 5.6.0 với PostgreSQL
- Modern UI với FlatLaf look-and-feel
- Separated controller logic cho maintainability

## 👨‍💻 Tác giả

- **Tên**: khaim
- **Môn học**: IE303 - Java
- **Trường**: UIT (University of Information Technology)

---

*Cập nhật lần cuối: July 30, 2025*
