import java.sql.ResultSet;
import java.sql.SQLException;

public class Professor extends Users {
    static int professorId;
    static String name;
    String email = getEmail();
    String password = getPassword();
    private DatabaseHelper dbHelper;

    Professor(String email, String password) {
        super(email, password);
        try {
            dbHelper = new DatabaseHelper();
            getInfo();
        } catch (Exception e) {
            System.out.println("Error occurred while initializing DatabaseHelper: " + e.getMessage());
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
                System.out.println("Logged in Successfully!!");
                System.out.println("----------------------------------");
                try {
                    String query = "SELECT * FROM Professors WHERE email = ?";
                    ResultSet resultSet = dbHelper.executeQuery(query, email);
                    if (resultSet.next()) {
                        name = resultSet.getString("name");
                        professorId = resultSet.getInt("professor_id");
                        functionality();
                    } else {
                        System.out.println("Error occurred !! Try again");
                        System.out.println("----------------------------------");
                    }
                } catch (SQLException e) {
                    System.out.println("Error occurred: " + e.getMessage());
                    System.out.println("----------------------------------");
                }
            }
        } else {
            signUp();
        }
    }

    @Override
    public boolean isValidUser() {
        try {
            String query = "SELECT * FROM Users WHERE email = ?";
            ResultSet resultSet = dbHelper.executeQuery(query, this.email);

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
                System.out.println("User not found please sign up");
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
        }
        return false;
    }

    @Override
    public void functionality() {
        System.out.println("What task do you want to do: ");
        System.out.println("1- Manage Courses");
        System.out.println("2- View enrolled students");
        System.out.println("3- Log out");
        System.out.println("----------------------------------");
        int task = Main.scanner.nextInt();
        switch (task) {
            case 1:
                manageCourse();
                functionality();
                break;
            case 2:
                viewEnrolledStudents();
                functionality();
                break;
            case 3:
                logOut();
                break;
            default:
                break;
        }
    }

    public void manageCourse() {
        System.out.println("1- View your course");
        System.out.println("2- Update your course");
        int task = Main.scanner.nextInt();

        if (task == 1) {
           viewCourse();
        } else if (task == 2) {
            updateCourse();
        } else {
            System.out.println("Invalid input!! Try again");
            System.out.println("----------------------------------");
            manageCourse();
        }
    }
    public void viewCourse(){
        try {
            String query = "SELECT * FROM Courses  WHERE professor_id = ?";
            ResultSet resultSet = dbHelper.executeQuery(query, professorId);

            if (resultSet.next()) {
                do {
                    System.out.println("Course Id     : " + resultSet.getInt("course_id"));
                    System.out.println("Course Code   : " + resultSet.getString("course_code"));
                    System.out.println("Title         : " + resultSet.getString("title"));
                    System.out.println("Credits       : " + resultSet.getInt("credits"));
                    System.out.println("Prerequisites : " + resultSet.getString("prerequisites"));
                    System.out.println("Timings       : " + resultSet.getString("timings"));
                    System.out.println("Semester      : " + resultSet.getInt("semester"));
                    System.out.println("Location      : " + resultSet.getString("location"));
                    System.out.println("Syllabus      : " + resultSet.getString("syllabus"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No courses found!! ");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
        }
    }


    public void updateCourse(){
        System.out.println("Your Courses : \n\n");
        viewCourse();
        try {
            System.out.println("Enter what changes you want to do (syllabus, timings, credits, prerequisites, enrollment limits, office hours)");
            String update = Main.scanner.next();
            System.out.println("Enter the course code");
            String course_code = Main.scanner.next();
            System.out.println("Enter the updated value:");
            Main.scanner.nextLine();
            String updatedValue = Main.scanner.nextLine();

            String query = "UPDATE Courses SET " + update + " = ? WHERE course_code = ?";
            if (update.equals("office_hours")) {
                query = "UPDATE Professors SET office_hours = ? WHERE professor_id = (SELECT professor_id FROM Courses WHERE course_code = ?)";
            }
            int result = dbHelper.executeUpdate(query,updatedValue,course_code);

            if (result > 0) {
                System.out.println("Successfully Updated!! ");
                System.out.println("----------------------------------");
                functionality();
            } else {
                System.out.println("Update failed !! Try again");
                System.out.println("----------------------------------");
                manageCourse();
            }

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
        }
    }
    public void viewEnrolledStudents() {
        System.out.println("Your Courses : \n\n");
        viewCourse();
        System.out.println("Enter the course code: ");
        String course_code = Main.scanner.next();

        try {
            String query = "SELECT * FROM Students WHERE student_id IN (SELECT student_id FROM Enrollments WHERE course_code = ?)";
            ResultSet resultSet = dbHelper.executeQuery(query, course_code);

            if (resultSet.next()) {
                do {
                    System.out.println("Student ID    : " + resultSet.getInt("student_id"));
                    System.out.println("Student Name  : " + resultSet.getString("name"));
                    System.out.println("Student Email : " + resultSet.getString("email"));
                    System.out.println("Semester      : " + resultSet.getInt("semester"));
                    System.out.println("CGPA          : " + resultSet.getBigDecimal("cgpa"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No students enrolled in this course");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);
        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
        }
    }

    public void signUp() {
        try {
            String query = "SELECT * FROM Users WHERE email = ?";
            ResultSet resultSet = dbHelper.executeQuery(query, email);
            if (resultSet.next()) {
                System.out.println("User already exists!! Please login");
                System.out.println("----------------------------------");
                getInfo();
            } else {
                System.out.println("Enter your name: ");
                Main.scanner.nextLine();
                name = Main.scanner.nextLine();

                System.out.println("Enter your phone number");
                String phn = Main.scanner.next();
                if (phn.length() < 10 || phn.length() > 15) {
                    System.out.println("Invalid phone number! Please enter a valid phone number.");
                    return;
                }

                System.out.println("Enter your address");
                Main.scanner.nextLine();
                String address = Main.scanner.nextLine();

                System.out.println("Enter your skills :");
                String skills = Main.scanner.nextLine();
                System.out.println("Enter your office hours :");
                String hours = Main.scanner.nextLine();

                String sql = "INSERT INTO Professors (name, email, phone_number, address, password, skills,office_hours) VALUES (?, ?, ?, ?, ?, ?,?)";
                int rowsInserted = dbHelper.executeUpdate(sql, name, email, phn, address, password, skills,hours);

                if (rowsInserted > 0) {
                    System.out.println("Signed Up successfully! Waiting for admin to confirm");
                    System.out.println("----------------------------------");
                    System.exit(0);
                } else {
                    System.out.println("Sign Up failed!! Try again");
                    System.out.println("----------------------------------");
                }
            }
            dbHelper.closeResultConnection(resultSet);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("----------------------------------");
        }
    }

}
