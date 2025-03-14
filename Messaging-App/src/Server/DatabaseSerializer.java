package Server;

import java.util.Map;
import java.io.*;
import java.util.HashMap;

public class DatabaseSerializer {
    Map<String, String> userDatabase = new HashMap<>();

    public DatabaseSerializer(String filePath, Map<String, String> UserDatabase) {
        if (fileExists(filePath)) {
            this.userDatabase = loadJsonFromFile(filePath);
            this.userDatabase.putAll(UserDatabase);
        } else {
            this.userDatabase = new HashMap<>(UserDatabase);
        }

        saveJsonToFile(convertToJson(userDatabase), filePath);
    }

    public Map<String, String> getUserDatabase() {
        return this.userDatabase;
    }

    private static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // Method to convert Map to a JSON-like String
    private static String convertToJson(Map<String, String> map) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        int count = 0;
        int size = map.size();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonBuilder.append("  \"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\"");
            if (count < size - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
            count++;
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    // Method to save JSON string to a file
    private static void saveJsonToFile(String json, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(json);
            System.out.println("JSON saved to " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
        }
    }

    // Method to read JSON from a file and convert it back to a Map
    private static Map<String, String> loadJsonFromFile(String filePath) {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\"") && line.contains(":")) {
                    String[] parts = line.split(":");
                    String key = parts[0].trim().replace("\"", "");
                    String value = parts[1].trim().replace("\"", "").replace(",", "");
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }

        return map;
    }
}
