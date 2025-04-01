package Serializers;

import Messages.MessageRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageSerializer
{
    List<MessageRequest> Messages;

    public List<MessageRequest> getMessages() {
        return Messages;
    }

    public MessageSerializer(List<MessageRequest> messages, String filePath)
    {
        if(fileExists(filePath))
        {
            this.Messages = deserializeFromJsonFile(filePath);
            this.Messages.addAll(messages);
        }
        else
        {
            this.Messages = messages;
        }

        serializeToJsonFile(this.Messages, filePath);
    }

    private static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private static void serializeToJsonFile(List<MessageRequest> messages, String fileName) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < messages.size(); i++) {
            jsonBuilder.append(toJson(messages.get(i)));
            if (i < messages.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonBuilder.toString());
            System.out.println("JSON file created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<MessageRequest> deserializeFromJsonFile(String fileName) {
        List<MessageRequest> messages = new ArrayList<>();
        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = jsonContent.toString().trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1); // Remove [ and ]
        }

        String[] objectStrings = json.split("},\\{");
        for (String obj : objectStrings) {
            obj = obj.trim();
            if (!obj.startsWith("{")) obj = "{" + obj;
            if (!obj.endsWith("}")) obj = obj + "}";
            messages.add(fromJson(obj));
        }

        return messages;
    }


    private static String toJson(MessageRequest message) {
        return "{\"username\":\"" + message.getUsername() + "\", \"message\":\"" + message.getMessage() +
                "\", \"timestamp\":\"" + message.getTimestamp().getTime() + "\"}";
    }

    private static MessageRequest fromJson(String json) {
        json = json.trim().replace("{", "").replace("}", "");
        String[] parts = json.split(",");
        String username = "";
        String message = "";
        long timestamp = 0;

        for (String part : parts) {
            String[] keyValue = part.split(":");
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            if (key.equals("username")) {
                username = value;
            } else if (key.equals("message")) {
                message = value;
            } else if (key.equals("timestamp")) {
                timestamp = Long.parseLong(value);
            }
        }

        MessageRequest msgRequest = new MessageRequest(username, message);
        msgRequest.setTimestamp(new Date(timestamp));
        return msgRequest;
    }
}