package app.swing.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to load environment variables from .env file
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
     * Load environment variables from .env file
     */
    private static void loadEnvFile() {
        if (loaded) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse key=value pairs
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    // Remove quotes if present
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
     * Get environment variable value
     * Priority: .env file -> system environment -> default value
     * 
     * @param key Environment variable key
     * @param defaultValue Default value if not found
     * @return Environment variable value or default value
     */
    public static String getEnv(String key, String defaultValue) {
        // First check .env file
        String value = envVars.get(key);
        if (value != null) {
            return value;
        }
        
        // Then check system environment
        value = System.getenv(key);
        if (value != null) {
            return value;
        }
        
        // Return default value
        return defaultValue;
    }
    
    /**
     * Get environment variable value without default
     * 
     * @param key Environment variable key
     * @return Environment variable value or null
     */
    public static String getEnv(String key) {
        return getEnv(key, null);
    }
} 