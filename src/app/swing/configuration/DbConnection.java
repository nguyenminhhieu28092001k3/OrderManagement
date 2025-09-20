/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.configuration;
import app.swing.contants.DbConst;
import java.sql.*;
/**
 *
 * @author khaim
 */
public class DbConnection {
    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e){
            throw new SQLException("Không thể kết nối DB", e);
        }
        return DriverManager.getConnection(DbConst.DB_URL, DbConst.USER, DbConst.PASS);
    }
}
