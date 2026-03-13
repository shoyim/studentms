package com.studentms.ui;

import com.studentms.dao.SubjectDAO;
import com.studentms.model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SubjectManagerDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName;
    private SubjectDAO subjectDAO = new SubjectDAO();

    public SubjectManagerDialog(JFrame parent) {
        super(parent, "Fanlarni Boshqarish", true);
        initUI();
        loadSubjects();
        setSize(450, 500);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(41, 128, 185));
        top.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel lbl = new JLabel("Fanlarni Boshqarish");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(Color.BLACK);
        top.add(lbl);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Fan nomi"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(189, 195, 199));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(6, 6));
        bottom.setBackground(new Color(245, 247, 250));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(0, 36));
        txtName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setBackground(new Color(245, 247, 250));

        JButton btnAdd = btn("Qo'shish", new Color(39, 174, 96));
        JButton btnDel = btn("O'chirish", new Color(192, 57, 43));
        JButton btnClose = btn("Yopish", new Color(127, 140, 141));

        btnAdd.addActionListener(e -> addSubject());
        btnDel.addActionListener(e -> deleteSubject());
        btnClose.addActionListener(e -> dispose());

        btnRow.add(btnAdd);
        btnRow.add(btnDel);
        btnRow.add(btnClose);

        bottom.add(new JLabel("Yangi fan nomi:"), BorderLayout.NORTH);
        bottom.add(txtName, BorderLayout.CENTER);
        bottom.add(btnRow, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadSubjects() {
        try {
            tableModel.setRowCount(0);
            for (Subject s : subjectDAO.getAll())
                tableModel.addRow(new Object[]{s.getId(), s.getName()});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSubject() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Fan nomini kiriting!"); return; }
        try {
            subjectDAO.add(name);
            txtName.setText("");
            loadSubjects();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Xato: " + e.getMessage(), "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Fan tanlang!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int ok = JOptionPane.showConfirmDialog(this, "\"" + name + "\" fanini o'chirasizmi?", "Tasdiqlash", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                subjectDAO.delete(id);
                loadSubjects();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Xato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}