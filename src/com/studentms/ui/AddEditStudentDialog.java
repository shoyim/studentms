package com.studentms.ui;

import com.studentms.model.Student;

import javax.swing.*;
import java.awt.*;

public class AddEditStudentDialog extends JDialog {

    private JTextField txtStudentId;
    private JTextField txtFullName;
    private JSpinner spnCourse;
    private Student student;
    private boolean saved = false;

    public AddEditStudentDialog(JFrame parent, Student student) {
        super(parent, student == null ? "Yangi Talaba" : "Talabani Tahrirlash", true);
        this.student = student;
        initUI();
        if (student != null) fillData();
        setSize(420, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(41, 128, 185));
        top.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel lbl = new JLabel(student == null ? "Yangi Talaba Qo'shish" : "Talabani Tahrirlash");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        top.add(lbl);
        add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font lf = new Font("Segoe UI", Font.BOLD, 13);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel l1 = new JLabel("Talaba ID:"); l1.setFont(lf); form.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtStudentId = field(); form.add(txtStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel l2 = new JLabel("F.I.O:"); l2.setFont(lf); form.add(l2, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtFullName = field(); form.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel l3 = new JLabel("Kursi:"); l3.setFont(lf); form.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        spnCourse = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
        spnCourse.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spnCourse.setPreferredSize(new Dimension(0, 34));
        form.add(spnCourse, gbc);

        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btns.setBackground(new Color(236, 240, 241));
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        JButton btnCancel = btn("Bekor qilish", new Color(127, 140, 141));
        JButton btnSave   = btn("Saqlash", new Color(41, 128, 185));
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());
        btns.add(btnCancel);
        btns.add(btnSave);
        add(btns, BorderLayout.SOUTH);
    }

    private void fillData() {
        txtStudentId.setText(student.getStudentId());
        txtFullName.setText(student.getFullName());
        spnCourse.setValue(student.getCourse());
    }

    private void save() {
        String sid = txtStudentId.getText().trim();
        String name = txtFullName.getText().trim();
        if (sid.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcha maydonlarni to'ldiring!", "Ogohlantirish", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (student == null) student = new Student();
        student.setStudentId(sid);
        student.setFullName(name);
        student.setCourse((int) spnCourse.getValue());
        saved = true;
        dispose();
    }

    private JTextField field() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public Student getStudent() { return student; }
    public boolean isSaved() { return saved; }
}