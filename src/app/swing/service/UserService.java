package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp service cho thực thể Người dùng
 * @author HieuNM
 */
public class UserService {

    public enum LoginResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        ACCOUNT_DISABLED,
        DATABASE_ERROR
    }

    public static class AuthResult {
        private LoginResult result;
        private User user;

        public AuthResult(LoginResult result, User user) {
            this.result = result;
            this.user = user;
        }

        public LoginResult getResult() {
            return result;
        }

        public User getUser() {
            return user;
        }
    }

    public AuthResult authenticate(String username, String password) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, username, password, role, status, full_name, email FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean status = rs.getBoolean("status");

                // Kiểm tra mật khẩu (có thể mã hóa MD5 hoặc plain text)
                if (!verifyPassword(password, storedPassword)) {
                    return new AuthResult(LoginResult.INVALID_CREDENTIALS, null);
                }

                // Kiểm tra trạng thái tài khoản
                if (!status) {
                    return new AuthResult(LoginResult.ACCOUNT_DISABLED, null);
                }

                // Tạo đối tượng User
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getBoolean("status"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));

                return new AuthResult(LoginResult.SUCCESS, user);
            } else {
                return new AuthResult(LoginResult.INVALID_CREDENTIALS, null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthResult(LoginResult.DATABASE_ERROR, null);
        }
    }

    private boolean verifyPassword(String inputPassword, String storedPassword) {
        // Nếu mật khẩu được mã hóa MD5
        if (storedPassword.length() == 32) {
            return storedPassword.equals(hashMD5(inputPassword));
        }
        // Nếu mật khẩu plain text
        return storedPassword.equals(inputPassword);
    }

    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLoginResultMessage(LoginResult result) {
        switch (result) {
            case SUCCESS:
                return "Đăng nhập thành công!";
            case INVALID_CREDENTIALS:
                return "Tên đăng nhập hoặc mật khẩu không đúng.";
            case ACCOUNT_DISABLED:
                return "Tài khoản của bạn đã bị khóa.";
            case DATABASE_ERROR:
                return "Lỗi kết nối cơ sở dữ liệu.";
            default:
                return "Lỗi không xác định.";
        }
    }

    // Các thao tác CRUD cho quản lý người dùng

    /**
     * Lấy tất cả người dùng từ cơ sở dữ liệu
     * @return Danh sách tất cả người dùng
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, username, password, role, status, full_name, email FROM users ORDER BY id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getBoolean("status"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Get user by ID
     * @param id User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT id, username, password, role, status, full_name, email FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getBoolean("status"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new user
     * @param user User object with data
     * @return true if successful, false otherwise
     */
    public boolean createUser(User user) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, role, status, full_name, email) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getUsername());

            // Hash the password if it's not already hashed
            String password = user.getPassword();
            if (password.length() != 32) {
                password = hashMD5(password);
            }
            stmt.setString(2, password);

            stmt.setString(3, user.getRole());
            stmt.setBoolean(4, user.isStatus());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getEmail());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing user
     * @param user User object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        try (Connection conn = DbConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE users SET username = ?, role = ?, status = ?, full_name = ?, email = ?");

            // If password is provided, update it too
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                sql.append(", password = ?");
            }

            sql.append(" WHERE id = ?");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getRole());
            stmt.setBoolean(3, user.isStatus());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());

            int paramIndex = 6;

            // If password is provided, set it
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String password = user.getPassword();
                if (password.length() != 32) {
                    password = hashMD5(password);
                }
                stmt.setString(paramIndex++, password);
            }

            stmt.setInt(paramIndex, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete user by ID
     * @param id User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int id) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if username already exists (for creating new users)
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

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
     * Check if username already exists for another user (for updating users)
     * @param username Username to check
     * @param excludeUserId User ID to exclude from check
     * @return true if username exists for another user, false otherwise
     */
    public boolean usernameExistsForOtherUser(String username, int excludeUserId) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setInt(2, excludeUserId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
