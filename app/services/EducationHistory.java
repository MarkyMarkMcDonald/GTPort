package services;

/**
 * User: mark.mcdonald
 * Date: 12/3/12
 */
public class EducationHistory {
    public String schoolName;
    public String year;
    public String degree;
    public String major;
    public Float gpa;

    public EducationHistory(String schoolName, String year, String degree, String major, Float gpa) {
        this.schoolName = schoolName;
        this.year = year;
        this.degree = degree;
        this.major = major;
        this.gpa = gpa;
    }
}
