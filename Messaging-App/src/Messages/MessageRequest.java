package Messages;

import java.io.Serializable;
import java.util.Date;

public class MessageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String message;
    private Date timestamp;

    // Constructor
    public MessageRequest(String username, String message) {
        this.username = username;
        this.message = message;
        this.timestamp = new Date();  // Set the current timestamp when the message is created
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
