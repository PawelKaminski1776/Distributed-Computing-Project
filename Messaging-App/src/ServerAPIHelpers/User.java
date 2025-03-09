package ServerAPIHelpers;

public class User {

    public static String Username;
    public static String Password;
    public static boolean LoggedIn = false;

    public User(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    public String getUsername() {
        return Username;
    }
    public static void setLoggedIn(boolean loggedIn) {
        LoggedIn = loggedIn;
    }
}
