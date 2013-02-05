package models;

import java.math.BigDecimal;

/**
 * User: mark.mcdonald
 * Date: 12/4/12
 */
public class StudentReportRow {
    public String instructor;
    public String courseCode;
    public String courseName;
    public BigDecimal gpa;

    public StudentReportRow(String instructor, String courseCode, String courseName, BigDecimal gpa) {
        this.instructor = instructor;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.gpa = gpa;
    }
}
