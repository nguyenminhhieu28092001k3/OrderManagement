package app.swing.contants;

import app.swing.util.EnvLoader;

/**
 * Lớp chứa các hằng số cấu hình cơ sở dữ liệu
 * @author khaim
 */
public class DbConst {
    // Lấy URL cơ sở dữ liệu từ biến môi trường hoặc dùng mặc định
    private static final String BASE_URL = EnvLoader.getEnv("DB_URL", "jdbc:postgresql://localhost:5432/app_swing_db");

    // Thêm tham số ApplicationName nếu chưa có trong URL
    public static final String DB_URL = BASE_URL.contains("?") ?
        BASE_URL : BASE_URL + "?ApplicationName=OrderManagementApp";

    // Lấy tên người dùng từ biến môi trường hoặc dùng mặc định
    public static final String USER = EnvLoader.getEnv("DB_USER", "postgres");

    // Lấy mật khẩu từ biến môi trường hoặc dùng mặc định
    public static final String PASS = EnvLoader.getEnv("DB_PASS", "123456");
}
