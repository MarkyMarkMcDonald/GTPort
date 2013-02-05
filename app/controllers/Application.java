package controllers;

import models.*;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import services.DatabaseQueries;
import services.DatabaseStatementExecutor;
import services.Logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Application extends Controller {

    @Before
    public static void addDetailsToTemplate(){
        renderArgs.put("username",Logic.username);
        renderArgs.put("usertype",Logic.userType);
    }

    public static void index() {
        if (Logic.username != null){
            String errorMessage = "";
            dashboard(errorMessage);
        } else {
            render();
        }
    }

    public static void processLogin(String username, String password){
        if (Logic.login(username,password)){
            Logic.findAndSetCurrentUserType();
            String errorMessage = "";
            dashboard(errorMessage);
        } else {
            index();
        }
    }

    public static void processLogOut(){
        Logic.logout();
        index();
    }

    public static void createStudentUser(String errorMessage){
        render(errorMessage);
    }

    public static void createFacultyUser(String errorMessage){
        render(errorMessage);
    }

    public static void processCreateStudent(String username, String password, String confirmPassword, String address, String name, String email, Date dob, String personalAddress, String gender, String contactNumber, String degree, String major, List<PreviousEducation> previousEducations){
        if (!password.equals(confirmPassword)){
            createStudentUser("Passwords do not match");
        } else if (username.length() < 1){
            createStudentUser("Username must have at least one character");
        }
        // Make sure username isn't already in the database
        if (DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoBase,new String[] {username, password}).size() == 0){
            Logic.createUser(username, password);
            DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoAll, new String[] {username, address, name, email, new java.sql.Date(dob.getTime()).toString(), personalAddress, gender, contactNumber});
            Logic.createStudent(username, degree, major);
            for (PreviousEducation previousEducation: previousEducations){
                DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoEducationHistory, new String[] {username, previousEducation.institutionName, previousEducation.gradYear, previousEducation.degree, previousEducation.major, previousEducation.gpa} );
            }
            index();
        } else {
            createStudentUser("That username is already taken");
        }
    }

    public static void processCreateFaculty(String username, String password, String confirmPassword, String address, String name, String email, Date dob, String personalAddress, String gender, String contactNumber, Long departmentId, String position){
        if (!password.equals(confirmPassword)){
            createFacultyUser("Passwords do not match");
        } else if (username.length() < 1){
            createFacultyUser("Username must have at least one character");
        }
        // Make sure username isn't already in the database
        if (DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoBase,new String[] {username, password}).size() == 0){
            Logic.createUser(username, password);
            DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoAll, new String[] {username, address, name, email, dob.toString(), personalAddress, gender, contactNumber});
            DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoFaculty, new String[] {username, Long.toString(departmentId),position});
            index();
        } else {
            createStudentUser("That username is already taken");
        }
    }

    public static void dashboard(String errorMessage){
        render(errorMessage);
    }

    public static void studentPersonalInformation(){
        HashMap<String, Object> baseInfo = Logic.viewPersonalInfoBase().get(0);
        HashMap<String,Object> studentInfo = Logic.viewPersonalInfoStudent().get(0);

        // Regular User
        String fullName = (String) baseInfo.get("Full_Name");
        Date dob = (Date) baseInfo.get("DOB");
        String gender = (String) baseInfo.get("Gender");
        String address = (String) baseInfo.get("Address");
        String permanentAddress = (String) baseInfo.get("Permanent_Address");
        String contactNumber = (String) baseInfo.get("Contact_No");
        String email = (String) baseInfo.get("Email_Id");

        // Student
        String major = (String) studentInfo.get("Major");
        long studentId = (Long) studentInfo.get("Student_Id");
        String degree = (String) studentInfo.get("Degree");

        render(fullName, dob,gender, address, permanentAddress, contactNumber, email, major, studentId, degree);
    }

    public static void processUpdateStudentPersonalInformation(String fullName, Date dob, String gender, String address, String permanentAddress, String contactNumber, String email, String major, Long studentId, String degree ){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.updatePersonalInformationRegUser, new String[] {Logic.username, fullName, dob.toString(), gender, address, permanentAddress, contactNumber, email, major,  Long.toString(studentId), degree});
    }

    public static void facultyPersonalInformation(String errorMessage){
        List<HashMap<String, Object>> baseInfo = Logic.viewPersonalInfoBase();
        List<HashMap<String, Object>> facultyInfo = Logic.viewPersonalInfoFaculty();
        HashMap<String,Object> firstRegInfo = baseInfo.get(0);
        HashMap<String,Object> firstFacultyInfo = facultyInfo.get(0);
        String fullName = (String) firstRegInfo.get("Full_Name");
        Date dob = (Date) firstRegInfo.get("DOB");
        String gender = (String) firstRegInfo.get("Gender");
        String address = (String) firstRegInfo.get("Address");
        String permanentAddress = (String) firstRegInfo.get("Permanent_Address");
        String contactNumber = (String) firstRegInfo.get("Contact_No");
        String email = (String) firstRegInfo.get("Email_Id");
        Integer departmentId = (Integer) firstFacultyInfo.get("Dept_Id");
        String position = (String) firstFacultyInfo.get("Position");
        render(fullName, dob, gender, address, permanentAddress, contactNumber, email, departmentId, position);
    }

    public static void processUpdateFacultyPersonalInformation(String fullName, Date dob, String gender, String address, String permanentAddress, String contactNumber, String email, Long departmentId, String position){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.updatePersonalInformationRegUser, new String[] {address, fullName, email,dob.toString(), permanentAddress, gender, contactNumber});
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.updatePersonalInformationUpdateFaculty, new String[] {departmentId.toString(), position, Logic.username});
        dashboard("");
    }

    public static void administrativeReport(){
        List<HashMap<String,Object>> results = Logic.administrativeReport();
        HashMap<String, AdminReportRow> courseNames = new HashMap<String, AdminReportRow>();
        for (HashMap<String,Object> result : results){
            String courseName = (String) result.get("Course_Title");
            String courseCode = (String) result.get("Code");
            BigDecimal gpa = (BigDecimal) result.get("Average_Grade");
            Long numberOfGrades = (Long) result.get("Number_Grades");
            if (!courseNames.containsKey(courseName)){
                AdminReportRow newCourseTitle = new AdminReportRow();
                newCourseTitle.courseName = courseName;
                newCourseTitle.courseCode = courseCode;
                newCourseTitle.numberOfGrades = numberOfGrades;
                newCourseTitle.gpa = gpa;
                courseNames.put(courseName, newCourseTitle);
            } else {
                AdminReportRow oldCourseTitle = courseNames.get(courseName);
                oldCourseTitle.courseCode += " / " + courseCode;
                courseNames.remove(courseName);
                courseNames.put(courseName, oldCourseTitle);
            }
        }
        List<AdminReportRow> averages = new ArrayList<AdminReportRow>(courseNames.values()) ;
        render(averages);
    }

    public static void facultyReport(){
        ArrayList<ArrayList<HashMap<String,Object>>> threeResults = Logic.facultyReport();
        HashMap<String,FacultyReportSetOfThree> mapOfCourseNamesAndResultSets = new HashMap<String, FacultyReportSetOfThree>();
        int counter = 0;
        for (ArrayList<HashMap<String,Object>> results : threeResults){
            for (HashMap<String,Object> result: results){
                String courseName = (String) result.get("Course_title");
                String courseCode = (String) result.get("Code");
                BigDecimal gpa = (BigDecimal) result.get("Average_Grade");
                if (mapOfCourseNamesAndResultSets.containsKey(courseName)){
                    FacultyReportSetOfThree set = mapOfCourseNamesAndResultSets.get(courseName);
                    if (!set.courseCode.contains(courseCode)){
                        set.courseCode+= (" / " + courseCode);
                    } else {}
                    switch (counter){
                        case 0:
                            set.noVisitsGpa = gpa;
                            break;
                        case 1:
                            set.lowVisitsGpa = gpa;
                            break;
                        case 2:
                            set.highVisitsGpa = gpa;
                            break;
                    }
                    mapOfCourseNamesAndResultSets.remove(courseName);
                    mapOfCourseNamesAndResultSets.put(courseName,set);
                } else {
                    FacultyReportSetOfThree set = new FacultyReportSetOfThree();
                    set.courseCode = courseCode;
                    set.courseName = courseName;
                    switch (counter){
                        case 0:
                            set.noVisitsGpa = gpa;
                            break;
                        case 1:
                            set.lowVisitsGpa = gpa;
                            break;
                        case 2:
                            set.highVisitsGpa = gpa;
                            break;
                    }
                    mapOfCourseNamesAndResultSets.put(courseName,set);
                }

            }
            counter++;
        }
        ArrayList<FacultyReportSetOfThree> finalResults = new ArrayList<FacultyReportSetOfThree>(mapOfCourseNamesAndResultSets.values());
        render(finalResults);
    }

    public static void studentReport(){
        List<HashMap<String,Object>> results = Logic.studentReport();
        List<StudentReportRow> resultRows = new ArrayList<StudentReportRow>();
        for (HashMap<String,Object> result : results){
            String courseName = (String) result.get("Course_Title");
            String courseCode = (String) result.get("Code");
            String instructor = (String) result.get("Faculty_Username");
            BigDecimal gpa = (BigDecimal) result.get("Average_Grade");
            StudentReportRow studentReportRow = new StudentReportRow(instructor, courseCode,courseName, gpa);
            resultRows.add(studentReportRow);
        }
        render(resultRows);
    }

    public static void chooseDepartmentToRegisterForCourse(){
        ArrayList<HashMap<String, Object>> departments = DatabaseStatementExecutor.execute(DatabaseQueries.viewDepartments, new String[]{});
        ArrayList<Department> departmentsAsList = new ArrayList<Department>();
        for (HashMap<String, Object> department : departments){
            Department newDepartment = new Department();
            newDepartment.id = (Integer) department.get("Dept_Id");
            newDepartment.name = (String) department.get("Name");
            departmentsAsList.add(newDepartment);
        }
        Cache.set("departments",departmentsAsList,"59mn");
        render(departmentsAsList);
    }

    public static void registerForCourses(Integer departmentId){
        for (Department department : (ArrayList<Department>) Cache.get("departments")){
            if (department.id.equals(departmentId)){
//                DatabaseStatementExecutor.execute(DatabaseQueries)
                render(department);
            }
        }
    }


}