/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.swing.view;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import app.swing.view.panel.QuanLySanPhamView;

/**
 *
 * @author khaim
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
//         public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             // Tạo loading view
//             LoadingView loadingView = new LoadingView(0); // Không auto-close
    
//             // Tạo background thread để xử lý loading
//             Thread loadingThread = new Thread(() -> {
//                 try {
//                     // Simulate loading time
//                     Thread.sleep(2000); // 2 giây
    
//                     // Sau khi loading xong, ẩn loading và hiển thị main window
//                     SwingUtilities.invokeLater(() -> {
//                         loadingView.setVisible(false);
//                         loadingView.dispose();
    
//                         // Tạo main window với QuanLySanPhamView
//                         JFrame frame = new JFrame("Quản lý sản phẩm");
//                         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                         frame.setSize(900, 700);
//                         frame.setLayout(new BorderLayout());
    
//                         // Thêm QuanLySanPhamView trực tiếp vào frame
//                         QuanLySanPhamView quanLyPanel = new QuanLySanPhamView();
//                         frame.add(quanLyPanel, BorderLayout.CENTER);
    
//                         frame.setLocationRelativeTo(null);
//                         frame.setVisible(true);
//                     });
    
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             });
    
//             loadingThread.start();
//         });
//     }
}
