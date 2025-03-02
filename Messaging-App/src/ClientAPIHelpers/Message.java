package ClientAPIHelpers;

import java.sql.Timestamp;

public class Message {
    String content;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
