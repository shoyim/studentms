package com.studentms.dao;

import com.studentms.model.Student;
import com.studentms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY course, full_name";
        Statement st = DatabaseConnection.getConnection().createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) list.add(map(rs));
        rs.close(); st.close();
        return list;
    }

    public List<Student> search(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_id LIKE ? ORDER BY full_name";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(map(rs));
        rs.close(); ps.close();
        return list;
    }

    public Student getById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Student s = rs.next() ? map(rs) : null;
        rs.close(); ps.close();
        return s;
    }

    public boolean add(Student s) throws SQLException {
        String sql = "INSERT INTO students (student_id, full_name, course) VALUES (?, ?, ?)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, s.getStudentId());
        ps.setString(2, s.getFullName());
        ps.setInt(3, s.getCourse());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET student_id=?, full_name=?, course=? WHERE id=?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, s.getStudentId());
        ps.setString(2, s.getFullName());
        ps.setInt(3, s.getCourse());
        ps.setInt(4, s.getId());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public int getLastInsertedId() throws SQLException {
        ResultSet rs = DatabaseConnection.getConnection()
                .createStatement().executeQuery("SELECT LAST_INSERT_ID()");
        return rs.next() ? rs.getInt(1) : -1;
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getInt("course")
        );
    }
}