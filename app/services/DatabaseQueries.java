package services;

/**
 * User: mark.mcdonald
 * Date: 12/2/12
 */
public class DatabaseQueries {

    public static String login = "SELECT Username, Password " +
            "FROM User " +
            "WHERE Username = $Username AND Password = $Password;";

    /*
    Authentication
     */
    public static String authenticateAdministrator = "SELECT Count(*) FROM ADMINISTRATOR WHERE Username = $Username;";

    public static String authenticateStudent = "SELECT Count(*) FROM STUDENT WHERE Regular_Username = $Username;";

    public static String authenticateFaculty = "SELECT Count(*) FROM FACULTY WHERE Regular_Username = $Username;";

    /*
    Create Account
     */
    public static String createAccount = "INSERT INTO USER " +
            "VALUES ($Username, $Password);";

    /*
    Create Personal Info
     */
    public static String createPersonalInfoAll = "INSERT INTO REGULAR_USER " +
            "Values($Username, $Address, $Name, $Email, $DOB, $PAddress, $Gender, $Contact_number);";

    public static String createPersonalInfoStudent = "INSERT INTO STUDENT (Regular_Username,Degree,Major) " +
            "Values($Username, $Degree,  $Major);";

    public static String createPersonalInfoTutor = "INSERT INTO APPLIED_TO_TUTOR " +
            "Values($Username,$Course_Title);";

    public static String createPersonalInfoFaculty = "INSERT INTO FACULTY " +
            "Values($Username, ,$DID, $Pos);";

    public static String createPersonalInfoEducationHistory = "INSERT INTO EDUCATION_HISTORY " +
            "VALUES ($Username, $SchoolName, $year, $Degree, $Major,$GPA);";

    public static String createPersonalInfoResearchInterests = "INSERT INTO RESEARCH_INTERESTS " +
            "VALUES ($Username, $Interest);";

    public static String viewPersonalInfoBase = "SELECT * FROM REGULAR_USER WHERE Username = $Username";

    public static String viewPersonalInfoStudent = "SELECT * FROM STUDENT WHERE Regular_Username= $Username;";

    public static String viewPersonalInfoFaculty = "SELECT * FROM FACULTY WHERE Regular_Username = $Username;";

    public static String viewEducationHistories = "SELECT Name_of_School, Year_of_Grad, EDUCATION_HISTORY.Degree, EDUCATION_HISTORY.Major, GPA " +
            "FROM STUDENT JOIN EDUCATION_HISTORY on STUDENT.Regular_Username = EDUCATION_HISTORY.Student_Username " +
            "WHERE Student_Username = $Username;";

    public static String viewResearchInterest = "FROM FACULTY JOIN RESEARCH_INTERESTS on  FACULTY.Regular_Username = RESEARCH_INTERESTS.Faculty_Username" +
            "WHERE Faculty_Username = $Username;";

    public static String updatePersonalInformationRegUser = "UPDATE REGULAR_USER " +
            "SET Address = $Address, Full_Name = $Name, Email_Id =$Email, DOB =$DOB, Permanent_Address = $PAddress, Gender = $Gender, Contact_No =$Contact_number " +
            "WHERE Username = $Username;";

    public static String updatePersonalInformationStudent = "UPDATE STUDENT " +
            "SET Degree =$Degree, Major=$Major, Student_Id = $Sid " +
            "WHERE Regular_Username = $Username;";

    public static String updatePersonalInformationUpdateFaculty = "UPDATE FACULTY " +
            "SET Dept_Id=$DID, Position = $Pos " +
            "WHERE Regular_Username = $Username;";

    public static String updateEducationHistory = "UPDATE EDUCATION_HISTORY " +
            "SET  Degree= $Degree, Major= $Major, GPA= $GPA " +
            "WHERE Regular_Username =$Username AND Name_of_School = $SchoolName AND Year_of_Grad= $year;";

    public static String viewDepartments = "SELECT * FROM DEPARTMENT";


    public static String registerCourse = "INSERT INTO REGISTERS " +
            "Values($Username, $CRN, NULL, $Mode)";

    public static String registeredCourses = "SELECT  Code, Course_Title, Letter, Grade_Mode " +
            "FROM (REGISTERS JOIN SECTION on REGISTERS.Section_CRN = SECTION.CRN) NATURAL JOIN CODES " +
            "WHERE Student_Username= $Username;";

    public static String viewCandidates = "SELECT  Student_Username " +
            "FROM APPLIED_TO_TUTOR NATURAL JOIN SECTION " +
            "WHERE Faculty_Username =$Username;";

    public static String assignCandidateAppliedTutor = "DELETE FROM APPLIED_TO_TUTOR " +
            "WHERE Student_Username =$Tutor_Username AND Course_Title = $Title;";

