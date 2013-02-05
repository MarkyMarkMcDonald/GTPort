package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: marky
 * Date: 12/3/12
 * Time: 1:45 PM
 */
public class Logic {

    public static String username;
    public static UserType userType;

    public static boolean login(String username, String password){
        List<HashMap<String, Object>> resultRows = DatabaseStatementExecutor.execute(DatabaseQueries.login, new String[]{username, password});
        if (!resultRows.isEmpty()){
            Logic.username = username;
            return true;
        } else {
            return false;
        }
    }

    public static void logout(){
        username = null;
        userType = null;
    }

    public static boolean findAndSetCurrentUserType(){
        List<HashMap<String, Object>> studentResultRows = DatabaseStatementExecutor.execute(DatabaseQueries.authenticateStudent, new String[]{username});
        List<HashMap<String, Object>> facultyResultRows = DatabaseStatementExecutor.execute(DatabaseQueries.authenticateFaculty, new String[]{username});
        List<HashMap<String, Object>> administratorResultRows = DatabaseStatementExecutor.execute(DatabaseQueries.authenticateAdministrator, new String[]{username});
        if (studentResultRows.size() > 0 && (Long) studentResultRows.get(0).get("Count(*)") > 0){
            Logic.userType = UserType.STUDENT;
        } else if (facultyResultRows.size() > 0  && (Long) facultyResultRows.get(0).get("Count(*)") > 0){
            Logic.userType = UserType.FACULTY;
        } else if (administratorResultRows.size() > 0 && (Long) administratorResultRows.get(0).get("Count(*)") > 0){
            Logic.userType = UserType.ADMINISTRATOR;
        }
        return true;
    }

    public static void createUser(String username, String password){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createAccount, new String[]{username, password});
    }

    public static void createStudent(String username, String degree, String major){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoStudent, new String[]{username, degree, major});
    }

    public static void createFaculty(String username){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoFaculty, new String[]{username});
    }


    public static void createPersonalInfoAll(String address, String name, String email, String DOB, String personalAddress, String gender, String contactNumber){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoAll, new String[]{username, address, name, email, DOB, personalAddress, gender, contactNumber});
    }

    public static void createPersonalInfoStudent(String degree, String major){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoStudent, new String[]{username, degree, major});
    }

    public static void createPersonalInfoTutor(List<String> courses){
        for (String course : courses){
            DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoTutor, new String[]{username, course});
        }
    }

    public static void createPersonalInfoFaculty(String departmentId, String position){
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoFaculty, new String[]{departmentId, position});
    }

    public static void createPersonalInfoEducationHistory(List<EducationHistory> histories){
        for (EducationHistory educationHistory : histories){
            DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoEducationHistory, new String[]{username, educationHistory.schoolName, educationHistory.year, educationHistory.degree, educationHistory.major, educationHistory.gpa.toString()});
        }
    }

    public static void createPersonalInfoResearchInterests(List<String> researchInterests){
        for (String interest : researchInterests){
            DatabaseStatementExecutor.executeUpdate(DatabaseQueries.createPersonalInfoResearchInterests, new String[]{username, interest});
        }
    }

    public static List<HashMap<String, Object>> viewPersonalInfoBase(){
        return DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoBase, new String[]{username});
    }

    public static List<HashMap<String, Object>> viewPersonalInfoStudent(){
        return DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoStudent, new String[]{username});
    }

    public static List<HashMap<String, Object>> viewPersonalInfoFaculty(){
        return DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoFaculty, new String[] {username});
    }

    public static boolean userHasEnteredPersonalInformation(){
        List<HashMap<String,Object>> regularPersonalInformation = DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoBase, new String[] {username});
        if (regularPersonalInformation.size() < 1){
            return false;
        }
        if (userType.equals(UserType.STUDENT)){
            List<HashMap<String,Object>> studentPersonalInformation = DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoStudent, new String[] {username});
            if (studentPersonalInformation.size() < 1){
                return false;
            }
        } else if (userType.equals(UserType.FACULTY)) {
            List<HashMap<String,Object>> facultyPersonalInformation = DatabaseStatementExecutor.execute(DatabaseQueries.viewPersonalInfoFaculty, new String[] {username});
            if (facultyPersonalInformation.size() < 1){
                return false;
            }
        }
        return true;
    }

    public static List<HashMap<String,Object>> administrativeReport(){
        return DatabaseStatementExecutor.execute(DatabaseQueries.firstReport, new String[] {});
    }

    public static ArrayList<ArrayList<HashMap<String,Object>>> facultyReport(){
        ArrayList<ArrayList<HashMap<String,Object>>> threeResults = new ArrayList<ArrayList<HashMap<String,Object>>>();
        DatabaseStatementExecutor.executeUpdate(DatabaseQueries.secondReportSetupView, new String[] {});
        threeResults.add(DatabaseStatementExecutor.execute(DatabaseQueries.secondReportNoMeetings, new String[]{}));
        threeResults.add(DatabaseStatementExecutor.execute(DatabaseQueries.secondReportLessThanThreeMeetings, new String[] {}));
        threeResults.add(DatabaseStatementExecutor.execute(DatabaseQueries.secondReportMoreThanTwoMeetings, new String[] {}));
        return threeResults;
    }

    public static List<HashMap<String,Object>> studentReport(){
        return DatabaseStatementExecutor.execute(DatabaseQueries.thirdReport, new String[] {});
    }


}
