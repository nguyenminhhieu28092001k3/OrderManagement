package app.swing.service;

import app.swing.configuration.DbConnection;
import app.swing.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author hieu
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
} 