package app.swing.util;

import app.swing.model.User;

/**
 *
 * @author hieu
 */
public class SessionManager {
    private static User currentUser;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase(role);
    }
    
    public static boolean isAdmin() {
        return hasRole("admin");
    }
    
    public static boolean isCustomerAdmin() {
        return hasRole("customer_admin");
    }
    
    public static boolean isStaff() {
        return hasRole("staff");
    }
} 