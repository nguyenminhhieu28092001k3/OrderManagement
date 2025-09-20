/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.configuration;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        SessionFactory tempFactory = null;
        try {
            tempFactory = new Configuration().configure().buildSessionFactory();
        } catch (HibernateException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, "Lỗi khởi tạo Hibernate: ", ex);
            throw new ExceptionInInitializerError(ex);
        }
        sessionFactory = tempFactory;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


