package com.studentms.model;

public class Student {
    private int id;
    private String studentId;
    private String fullName;
    private int course;

    public Student() {}

    public Student(int id, String studentId, String fullName, int course) {
        this.id = id;
        this.studentId = studentId;
        this.fullName = fullName;
        this.course = course;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getCourse() { return course; }
    public void setCourse(int course) { this.course = course; }

    @Override
    public String toString() { return fullName + " (" + studentId + ")"; }
}