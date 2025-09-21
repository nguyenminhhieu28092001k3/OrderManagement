package app.swing.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp tiện ích để tải các biến môi trường từ file .env
 *
 * @author
 */
public class EnvLoader {
    private static final Map<String, String> envVars = new HashMap<>();
    private static boolean loaded = false;

    static {
        loadEnvFile();
    }

    /**
     * Tải các biến môi trường từ file .env
     */
    private static void loadEnvFile() {
        if (loaded) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Bỏ qua các dòng trống và comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Phân tích các cặp key=value
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();

                    // Loại bỏ dấu ngoặc kép nếu có
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    envVars.put(key, value);
                }
            }
            loaded = true;
            System.out.println("✅ Loaded .env file successfully");
        } catch (IOException e) {
            System.out.println("⚠️ .env file not found, using system environment variables");
        }
    }

    /**
     * Lấy giá trị biến môi trường
     * Ưu tiên: file .env -> biến môi trường hệ thống -> giá trị mặc định
     *
     * @param key Khóa biến môi trường
     * @param defaultValue Giá trị mặc định nếu không tìm thấy
     * @return Giá trị biến môi trường hoặc giá trị mặc định
     */
    public static String getEnv(String key, String defaultValue) {
        // Kiểm tra file .env trước
        String value = envVars.get(key);
        if (value != null) {
            return value;
        }

        // Sau đó kiểm tra biến môi trường hệ thống
        value = System.getenv(key);
        if (value != null) {
            return value;
        }

        // Trả về giá trị mặc định
        return defaultValue;
    }

    /**
     * Lấy giá trị biến môi trường không có giá trị mặc định
     *
     * @param key Khóa biến môi trường
     * @return Giá trị biến môi trường hoặc null
     */
    public static String getEnv(String key) {
        return getEnv(key, null);
    }
}
