import java.sql.ResultSet;
import java.sql.SQLException;

public class Student extends Users {
    static String name;
    static int semester;
    static double cgpa;
    static double sgpa;
    static int studentId;
    String email = getEmail();
    String password = getPassword();
    private DatabaseHelper dbHelper;

    Student(String email, String password) {
        super(email, password);
        try {
            dbHelper = new DatabaseHelper(); // Creating an instance of DatabaseHelper
            getInfo();
        } catch (Exception e) {
            System.out.println("Error occurred while initializing DatabaseHelper: " + e.getMessage());
            System.exit(1);
        } finally {
            try{
                dbHelper.closeConnection();
            }catch (SQLException e) {
                System.out.println("Error Occurred :"+e.getMessage());
            }
            System.exit(0);
        }
    }

   public void getInfo() {
        System.out.println("1- Log in ");
        System.out.println("2- Sign Up");
        System.out.println("----------------------------------");
        int n = Main.scanner.nextInt();
        if (n == 1) {
            if (isValidUser()) {
                System.out.println("Successfully Logged In!");
                System.out.println("----------------------------------");
                try {
                    String query = "select * from Students where email= ?";
                    ResultSet resultSet = dbHelper.executeQuery(query,email);
                    if (resultSet.next()) {
                        name = resultSet.getString("name");
                        semester = resultSet.getInt("semester");
                        studentId = resultSet.getInt("student_id");
                        functionality();
                    } else {
                        System.out.println("Error occurred !! Try again");
                        System.out.println("----------------------------------");
                        System.exit(1);
                    }
                    dbHelper.closeResultConnection(resultSet);
                } catch (SQLException e) {
                    System.out.println("Error occurred : " + e.getMessage());
                    System.out.println("----------------------------------");
                    System.exit(1);
                }
            }
        } else {
            signUp();
        }
    }

