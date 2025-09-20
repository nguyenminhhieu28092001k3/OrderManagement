import java.sql.*;

public class TestConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/app_swing_db";
    private static final String USER = "postgres";
    private static final String PASS = "123456";
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Testing PostgreSQL Connection");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL driver loaded successfully");
            
            // Test connection
            System.out.println("Connecting to database...");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            if (conn != null) {
                System.out.println("✓ Database connection established successfully!");
                
                // Test query
                String sql = "SELECT username, role, full_name, status FROM users ORDER BY id";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                System.out.println("\nUsers in database:");
                System.out.println("==================");
                System.out.printf("%-15s %-15s %-20s %-8s%n", "Username", "Role", "Full Name", "Status");
                System.out.println("-".repeat(60));
                
                while (rs.next()) {
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    String fullName = rs.getString("full_name");
                    boolean status = rs.getBoolean("status");
                    
                    System.out.printf("%-15s %-15s %-20s %-8s%n", 
                        username, role, fullName, status ? "Active" : "Disabled");
                }
                
                rs.close();
                stmt.close();
                conn.close();
                
                System.out.println("\n✓ Test completed successfully!");
                System.out.println("Your application is ready to use the database.");
            } else {
                System.out.println("✗ Failed to connect to database");
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("✗ PostgreSQL driver not found!");
            System.out.println("Make sure postgresql-42.7.6.jar is in your classpath.");
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("\nTroubleshooting:");
            System.out.println("1. Make sure PostgreSQL is running (docker-compose up -d postgres)");
            System.out.println("2. Check if port 5432 is available");
            System.out.println("3. Verify database credentials");
        }
    }
} 