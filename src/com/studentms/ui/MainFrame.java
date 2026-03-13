package com.studentms.ui;

import com.studentms.dao.StudentDAO;
import com.studentms.dao.StudentSubjectDAO;
import com.studentms.model.Student;
import com.studentms.model.StudentSubject;
import com.studentms.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {

    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JTable gradeTable;
    private DefaultTableModel gradeModel;
    private JTextField txtSearch;
    private JLabel lblStatus, lblCount;
    private StudentDAO studentDAO = new StudentDAO();
    private StudentSubjectDAO ssDAO = new StudentSubjectDAO();
    private int selectedStudentDbId = -1;

    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER  = new Color(192, 57, 43);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color BG      = new Color(245, 247, 250);

    public MainFrame() {
        initUI();
        loadStudents();
    }

    private void initUI() {
        setTitle("Talabalar Boshqaruvi Tizimi");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1150, 680);
        setMinimumSize(new Dimension(950, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(MainFrame.this, "Dasturdan chiqmoqchimisiz?",
                        "Chiqish", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DatabaseConnection.closeConnection();
                    System.exit(0);
                }
            }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatus(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PRIMARY);
        p.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("Talabalar Boshqaruvi Tizimi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.WEST);

        lblCount = new JLabel("Talabalar: 0");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCount.setForeground(new Color(200, 230, 255));
        p.add(lblCount, BorderLayout.EAST);
        return p;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeft(), buildRight());
        split.setDividerLocation(620);
        split.setDividerSize(5);
        split.setBorder(null);
        return split;
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(10, 12, 10, 6));

        p.add(buildToolbar(), BorderLayout.NORTH);

        studentModel = new DefaultTableModel(new String[]{"ID", "Talaba ID", "F.I.O", "Kurs", "O'rtacha"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentModel);
        studentTable.setRowHeight(36);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setGridColor(new Color(220, 225, 230));

        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (sel) { setBackground(new Color(174, 214, 241)); }
                else { setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 242, 250)); }
                if (col == 4 && v != null && !v.toString().equals("-")) {
                    try {
                        double g = Double.parseDouble(v.toString());
                        if (!sel) setForeground(g >= 85 ? new Color(39, 130, 67) : g >= 70 ? new Color(180, 100, 0) : new Color(192, 57, 43));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } catch (NumberFormatException ignored) {}
                } else if (!sel) { setForeground(new Color(44, 62, 80)); }
                return this;
            }
        });

        JTableHeader h = studentTable.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(new Color(189, 195, 199));
        h.setForeground(Color.BLACK);
        h.setPreferredSize(new Dimension(0, 40));

        int[] w = {40, 110, 200, 60, 80};
        for (int i = 0; i < w.length; i++) studentTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onStudentSelected();
        });
        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editStudent();
            }
        });

        JScrollPane sp = new JScrollPane(studentTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRight() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(10, 6, 10, 12));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(BG);
        JLabel lbl = new JLabel("Fanlar va Baholar");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(52, 73, 94));
        topRow.add(lbl, BorderLayout.WEST);

        JButton btnGrade = btn("+ Fan / Baho", PRIMARY);
        btnGrade.addActionListener(e -> openGradeDialog());
        topRow.add(btnGrade, BorderLayout.EAST);
        p.add(topRow, BorderLayout.NORTH);

        gradeModel = new DefaultTableModel(new String[]{"Fan", "Baho", "Holat"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        gradeTable = new JTable(gradeModel);
        gradeTable.setRowHeight(34);
        gradeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gradeTable.setGridColor(new Color(220, 225, 230));

        gradeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 242, 250));
                    if (col == 1 && v != null) {
                        try {
                            double g = Double.parseDouble(v.toString());
                            setForeground(g >= 85 ? new Color(39, 130, 67) : g >= 70 ? new Color(180, 100, 0) : new Color(192, 57, 43));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } catch (NumberFormatException ignored) {}
                    } else if (col == 2 && v != null) {
                        String s = v.toString();
                        setForeground("A'lo".equals(s) ? new Color(39, 130, 67) : "Yaxshi".equals(s) ? new Color(180, 100, 0) : "Qoniqarli".equals(s) ? new Color(200, 130, 0) : new Color(192, 57, 43));
                    } else {
                        setForeground(new Color(44, 62, 80));
                    }
                }
                return this;
            }
        });

        JTableHeader gh = gradeTable.getTableHeader();
        gh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gh.setBackground(new Color(189, 195, 199));
        gh.setForeground(Color.BLACK);
        gh.setPreferredSize(new Dimension(0, 40));

        JScrollPane sp = new JScrollPane(gradeTable);
        sp.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(BG);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btns.setBackground(BG);

        JButton bAdd  = btn("+ Qo'shish", SUCCESS);
        JButton bEdit = btn("Tahrirlash", WARNING);
        JButton bDel  = btn("O'chirish", DANGER);
        JButton bSub  = btn("Fanlar", new Color(142, 68, 173));
        JButton bRef  = btn("Yangilash", new Color(127, 140, 141));

        bAdd.addActionListener(e -> addStudent());
        bEdit.addActionListener(e -> editStudent());
        bDel.addActionListener(e -> deleteStudent());
        bSub.addActionListener(e -> new SubjectManagerDialog(this).setVisible(true));
        bRef.addActionListener(e -> loadStudents());

        btns.add(bAdd); btns.add(bEdit); btns.add(bDel); btns.add(bSub); btns.add(bRef);
        p.add(btns, BorderLayout.WEST);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        search.setBackground(BG);
        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(200, 34));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) searchStudents(); }
        });
        JButton bSearch = btn("Qidirish", PRIMARY);
        bSearch.addActionListener(e -> searchStudents());
        search.add(txtSearch); search.add(bSearch);
        p.add(search, BorderLayout.EAST);
        return p;
    }

    private JPanel buildStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(52, 73, 94));
        p.setBorder(new EmptyBorder(5, 14, 5, 14));
        lblStatus = new JLabel("Tayyor");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(200, 220, 240));
        p.add(lblStatus, BorderLayout.WEST);
        return p;
    }

    private void loadStudents() {
        try {
            List<Student> list = studentDAO.getAll();
            fillStudentTable(list);
            lblCount.setText("Talabalar: " + list.size());
            gradeModel.setRowCount(0);
            selectedStudentDbId = -1;
            status("Tayyor. Jami: " + list.size() + " talaba");
        } catch (SQLException e) { error(e.getMessage()); }
    }

    private void searchStudents() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadStudents(); return; }
        try {
            List<Student> list = studentDAO.search(kw);
            fillStudentTable(list);
            status("Qidiruv: " + list.size() + " ta natija");
        } catch (SQLException e) { error(e.getMessage()); }
    }

    private void fillStudentTable(List<Student> list) {
        studentModel.setRowCount(0);
        for (Student s : list) {
            double avg = 0;
            try { avg = ssDAO.getAverage(s.getId()); } catch (SQLException ignored) {}
            studentModel.addRow(new Object[]{
                    s.getId(), s.getStudentId(), s.getFullName(),
                    s.getCourse() + "-kurs",
                    avg > 0 ? String.format("%.1f", avg) : "-"
            });
        }
    }

    private void onStudentSelected() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return;
        selectedStudentDbId = (int) studentModel.getValueAt(row, 0);
        try {
            List<StudentSubject> grades = ssDAO.getByStudentId(selectedStudentDbId);
            gradeModel.setRowCount(0);
            for (StudentSubject ss : grades) {
                String holat = ss.getGrade() >= 5 ? "A'lo" : ss.getGrade() >= 3 ? "Yaxshi" : ss.getGrade() >= 2 ? "Qoniqarli" : "Qoniqarsiz";
                gradeModel.addRow(new Object[]{ss.getSubjectName(), ss.getGrade(), holat});
            }
            status("Talaba: " + studentModel.getValueAt(row, 2) + " — " + grades.size() + " ta fan");
        } catch (SQLException e) { error(e.getMessage()); }
    }

    private void openGradeDialog() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Talabani tanlang!"); return; }
        int dbId = (int) studentModel.getValueAt(row, 0);
        String name = (String) studentModel.getValueAt(row, 2);
        GradeDialog dlg = new GradeDialog(this, dbId, name);
        dlg.setVisible(true);
        onStudentSelected();
        loadStudents();
    }

    private void addStudent() {
        AddEditStudentDialog dlg = new AddEditStudentDialog(this, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                studentDAO.add(dlg.getStudent());
                loadStudents();
                status("Yangi talaba qo'shildi.");
            } catch (SQLException e) { error(e.getMessage()); }
        }
    }

    private void editStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Talabani tanlang!"); return; }
        int id = (int) studentModel.getValueAt(row, 0);
        try {
            Student s = studentDAO.getById(id);
            AddEditStudentDialog dlg = new AddEditStudentDialog(this, s);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                studentDAO.update(dlg.getStudent());
                loadStudents();
                status("Talaba yangilandi.");
            }
        } catch (SQLException e) { error(e.getMessage()); }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Talabani tanlang!"); return; }
        String name = (String) studentModel.getValueAt(row, 2);
        if (JOptionPane.showConfirmDialog(this, "\"" + name + "\" o'chirilsinmi?", "Tasdiqlash", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                studentDAO.delete((int) studentModel.getValueAt(row, 0));
                loadStudents();
                status("Talaba o'chirildi.");
            } catch (SQLException e) { error(e.getMessage()); }
        }
    }

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void status(String msg) { lblStatus.setText(msg); }
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Xato", JOptionPane.ERROR_MESSAGE);
        status("Xato yuz berdi.");
    }
}