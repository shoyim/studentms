package com.studentms.model;

public class StudentSubject {
    private int id;
    private int studentId;
    private int subjectId;
    private String subjectName;
    private double grade;

    public StudentSubject() {}

    public StudentSubject(int id, int studentId, int subjectId, String subjectName, double grade) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.grade = grade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
}