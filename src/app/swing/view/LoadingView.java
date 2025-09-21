/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.view;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Giao diện loading hiển thị khi khởi động ứng dụng
 * @author HieuNM
 */
public class LoadingView extends JWindow {
    public LoadingView(int duration) {
        JPanel content = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Đang tải ứng dụng...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        content.add(label, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        content.add(progressBar, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(300, 120);
        setLocationRelativeTo(null);
        setVisible(true);

        // Đợi trong thời gian duration (ms)
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setVisible(false);
        dispose();
    }

    // Main method removed - application now starts from AppSwing.java
}
