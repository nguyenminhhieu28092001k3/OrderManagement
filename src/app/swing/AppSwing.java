/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app.swing;

import app.swing.view.LoadingView;
import app.swing.view.LoginView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author HieuNM
 */
public class AppSwing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Set system look and feel
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Show loading screen first
                new LoadingView(6000); // 3 seconds loading

                // Then show login screen
                new LoginView().setVisible(true);
            }
        });
    }
}
