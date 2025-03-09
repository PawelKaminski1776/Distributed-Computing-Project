package ClientAPIHelpers;

import java.io.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.*;

public class EchoClient2 {
   static final String endMessage = ".";
   static final String serverHost = "localhost";
   static final int serverPort = 12345;

   public static void main(String[] args) {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      try {
         SSLContext sslContext = SSLContext.getInstance("TLS");
         sslContext.init(null, null, new java.security.SecureRandom());
         SSLSocketFactory factory = sslContext.getSocketFactory();
         SSLSocket socket = (SSLSocket) factory.createSocket(serverHost, serverPort);

         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

         System.out.println("Welcome to the Echo client.");
         System.out.println("Do you want to log in or create an account? (login/create)");
         String action = br.readLine();

         if (action.equals("create")) {
            System.out.println("Choose a username:");
            String username = br.readLine();
            System.out.println("Choose a password:");
            String password = br.readLine();
            out.println("CREATE_ACCOUNT " + username + " " + password);

            String response = in.readLine();
            if (response.equals("ACCOUNT_CREATED")) {
               System.out.println("Account created successfully.");
            } else if (response.equals("USER_EXISTS")) {
               System.out.println("User already exists.");
               return;
            }
         } else if (action.equals("login")) {
            System.out.println("What is your username?");
            String username = br.readLine();
            System.out.println("What is your password?");
            String password = br.readLine();

            out.println("LOGIN " + username + " " + password);
            String response = in.readLine();
            if (response.equals("LOGIN_SUCCESS")) {
               System.out.println("Logged in successfully.");
            } else {
               System.out.println("Login failed.");
               return;
            }
         }

         new Thread(() -> {
            try {
               String incomingMessage;
               while ((incomingMessage = in.readLine()) != null) {
                  if (incomingMessage.startsWith("📩 MESSAGE_FROM")) {
                     System.out.println("\n" + incomingMessage);
                  }
               }
            } catch (IOException e) {
               System.out.println("Connection closed.");
            }
         }).start();

         boolean done = false;
         while (!done) {
            System.out.print("Enter recipient username (or '.' to log off): ");
            String recipient = br.readLine();

            if (recipient.trim().equals(endMessage)) {
               done = true;
               out.println("LOGOFF");
            } else {
               System.out.print("Enter message: ");
               String msgContent = br.readLine();
               out.println("SEND_MESSAGE " + recipient + " " + msgContent);
            }
         }
         socket.close();
      } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
         e.printStackTrace();
      }
   }
}
