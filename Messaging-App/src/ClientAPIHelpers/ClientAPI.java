package ClientAPIHelpers;

import ServerAPIHelpers.Users;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientAPI {
    static final String endMessage = ".";
    static final String UserID = "None";

    public static void main(String[] args) {
        try

        {

            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);

            System.out.println("Welcome to the Chatting client.\n" +
                    "What is the name of the server host?");
            String hostName = br.readLine();

            if (hostName.length() == 0)
                hostName = "localhost";
            System.out.println("What is the port number of the server host?");
            String portNum = br.readLine();
            if (portNum.length() == 0)
                portNum = "7";
            boolean done = false;
            while (!done) {
                if (UserID.equals("None")) {
                    System.out.println("Please enter a user ID");
                    String message = br.readLine();
                }
                System.out.println("Enter 1 to Send Message, 2 to Exit");
                String message = br.readLine();

                if ((message.trim()).equals(endMessage)) {
                    done = true;
                    helper.done();
                } else {
                    // Serialize a Message object into JSON
                    if (message.equals("1")) {
                        // Create the Message object
                        System.out.println("Enter your username:");
                        String user = br.readLine();
                        System.out.println("Enter your message:");
                        String content = br.readLine();

                        Message msg = new Message(user, content);

                        // Convert the Message object into a JSON string
                        JSONObject jsonMessage = new JSONObject();
                        jsonMessage.put("user", msg.getUser());
                        jsonMessage.put("content", msg.getContent());

                        // Send the JSON string to the server
                        String serverResponse = helper.getEcho(jsonMessage.toString());
                        System.out.println("Server response: " + serverResponse);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String setUserID(String userID) {
        UserID = userID;
    }
}