/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.contants;

import app.swing.util.EnvLoader;

/**
 *
 * @author khaim
 */
public class DbConst {
    // Get the base URL from environment variable or use default
    private static final String BASE_URL = EnvLoader.getEnv("DB_URL", "jdbc:postgresql://localhost:5432/app_swing_db");

    // Append timezone parameters if not already present
    public static final String DB_URL = BASE_URL.contains("?") ?
        BASE_URL : BASE_URL + "?ApplicationName=OrderManagementApp";

    public static final String USER = EnvLoader.getEnv("DB_USER", "postgres");

    public static final String PASS = EnvLoader.getEnv("DB_PASS", "123456");
}
