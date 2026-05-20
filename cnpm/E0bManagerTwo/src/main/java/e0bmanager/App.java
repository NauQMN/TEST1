package e0bmanager;

import com.formdev.flatlaf.FlatLightLaf;
import e0bmanager.ui.LoginForm;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        // Cấu hình giao diện hệ thống
        UIManager.put("Table.alternateRowColor", new java.awt.Color(248, 250, 252));
        // Chạy ứng dụng
        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}
