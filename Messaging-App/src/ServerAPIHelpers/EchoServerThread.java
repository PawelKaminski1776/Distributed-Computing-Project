package ServerAPIHelpers;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.*;

public class EchoServerThread implements Runnable {
    private SSLSocket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    private static final HashMap<String, String> userDatabase = new HashMap<>(); // Stores username -> password
    private static final HashMap<String, PrintWriter> activeUsers = new HashMap<>(); // Stores active users' output streams
    private static final HashMap<String, List<String>> pendingMessages = new HashMap<>(); // Store messages for offline users

    public EchoServerThread(SSLSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(" ", 3);

                if (message.startsWith("LOGIN")) {
                    String username = parts[1];
                    String password = parts[2];

                    if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                        out.println("LOGIN_SUCCESS");
                        this.username = username;
                        activeUsers.put(username, out);

                        if (pendingMessages.containsKey(username)) {
                            for (String msg : pendingMessages.get(username)) {
                                out.println(msg);
                            }
                            pendingMessages.remove(username);
                        }
                    } else {
                        out.println("LOGIN_FAILED");
                    }
                } else if (message.startsWith("CREATE_ACCOUNT")) {
                    String username = parts[1];
                    String password = parts[2];

                    if (userDatabase.containsKey(username)) {
                        out.println("USER_EXISTS");
                    } else {
                        userDatabase.put(username, password);
                        out.println("ACCOUNT_CREATED");
                    }
                } else if (message.startsWith("SEND_MESSAGE")) {
                    String recipient = parts[1];
                    String msgContent = parts[2];

                    String formattedMessage = "Incoming message from: " + this.username + ": " + msgContent;

                    if (activeUsers.containsKey(recipient)) {
                        PrintWriter recipientOut = activeUsers.get(recipient);
                        recipientOut.println(formattedMessage);
                        out.println("Message sent to " + recipient);
                    } else {
                        pendingMessages.computeIfAbsent(recipient, k -> new ArrayList<>()).add(formattedMessage);
                        out.println("Recipient is offline. Message will be delivered when they log in.");
                    }
                } else if (message.equals("LOGOFF")) {
                    out.println("Goodbye!");
                    activeUsers.remove(this.username);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