    @Override
    public boolean isValidUser() {
        try {
            String query = "select * from Users where email= ?";
            ResultSet resultSet = dbHelper.executeQuery(query,email);

            if (resultSet.next()) {
                String tempPassword = resultSet.getString("password");
                if (this.password.equals(tempPassword)) {
                    return true;
                } else {
                    System.out.println("Wrong Credential try again!!");
                    System.out.println("----------------------------------");
                    System.exit(0);
                }
            } else {
                System.out.println("Details not found!! Please sign up");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.exit(1);
        }
        return false;
    }

    @Override
   public void functionality() {
        System.out.println("Enter what you want to do: ");
        System.out.println("1 - View Courses");
        System.out.println("2 - Register for a Course");
        System.out.println("3 - View Schedule");
        System.out.println("4 - Track Academic Progress");
        System.out.println("5 - Drop Courses");
        System.out.println("6 - Submit Complaints:");
        System.out.println("7 - View Complaint Status:");
        System.out.println("8 - Log out");
        System.out.println("----------------------------------");
        int task = Main.scanner.nextInt();
        switch (task) {
            case 1:
                viewCourse(0);
                functionality();
                break;
            case 2:
                registerCourse();
                functionality();
                break;
            case 3:
                viewSchedule();
                functionality();
                break;
            case 4:
                trackAcademicProgress();
                functionality();
                break;
            case 5:
                dropCourse();
                functionality();
                break;
            case 6:
                registerComplaint();
                functionality();
                break;
            case 7:
                viewComplaint();
                functionality();
                break;
            case 8:
                logOut();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                System.out.println("----------------------------------");
                functionality();
                break;
        }
    }

    public boolean viewCourse(int temp) {
        try {
            String query = "SELECT * FROM Courses WHERE semester = ?";
            ResultSet resultSet = dbHelper.executeQuery(query,semester);
            if(temp==1){
                query="SELECT * " +
                        "FROM Courses c " +
                        "WHERE semester = ? " +
                        "AND NOT EXISTS ( " +
                        "    SELECT 1  " +
                        "    FROM Enrollments e " +
                        "    WHERE e.course_code = c.course_code  " +
                        "    AND e.student_id = ? " +
                        ") ";
                resultSet=dbHelper.executeQuery(query,semester,studentId);
            }

            if (resultSet.next()) {
                do {
                    System.out.println("Course Id    : " + resultSet.getInt("course_id"));
                    System.out.println("Course Code  : " + resultSet.getString("course_code"));
                    System.out.println("Title        : " + resultSet.getString("title"));
                    System.out.println("Professor Id : " + resultSet.getInt("professor_id"));
                    System.out.println("Credits      : " + resultSet.getInt("credits"));
                    System.out.println("Prerequisites: " + resultSet.getString("prerequisites"));
                    System.out.println("Timings      : " + resultSet.getString("timings"));
                    System.out.println("Semester     : " + resultSet.getInt("semester"));
                    System.out.println("Location     : " + resultSet.getString("location"));
                    System.out.println("Syllabus     : " + resultSet.getString("syllabus"));
                    System.out.println("Course Limit : " + resultSet.getInt("course_limit"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No courses available.");
                System.out.println("----------------------------------");
                return  false;
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }
        return true;
    }

   public void registerCourse() {
        System.out.println("You can only register for the following courses:");
        if(viewCourse(1)) {
            System.out.println("Enter the number of courses you want to register:");
            int n = Main.scanner.nextInt();
            int totalCredits = 0;

            try {
                // Calculating total credits for the current semester courses already enrolled by the student
                String totalCreditsQuery = "SELECT SUM(c.credits) AS total_credits " +
                        "FROM Enrollments e " +
                        "JOIN Courses c ON e.course_code = c.course_code " +
                        "WHERE e.student_id = ? AND c.semester = ?";
                ResultSet resultSet = dbHelper.executeQuery(totalCreditsQuery, studentId, semester);

                if (resultSet.next()) {
                    totalCredits = resultSet.getInt("total_credits");
                }

                // Loop to register for each course
                while (n-- > 0) {
                    System.out.println("Enter the course code: ");
                    String courseCode = Main.scanner.next();

                    String courseQuery = "SELECT * FROM Courses WHERE course_code = ?";
                    resultSet = dbHelper.executeQuery(courseQuery, courseCode);

                    if (!resultSet.next()) {
                        System.out.println("Enter the correct course code!!");
                        continue; //skipping this course
                    }

                    String checkEnrollmentQuery = "SELECT * FROM Enrollments WHERE course_code = ? AND student_id = ?";
                    ResultSet resultSet1 = dbHelper.executeQuery(checkEnrollmentQuery, courseCode, studentId);

                    if (resultSet1.next()) {
                        System.out.println("Already course registered. Try another one.");
                        System.out.println("----------------------------------");
                        continue;
                    }

                    int courseCredits = resultSet.getInt("credits");

                    // Checking current number of registrations
                    int courseLimit = resultSet.getInt("course_limit");
                    int noOfRegistrations = resultSet.getInt("no_of_registration");

                    if (noOfRegistrations >= courseLimit) {
                        System.out.println("The course has reached its limit of registrations. You cannot register for this course.");
                        System.out.println("----------------------------------");
                        continue;
                    }

                    // Checking credit limit
                    if (totalCredits + courseCredits > 20) {
                        System.out.println("Total credits cannot exceed 20 for the current semester. You cannot register for this course.");
                        continue;
                    }

                    String prerequisites = resultSet.getString("prerequisites");
                    if (prerequisites != null) {
                        String prerequisiteQuery = "SELECT * FROM Enrollments WHERE student_id = ? AND course_code = ?";
                        resultSet = dbHelper.executeQuery(prerequisiteQuery, studentId, prerequisites);

                        if (!resultSet.next()) {
                            System.out.println("You can't enroll in this course as you haven't completed the prerequisites, try a different one.");
                            System.out.println("----------------------------------");
                            continue;
                        }
                    }

                    // Register for the course
                    String registerQuery = "INSERT INTO Enrollments (student_id, course_code, status, semester) VALUES (?, ?, ?, ?)";
                    int rowsAffected = dbHelper.executeUpdate(registerQuery, studentId, courseCode, "Registered", semester);

                    if (rowsAffected > 0) {
                        System.out.println("Successfully registered.");
                        System.out.println("----------------------------------");
                        totalCredits += courseCredits;

                        String updateRegistrationsQuery = "UPDATE Courses SET no_of_registration = no_of_registration + 1 WHERE course_code = ?";
                        dbHelper.executeUpdate(updateRegistrationsQuery, courseCode);
                    } else {
                        System.out.println("Registration failed!! Try again.");
                    }
                }
                dbHelper.closeResultConnection(resultSet);

            } catch (SQLException e) {
                System.out.println("Error occurred: " + e.getMessage());
                System.out.println("----------------------------------");
                System.exit(1);
            }
        }
    }


   public void viewSchedule() {
        try {
            String query = "SELECT c.title, c.timings, c.course_code, c.location, p.name AS professor_name FROM Courses c  JOIN Professors p ON c.professor_id = p.professor_id WHERE c.course_code IN ( SELECT e.course_code FROM Enrollments e WHERE e.student_id = ? and semester=? )";
            ResultSet resultSet = dbHelper.executeQuery(query,studentId,semester);
            if (resultSet.next()) {
                do {
                    System.out.println("Course title   : " + resultSet.getString("title"));
                    System.out.println("Course Code    : " + resultSet.getString("course_code"));
                    System.out.println("Timing         : " + resultSet.getString("timings"));
                    System.out.println("Location       : " + resultSet.getString("location"));
                    System.out.println("Professor Name : " + resultSet.getString("professor_name"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No data available");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }
    }

   public void trackAcademicProgress() {
        try {
            String query = "SELECT course_code FROM Enrollments WHERE student_id = ? AND semester = ? AND status = 'Failed'";
            ResultSet resultSet = dbHelper.executeQuery(query, studentId, semester);
            boolean hasFailedCourses = false;

            while (resultSet.next()) {
                hasFailedCourses = true;
                String failedCourseCode = resultSet.getString("course_code");
                System.out.println("You failed in course: " + failedCourseCode);
            }
            // SGPA Calculation
                query = "SELECT " +
                        " SUM(e.point * c.credits) AS total_points, " +
                        " SUM(c.credits) AS total_credits " +
                        "FROM " +
                        " Enrollments e " +
                        "JOIN Courses c ON e.course_code = c.course_code " +
                        "WHERE " +
                        " e.student_id = ? " +
                        " AND e.semester = ? " +
                        " AND e.status = 'Completed'";

                resultSet = dbHelper.executeQuery(query, studentId, semester);
                if (resultSet.next()) {
                    double totalPoints = resultSet.getDouble("total_points");
                    double totalCredits = resultSet.getDouble("total_credits");

                    if (totalCredits > 0) {
                        sgpa = totalPoints / totalCredits;
                        System.out.println("Name     : " + name);
                        System.out.println("Id       : " + studentId);
                        System.out.println("Semester : " + semester);
                        System.out.println("SGPA     : " + String.format("%.2f", sgpa));  // Format SGPA to 2 decimal places
                    } else {
                        System.out.println("No completed courses found for SGPA.");
                    }
                } else {
                    System.out.println("Data not present!! Try again");
                    System.out.println("----------------------------------");
                }

            if (!hasFailedCourses) {
                // CGPA Calculation
                query = "SELECT SUM(e.point * c.credits) AS total_points, SUM(c.credits) AS total_credits " +
                        "FROM Enrollments e " +
                        "JOIN Courses c ON e.course_code = c.course_code " +
                        "WHERE e.student_id = ? AND e.status = 'Completed'";

                resultSet = dbHelper.executeQuery(query, studentId);
                if (resultSet.next()) {
                    double totalPoints = resultSet.getDouble("total_points");
                    double totalCredits = resultSet.getDouble("total_credits");

                    if (totalCredits > 0) {
                        cgpa = totalPoints / totalCredits;
                        System.out.println("CGPA       : " + String.format("%.2f", cgpa));  // Formating CGPA to 2 decimal places
                        System.out.println("----------------------------------");
                        String updateQuery = "UPDATE Students SET cgpa = ? WHERE student_id = ?";
                        dbHelper.executeUpdate(updateQuery, cgpa, studentId);  // Update the CGPA in the database
                        System.out.println("CGPA updated successfully for student ID: " + studentId);
                    } else {
                        System.out.println("No completed courses found for CGPA.");
                    }
                } else {
                    System.out.println("Data not present!! Try again.");
                    System.out.println("----------------------------------");
                }
            } else {
                System.out.println("Cannot calculate  CGPA due to failed courses.");
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }
    }



   public void dropCourse() {
        try {
            String str="select * from Enrollments where  student_id= ? and status='Registered'";
            ResultSet resultSet1=dbHelper.executeQuery(str,studentId);
            if(resultSet1.next()){
                System.out.println("Courses yor are registered :");
                do{
                    System.out.println(("Course code :"+resultSet1.getString("course_code")));
                }while(resultSet1.next());
            }else{
                System.out.println("You are not registered any course. Do registration.");
                System.out.println("----------------------------------");
                return;
            }
            System.out.println("Enter the course code to drop : ");
            String courseCode = Main.scanner.next();
            String query = "DELETE FROM Enrollments WHERE course_code = ? AND student_id = ? AND status='Registered' ";
            int result = dbHelper.executeUpdate(query,courseCode,studentId);
            if (result > 0) {
                System.out.println("Course " + courseCode + " successfully dropped.");
                System.out.println("----------------------------------");
            } else {
                System.out.println("No such course found try again.");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet1);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }
    }

   public void registerComplaint() {
        System.out.println("Explain your complaint in detail : ");
        Main.scanner.nextLine();
        String description = Main.scanner.nextLine();
        try {
            String query = "INSERT INTO Complaints (student_id, description) VALUES (?, ?)";
            int rowsAffected = dbHelper.executeUpdate(query,studentId,description);

            if (rowsAffected > 0) {
                System.out.println("Complaint registered successfully.");
                System.out.println("----------------------------------");
            } else {
                System.out.println("Failed to register complaint.");
                System.out.println("----------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }
    }

   public void viewComplaint() {
        System.out.println("Your Complaints : ");
        try {
            String query = "select * from Complaints where student_id= ?";
            ResultSet resultSet = dbHelper.executeQuery(query,studentId);
            if (resultSet.next()) {
                do {
                    System.out.println("Complaint Id  :" + resultSet.getInt("complaint_id"));
                    System.out.println("Name          : " + name);
                    System.out.println("Description   :" + resultSet.getString("description"));
                    System.out.println("Status         : " + resultSet.getString("status"));
                    System.out.println("Solution      : " + resultSet.getString("resolution"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No complaint registered.");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);
        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }
    }

   public void signUp() {
        try {
            String query = "select * from Users where email=?";
            ResultSet resultSet = dbHelper.executeQuery(query,email);

            if (resultSet.next()) {
                System.out.println("User already exist!! Please login");
                System.out.println("----------------------------------");
                getInfo();
            } else {
                System.out.println("Enter your name : ");
                Main.scanner.nextLine();
                name = Main.scanner.nextLine();
                System.out.println("Enter your phone number");
                String phn = Main.scanner.next();
                System.out.println("Enter your address");
                Main.scanner.nextLine();
                String address = Main.scanner.nextLine();
                System.out.println("Enter your semester :");
                int semester = Main.scanner.nextInt();

                String sql = "INSERT INTO Students (name, email, semester,phone_number, address,password, status) "
                        +
                        "VALUES ( ?, ?, ?, ?, ?, ?, 'No')";

                int rowsInserted = dbHelper.executeUpdate(sql,name,email,semester,phn,address,password);

                if (rowsInserted > 0) {
                    System.out.println("Signed Up successfully! Waiting for admin to confirm");
                    System.out.println("----------------------------------");
                    System.exit(0);
                } else {
                    System.out.println("Signed Up failed!! Try again");
                    System.out.println("----------------------------------");
                    signUp();
                }

            }
            dbHelper.closeResultConnection(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("----------------------------------");
            System.exit(1);
        }

    }
}