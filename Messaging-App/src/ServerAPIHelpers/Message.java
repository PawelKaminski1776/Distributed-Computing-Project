package ServerAPIHelpers;

import java.sql.Timestamp;

public class Message {
    String content;
    Timestamp timestamp;

    public Message(String content) {
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return "Message: " + content + " | Timestamp: " + timestamp.toString();
    }
}
