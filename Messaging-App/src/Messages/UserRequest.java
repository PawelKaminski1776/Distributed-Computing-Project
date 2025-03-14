package Messages;

import java.io.Serializable;

public class UserRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String action;

    public UserRequest(String username, String password, String action) {
        this.username = username;
        this.password = password;
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getAction() {
        return action;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
