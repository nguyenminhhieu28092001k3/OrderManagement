package app.swing.model;

/**
 * Lớp model cho thực thể Người dùng
 * @author
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private boolean status;
    private String fullName;
    private String email;

    // Các constructor
    public User() {}

    public User(int id, String username, String password, String role, boolean status, String fullName, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.fullName = fullName;
        this.email = email;
    }

    // Các phương thức Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
