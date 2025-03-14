package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Messages.MessageRequest;

public class MessageSerializer {
    List<MessageRequest> messages = new ArrayList<>();

    public MessageSerializer(List<MessageRequest> Messages, String filePath) {
        // If file exists, deserialize messages and add them to the list
        if (fileExists(filePath)) {
            this.messages.addAll(deserializeMessages(filePath));
        }

        // Add new messages to the list
        this.messages.addAll(Messages);

        // Serialize the updated list of messages
        serializeMessages(messages, filePath);
    }

    public List<MessageRequest> getMessages() {
        return messages;
    }

    private static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    // Serialize the list of messages to a file
    public static void serializeMessages(List<MessageRequest> messages, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(messages);
            System.out.println("Messages serialized successfully.");
        } catch (IOException e) {
            System.out.println("Error serializing messages: " + e.getMessage());
        }
    }

    // Deserialize a list of MessageRequest objects from a file
    @SuppressWarnings("unchecked")
    public static List<MessageRequest> deserializeMessages(String filePath) {
        List<MessageRequest> messages = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No previous messages found.");
            return messages;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            messages = (List<MessageRequest>) ois.readObject();
            System.out.println("Messages deserialized successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error deserializing messages: " + e.getMessage());
        }
        return messages;
    }
}
