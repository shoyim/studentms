package com.studentms.ui;

import com.studentms.dao.StudentSubjectDAO;
import com.studentms.dao.SubjectDAO;
import com.studentms.model.StudentSubject;
import com.studentms.model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GradeDialog extends JDialog {

    private int studentId;
    private String studentName;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Subject> cmbSubjects;
    private JSpinner spnGrade;
    private StudentSubjectDAO ssDAO = new StudentSubjectDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();

    public GradeDialog(JFrame parent, int studentId, String studentName) {
        super(parent, "Fanlar va Baholar: " + studentName, true);
        this.studentId = studentId;
        this.studentName = studentName;
        initUI();
        loadData();
        setSize(560, 520);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(41, 128, 185));
        top.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel lbl = new JLabel("Talaba: " + studentName);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        top.add(lbl);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"#", "Fan", "Baho"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(189, 195, 199));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(80);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 242, 250));
                    if (col == 2 && v != null) {
                        try {
                            double g = Double.parseDouble(v.toString());
                            setForeground(g >= 85 ? new Color(39, 130, 67) : g >= 70 ? new Color(180, 100, 0) : new Color(192, 57, 43));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } catch (NumberFormatException ignored) {}
                    } else {
                        setForeground(new Color(44, 62, 80));
                    }
                }
                return this;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setBackground(new Color(236, 240, 241));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbSubjects = new JComboBox<>();
        cmbSubjects.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbSubjects.setPreferredSize(new Dimension(200, 34));

        spnGrade = new JSpinner(new SpinnerNumberModel(85.0, 0.0, 100.0, 0.5));
        spnGrade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spnGrade.setPreferredSize(new Dimension(80, 34));

        JButton btnAssign = btn("Biriktir / Yangilanish", new Color(41, 128, 185));
        JButton btnRemove = btn("O'chirish", new Color(192, 57, 43));
        JButton btnClose  = btn("Yopish", new Color(127, 140, 141));

        btnAssign.addActionListener(e -> assignGrade());
        btnRemove.addActionListener(e -> removeGrade());
        btnClose.addActionListener(e -> dispose());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        bottom.add(new JLabel("Fan:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        bottom.add(cmbSubjects, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        bottom.add(new JLabel("Baho:"), gbc);
        gbc.gridx = 3;
        bottom.add(spnGrade, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnAssign);
        btnRow.add(btnRemove);
        btnRow.add(btnClose);
        bottom.add(btnRow, gbc);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<StudentSubject> list = ssDAO.getByStudentId(studentId);
            for (int i = 0; i < list.size(); i++) {
                StudentSubject ss = list.get(i);
                tableModel.addRow(new Object[]{i + 1, ss.getSubjectName(), ss.getGrade()});
            }

            cmbSubjects.removeAllItems();
            for (Subject s : subjectDAO.getAll())
                cmbSubjects.addItem(s);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignGrade() {
        Subject selected = (Subject) cmbSubjects.getSelectedItem();
        if (selected == null) return;
        double grade = (double) spnGrade.getValue();
        try {
            ssDAO.assign(studentId, selected.getId(), grade);
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeGrade() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Fan tanlang!"); return; }
        try {
            List<StudentSubject> list = ssDAO.getByStudentId(studentId);
            ssDAO.remove(list.get(row).getId());
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}