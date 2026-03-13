package com.studentms;

import com.studentms.ui.MainFrame;
import com.studentms.util.DatabaseConnection;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {}

    SwingUtilities.invokeLater(() -> {
      try {
        DatabaseConnection.getConnection();
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null,
                "Ma'lumotlar bazasiga ulanib bo'lmadi!\n\nXato: " + e.getMessage() +
                        "\n\nIltimos quyidagilarni tekshiring:\n" +
                        "• MySQL serveri ishga tushirilganmi?\n" +
                        "• DatabaseConnection.java da parol to'g'rimi?\n" +
                        "• 'student_management' bazasi mavjudmi?",
                "Ulanish xatosi", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    });
  }
}