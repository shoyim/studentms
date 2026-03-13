package com.studentms.dao;

import com.studentms.model.StudentSubject;
import com.studentms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentSubjectDAO {

    public List<StudentSubject> getByStudentId(int studentId) throws SQLException {
        List<StudentSubject> list = new ArrayList<>();
        String sql = "SELECT ss.id, ss.student_id, ss.subject_id, s.name, ss.grade " +
                "FROM student_subjects ss JOIN subjects s ON ss.subject_id = s.id " +
                "WHERE ss.student_id = ? ORDER BY s.name";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new StudentSubject(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("subject_id"),
                    rs.getString("name"),
                    rs.getDouble("grade")
            ));
        }
        rs.close(); ps.close();
        return list;
    }

    public boolean assign(int studentId, int subjectId, double grade) throws SQLException {
        String sql = "INSERT INTO student_subjects (student_id, subject_id, grade) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE grade=?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, studentId);
        ps.setInt(2, subjectId);
        ps.setDouble(3, grade);
        ps.setDouble(4, grade);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean updateGrade(int id, double grade) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE student_subjects SET grade=? WHERE id=?");
        ps.setDouble(1, grade);
        ps.setInt(2, id);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean remove(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM student_subjects WHERE id=?");
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public double getAverage(int studentId) throws SQLException {
        String sql = "SELECT AVG(grade) FROM student_subjects WHERE student_id=?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        double avg = rs.next() ? rs.getDouble(1) : 0;
        rs.close(); ps.close();
        return avg;
    }
}