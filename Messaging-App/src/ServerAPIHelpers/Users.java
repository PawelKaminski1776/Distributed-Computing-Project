package ServerAPIHelpers;

import java.util.ArrayList;

public class Users {

    // Needs to be changed for security
    public static ArrayList<String> Users = new ArrayList();
    public static ArrayList<String> Passwords = new ArrayList();
    public static boolean LoggedIn = false;

    public static void setUsers(ArrayList<String> users) {
        Users = users;
    }

    public static void setPasswords(ArrayList<String> passwords) {
        Passwords = passwords;
    }

    public static void setLoggedIn(boolean loggedIn) {
        LoggedIn = loggedIn;
    }
}
