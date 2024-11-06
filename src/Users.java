abstract public class Users {
    private final String  email;
    private final String password;
    abstract public boolean isValidUser();
    abstract void functionality();

    Users(String email, String password) {
        this.email = email;
        this.password = password;
    }

    String getEmail() {
        return email;
    }
    String getPassword() {
        return password;
    }

    public void logOut() {
        System.out.println("All changes saved");
        System.out.println("Successfully loged out");
    }
}
