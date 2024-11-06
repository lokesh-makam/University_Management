import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrator extends Users {
    private static final String adminPassword = "Admin@1234";
    private static final String adminEmail = "admin123@gmail.com";
    private final String email=getEmail();
    private final String password=getPassword();
    private DatabaseHelper dbHelper;

    Administrator(String email, String password) {
        super(email, password);
        try {
            dbHelper = new DatabaseHelper();
            if (isValidUser()) {
                functionality();
            } else {
                System.out.println("Wrong Credentials try again");
                System.out.println("----------------------------------");
                System.exit(0);
            }
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

    public boolean isValidUser() {
        if (email.equalsIgnoreCase(adminEmail) && password.equals(adminPassword)) {
            System.out.println("Logged in Successfully");
            System.out.println("----------------------------------");
            return true;
        }
        return false;
    }

    public void functionality() {
        System.out.println("1-Manage Course");
        System.out.println("2-Manage Student Records");
        System.out.println("3-Assign Professor to Courses");
        System.out.println("4-Handle Complaints");
        System.out.println("5-Verify Students");
        System.out.println("6-Verify Professor");
        System.out.println("7-Log out");
        System.out.println("----------------------------------");
        int task = Main.scanner.nextInt();
        switch (task) {
            case 1:
                manageCourse();
                functionality();
                break;
            case 2:
                manageStudentRecords();
                functionality();
                break;
            case 3:
                assignProfessorsToCourses();
                functionality();
                break;
            case 4:
                handleComplaints();
                functionality();
                break;
            case 5:
                verify("student");
                functionality();
                break;
            case 6:
                verify("professor");
                functionality();
                break;
            case 7:
                logOut();
                break;
            default:
                System.out.println("Invalid input!! Try again");
                System.out.println("----------------------------------");
                functionality();
                break;
        }
    }

    public void manageCourse() {
        System.out.println("1-View Courses");
        System.out.println("2-Add Courses");
        System.out.println("3-Delete Courses");
        int task = Main.scanner.nextInt();
        switch (task) {
            case 1:
                viewCourse();
                break;
            case 2:
                addCourse();
                break;
            case 3:
                deleteCourse();
                break;
            default:
                break;
        }
    }

    public void viewCourse() {
        try {
            String query = "SELECT * FROM Courses";
            ResultSet resultSet = dbHelper.executeQuery(query);
            if (resultSet.next()) {
                do {
                    System.out.println("Course Id           : " + resultSet.getInt("course_id"));
                    System.out.println("Course Code         : " + resultSet.getString("course_code"));
                    System.out.println("Title               : " + resultSet.getString("title"));
                    System.out.println("Professor Id        : " + resultSet.getInt("professor_id"));
                    System.out.println("Credits             : " + resultSet.getInt("credits"));
                    System.out.println("Prerequisites       : " + resultSet.getString("prerequisites"));
                    System.out.println("Timings             : " + resultSet.getString("timings"));
                    System.out.println("Semester            : " + resultSet.getInt("semester"));
                    System.out.println("Location            : " + resultSet.getString("location"));
                    System.out.println("Syllabus            : " + resultSet.getString("syllabus"));
                    System.out.println("Course Limit        : " + resultSet.getInt("course_limit"));
                    System.out.println("Registered Students : " + resultSet.getInt("no_of_registration"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No courses available.");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    public void addCourse() {
        System.out.println("How many courses you want to add: ");
        int n = Main.scanner.nextInt();
        while (n-- > 0) {
            try {
                String query = "INSERT INTO Courses (course_code, title, " +
                        "credits, prerequisites, timings, semester, location, syllabus, course_limit)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                Main.scanner.nextLine();

                System.out.print("Enter course code: ");
                String courseCode = Main.scanner.nextLine();

                System.out.print("Enter title: ");
                String title = Main.scanner.nextLine();

                System.out.print("Enter credits: ");
                int credits = Main.scanner.nextInt();

                System.out.print("Enter prerequisites: ");
                Main.scanner.nextLine();
                String input = Main.scanner.nextLine().trim();
                String prerequisites = input.equalsIgnoreCase("null") || input.isEmpty() ? null : input;

                System.out.print("Enter timings: ");
                String timings = Main.scanner.nextLine();

                System.out.print("Enter semester: ");
                int semester = Main.scanner.nextInt();

                System.out.print("Enter location: ");
                Main.scanner.nextLine();
                String location = Main.scanner.nextLine();

                System.out.print("Enter syllabus: ");
                String syllabus = Main.scanner.nextLine();

                System.out.print("Enter limit: ");
                int course_limit = Main.scanner.nextInt();

                int rowsAffected = dbHelper.executeUpdate(query, courseCode, title, credits, prerequisites, timings, semester, location, syllabus, course_limit);

                if (rowsAffected > 0) {
                    System.out.println("Course inserted successfully.");
                    System.out.println("----------------------------------");
                } else {
                    System.out.println("Failed to insert course.");
                    System.out.println("----------------------------------");
                }

            } catch (SQLException e) {
                System.err.println("SQL error: " + e.getMessage());
            }
        }
    }

    public void deleteCourse() {
        System.out.println("Enter the course code to delete :");
        String courseCode = Main.scanner.next();

        try {
            String query = "DELETE FROM Courses WHERE course_code = ?";
            int result = dbHelper.executeUpdate(query, courseCode);
            if (result > 0) {
                System.out.println("Course deleted successfully!!");
                System.out.println("----------------------------------");
            } else {
                System.out.println("Deletion failed. Try again.");
                System.out.println("----------------------------------");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void manageStudentRecords() {
        System.out.println("1-View Students record");
        System.out.println("2-Update Students records");
        int task = Main.scanner.nextInt();
        if (task == 1) {
            viewStudentRecords();
        } else if (task == 2) {
            updateStudentsRecords();
        }
    }

    public void viewStudentRecords() {
        System.out.println("Enter the Student id :");
        int studentId = Main.scanner.nextInt();
        try {
            String query = "SELECT * FROM Students WHERE student_id = ?";
            ResultSet resultSet = dbHelper.executeQuery(query, studentId);
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
                System.out.println("No data found !!");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);

        } catch (SQLException e) {
            System.out.println("Error Occurred: " + e.getMessage());
        }
    }

    public void updateStudentsRecords() {
        System.out.println("Enter Student Id");
        int student_id = Main.scanner.nextInt();
        System.out.println("What you want to update (name, semester, phone number, address, grades)");
        String task = Main.scanner.next();

        try {
            if (task.equalsIgnoreCase("grades")) {
                // Handle updating grades
                String query = "SELECT * FROM Enrollments WHERE student_id = ? AND status = 'Registered'";
                ResultSet resultSet = dbHelper.executeQuery(query, student_id);
                int rowCount = 0;
                if (resultSet.next()) {
                    System.out.println("Courses student registered in:");
                    do {
                        rowCount++;
                        System.out.println("Course code: " + resultSet.getString("course_code"));
                    } while (resultSet.next());
                } else {
                    System.out.println("Invalid Student Id!! Try again");
                    System.out.println("----------------------------------");
                    return;
                }

                while (rowCount-- > 0) {
                    System.out.println("Enter the course code:");
                    String course_code = Main.scanner.next();
                    System.out.println("Enter the points received by the student:");
                    int points = Main.scanner.nextInt();

                    // Update the grade and set status to 'Completed' or 'Failed' based on points
                    if (points < 5) {
                        query = "UPDATE Enrollments SET point = ?, status = 'Failed' WHERE course_code = ? AND student_id = ?";
                    } else {
                        query = "UPDATE Enrollments SET point = ?, status = 'Completed' WHERE course_code = ? AND student_id = ?";
                    }

                    int result = dbHelper.executeUpdate(query, points, course_code, student_id);
                    if (result > 0) {
                        System.out.println("Updated successfully");
                        System.out.println("----------------------------------");
                    } else {
                        System.out.println("Update failed!! Try again");
                        System.out.println("----------------------------------");
                    }
                }

            } else {
            if (task.equalsIgnoreCase("semester")) {
                String query = "SELECT * FROM Enrollments WHERE student_id = ? AND status = 'Failed'";
                ResultSet resultSet = dbHelper.executeQuery(query, student_id);

                if (resultSet.next()) {
                    // Student has failed courses, block semester upgrade
                    System.out.println("Student has failed the course: " + resultSet.getString("course_code"));
                    System.out.println("Cannot upgrade semester until all failed courses are passed.");
                    System.out.println("----------------------------------");
                    dbHelper.closeResultConnection(resultSet);
                    return;
                }
            }
                System.out.println("Enter updated value:");
                Main.scanner.nextLine();
                String updatedValue = Main.scanner.nextLine();
                String query = "UPDATE Students SET " + task + " = ? WHERE student_id = ?";
                int result = dbHelper.executeUpdate(query, updatedValue, student_id);
                if (result > 0) {
                    System.out.println(task +" updated successfully!!");
                    System.out.println("----------------------------------");
                } else {
                    System.out.println("Update failed!! Try again.");
                    System.out.println("----------------------------------");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
            System.out.println("----------------------------------");
        }
    }


    public void assignProfessorsToCourses() {
        System.out.println("List of Professors :");
        String query="select * from Professors where verified='Yes'";
        try{
            ResultSet resultSet=dbHelper.executeQuery(query);
            if(resultSet.next()){
                do{
                    System.out.println("Name  :"+resultSet.getString("name"));
                    System.out.println("Id    :"+resultSet.getString("professor_id"));
                    System.out.println("Field :"+resultSet.getString("skills"));
                    System.out.println("----------------------------------");
                }while (resultSet.next());
            }else{
                System.out.println("No Professors found!!");
                return;
            }
            dbHelper.closeResultConnection(resultSet);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Enter Professor Id:");
        int id = Main.scanner.nextInt();
        System.out.println("How many Courses do you want to assign?");
        int n = Main.scanner.nextInt();

        while (n-- > 0) {
            try {
                System.out.println("Enter the course code:");
                String courseCode = Main.scanner.next();

                query = "UPDATE Courses " +
                        " SET professor_id = ? " +
                        " WHERE course_code = ? ";
                int result = dbHelper.executeUpdate(query, id, courseCode);

                if (result > 0) {
                    System.out.println("Course assigned successfully.");
                    System.out.println("----------------------------------");
                } else {
                    System.out.println("Course assignment failed. Please try again.");
                    System.out.println("----------------------------------");
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public void handleComplaints() {
        System.out.println("1-View all submitted complaints ");
        System.out.println("2-Update complaints ");
        int n = Main.scanner.nextInt();
        if (n == 1) {
           viewComplaints();
        } else {
            System.out.println("You can update the following Complaints :");
            viewComplaints();
            updateComplaints();
        }
    }

    public void  viewComplaints(){
        try {
            String query = "select * from Complaints";
            ResultSet resultSet = dbHelper.executeQuery(query);
            if (resultSet.next()) {
                do {
                    System.out.println("Complaint ID  : " + resultSet.getInt("complaint_id"));
                    System.out.println("Student ID    : " + resultSet.getInt("student_id"));
                    System.out.println("Description   : " + resultSet.getString("description"));
                    System.out.println("Status        : " + resultSet.getString("status"));
                    System.out.println("Resolution    : " + resultSet.getString("resolution"));
                    System.out.println("Created At    : " + resultSet.getString("created_at"));
                    System.out.println("----------------------------------");
                } while (resultSet.next());
            } else {
                System.out.println("No Complaints Found");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateComplaints(){
        System.out.println("Enter Complaint Id");
        int Complaint_id = Main.scanner.nextInt();
        System.out.println("Enter solution");
        Main.scanner.nextLine();
        String solution = Main.scanner.nextLine();
        try {
            String query = "UPDATE Complaints SET resolution = ? , status = 'Resolved' WHERE complaint_id = ?";
            int rowsUpdated =dbHelper.executeUpdate(query,solution,Complaint_id);

            if (rowsUpdated > 0) {
                System.out.println("Resolution updated successfully.");
                System.out.println("----------------------------------");
            } else {
                System.out.println("No complaint found with the given ID.");
                System.out.println("----------------------------------");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void verify(String role) {
        String str;
        if(role.equals("student")){
             str="select * from Students where status='No'";
        }else{
            str="select * from Professors where verified='No'";
        }

        try {
            ResultSet res=dbHelper.executeQuery(str);
            if(res.next()){
                System.out.println("The list of "+role+" who are not verified : ");
                do{
                    System.out.println("name : "+res.getString("name"));
                    System.out.println("ID   : "+res.getInt(role+"_id"));
                }while(res.next());
            }else
            {
                System.out.println("All are verified");
                return;
            }
            System.out.println("Enter "+role+" id:");
            int id = Main.scanner.nextInt();
            System.out.println("Do you want to verify (Yes or No)?");
            String response = Main.scanner.next();
            // Updating the  status
            if(response.equalsIgnoreCase("Yes")) {
                String updateQuery = "UPDATE Students SET status = 'Yes' WHERE student_id = ? and status ='No'";
                if(role.equals("professor")){
                    updateQuery = "UPDATE Professors SET verified = 'Yes' WHERE professor_id = ? and verified='No'";
                }
                int updateResult = dbHelper.executeUpdate(updateQuery, id);
                if (updateResult > 0) {

                    System.out.println(role+" verified");
                    System.out.println("----------------------------------");

                    // Retrieving student  or Professor details
                    String selectQuery = "SELECT * FROM Students WHERE student_id = ?";
                    if(role.equals("professor")){
                        selectQuery = "SELECT * FROM Professors WHERE professor_id = ?";
                    }
                    ResultSet resultSet = dbHelper.executeQuery(selectQuery, id);

                    if (resultSet.next()) {
                        String stdEmail = resultSet.getString("email");
                        String stdPassword = resultSet.getString("password");

                        // Inserting into Users table
                        String insertQuery = "INSERT INTO Users (email, password) VALUES (?, ?)";
                        int insertResult = dbHelper.executeUpdate(insertQuery, stdEmail, stdPassword);

                        if (insertResult > 0) {
                            System.out.println("User account created successfully.");
                        }
                    }
                } else {
                    System.out.println("No data found with the "+role+" id");
                    System.out.println("----------------------------------");
                }
            }else{
                System.out.println(role+" not verified");
                System.out.println("----------------------------------");
            }
            dbHelper.closeResultConnection(res);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}