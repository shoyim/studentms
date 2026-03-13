package com.studentms.dao;

import com.studentms.model.Subject;
import com.studentms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<Subject> getAll() throws SQLException {
        List<Subject> list = new ArrayList<>();
        Statement st = DatabaseConnection.getConnection().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM subjects ORDER BY name");
        while (rs.next()) list.add(new Subject(rs.getInt("id"), rs.getString("name")));
        rs.close(); st.close();
        return list;
    }

    public boolean add(String name) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("INSERT INTO subjects (name) VALUES (?)");
        ps.setString(1, name.trim());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean delete(int id) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM subjects WHERE id=?");
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }

    public boolean update(Subject s) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE subjects SET name=? WHERE id=?");
        ps.setString(1, s.getName());
        ps.setInt(2, s.getId());
        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    }
}