    public static String assignCandidateInsertIntoTutor = "INSERT INTO TUTOR " +
            "Values($Tutor);";

    public static String assignCandidateTutorTitle = "INSERT INTO TUTORS_FOR " +
            "Values ($Tutor, $Title);";

    public static String viewGrades = "SELECT Student_Username, Student_Id, Grade " +
            "FROM (REGISTERS JOIN SECTION on REGISTERS.Section_CRN = SECTION.CRN) JOIN STUDENT on Student_Username = STUDENT.Regular_Username " +
            "WHERE Faculty_Username = $Username;";

    public static String assignGradesSectionCRN = "SELECT Section_CRN " + "FROM REGISTERS JOIN SECTION on REGISTERS.Section_CRN = SECTION.CRN "
            +  "WHERE Student_Username= $Student AND Faculty_Username= $Username";

    public static String assignGradesUpdateRegister = "UPDATE REGISTERS" +"SET Grade=$Grade "+
            "WHERE Student_Username=$Student AND Section_CRN=$CRN;";

    public static String findTutors = "SELECT Code, Course_Title, Full_Name, Email_Id " +
            "FROM (TUTORS_FOR NATURAL JOIN CODES) JOIN REGULAR_USER on Tutor_Username = REGULAR_USER.Username;";

    public static String visitDetailsTutorCode = "SELECT Code" + "FROM TUTORS_FOR NATURAL JOIN CODES " +
            "WHERE Tutor_Username= $Username;";

    public static String visitDetailsStudentName = "SELECT Full_Name, Username " +
            "FROM  STUDENT, REGULAR_USER " + "WHERE Student_Id= $SID AND Regular_Username = Username;";

    public static String addVisitSelectCrn = "SELECT CRN FROM (REGISTERS JOIN SECTION ON REGISTERS.Section_CRN = SECTION.CRN) NATURAL " +
            "JOIN CODES WHERE Student_Username =$Student_name AND Code = $Code;";

    public static String addVisitUpdateLogVisits = "UPDATE INTO LOG_VISITS " +
            "Value($Username, $Student_Name, $CRN);";

    public static String addVisitsDayTime = "INSERT INTO DATE_TIME "
            + "Value($Username, $Student_Name, $CRN, $Date_Time);";

    public static String firstReport = "SELECT Code, Course_Title, AVG(GRADE) AS Average_Grade, COUNT(GRADE) AS Number_Grades\n" +
            "FROM (REGISTERS JOIN SECTION ON REGISTERS.Section_CRN = SECTION.CRN) NATURAL JOIN CODES\n" +
            "GROUP BY Code;\n";

    /*
        Second Report
     */

    public static String secondReportSetupView = "CREATE OR REPLACE VIEW GRADES AS " +
            "SELECT  Code, Course_title, Count(Date_Time) AS No_Meetings, Grade " +
            "FROM  ((REGISTERS JOIN DATE_TIME ON Section_CRN = DATE_TIME.Section) JOIN SECTION ON Section_CRN = SECTION.CRN) NATURAL JOIN CODES " +
            "GROUP BY Code, Student_Username;";

    public static String secondReportNoMeetings = "SELECT Code, Course_title, No_Meetings, AVG(Grade) as Average_Grade FROM GRADES " +
            "GROUP BY CODE " +
            "HAVING No_Meetings = \"0\";";

    public static String secondReportLessThanThreeMeetings = "SELECT Code, Course_title, No_Meetings, AVG(Grade) as Average_Grade FROM GRADES " +
            "GROUP BY CODE " + "HAVING No_Meetings > \"0\" AND No_Meetings < \"3\";";

    public static String secondReportMoreThanTwoMeetings = "SELECT Code, Course_title, No_Meetings, AVG(Grade) as Average_Grade FROM GRADES " +
            "GROUP BY CODE " + "HAVING No_Meetings > \"2\";";

    /*
    Third Report
     */

    public static String thirdReport = "SELECT Faculty_Username, Code, Course_Title, AVG(Grade) AS Average_Grade " +
            "FROM (REGISTERS JOIN SECTION on REGISTERS.Section_CRN = SECTION.CRN) " +
            "NATURAL JOIN CODES " + "GROUP BY Faculty_Username, Code;";

    /*
    Not implemented
     */

    public static String addCourseIntoCourse = "INSERT INTO COURSE " +
            "VALUES($Title);";

    public static String addCourseIntoCodes =
            "INSERT INTO CODES" + "VALUES( $Title, $Code);";

    public static String addCourseIntoSection = "INSERT INTO SECTION " +
            "VALUES($CRN, $Time, $Day, $Location, $Term, $Letter, $Title, $instructor);";

    public static String addCourseIntoOffers = "INSERT INTO OFFERS " +
            "VALUES($Department, $Title);";


}
