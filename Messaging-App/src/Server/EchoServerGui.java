package Server;

import Messages.DownloadRequest;
import Messages.UserRequest;
import Messages.MessageRequest;
import Serializers.DatabaseSerializer;
import Serializers.MessageSerializer;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EchoServerGui extends JFrame {
   private static final int SERVER_PORT = 7;
   private static Map<String, String> userDatabase = new HashMap<>(); // Stores usernames and passwords
   private static List<MessageRequest> messageHistory = new ArrayList<>(); // Stores messages from clients
   private static List<PrintWriter> clientOutputs = new ArrayList<>(); // List of PrintWriters to send messages to clients

   private static final String userDatabaseFilePath = "userDatabase.json";
   private static final String userMessagesFilePath = "Messages.json";

   private JTextArea logArea;
   private JButton startButton;
   private JButton stopButton;
   private ServerSocket serverSocket;
   private boolean isServerRunning = false;

   public EchoServerGui() {
      setTitle("Echo Server");
      setSize(500, 400);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);

      // Text area to display logs
      logArea = new JTextArea(15, 40);
      logArea.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(logArea);

      // Start and Stop buttons
      startButton = new JButton("Start Server");
      stopButton = new JButton("Stop Server");

      startButton.setEnabled(true);
      stopButton.setEnabled(false);

      JPanel buttonPanel = new JPanel();
      buttonPanel.add(startButton);
      buttonPanel.add(stopButton);

      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);

      startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            startServer();
         }
      });

      stopButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            stopServer();
         }
      });
   }

   private void startServer() {
      try {
         serverSocket = new ServerSocket(SERVER_PORT);
         isServerRunning = true;
         logArea.append("Server started on port " + SERVER_PORT + "\n");

         startButton.setEnabled(false);
         stopButton.setEnabled(true);

         // Accept client connections in a new thread
         new Thread(new ClientHandler()).start();

      } catch (IOException ex) {
         logArea.append("Error starting server: " + ex.getMessage() + "\n");
      }
   }

   private void stopServer() {
      try {
         if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
         }
         isServerRunning = false;
         logArea.append("Server stopped.\n");

         startButton.setEnabled(true);
         stopButton.setEnabled(false);

      } catch (IOException ex) {
         logArea.append("Error stopping server: " + ex.getMessage() + "\n");
      }
   }

   private class ClientHandler implements Runnable {
      @Override
      public void run() {
         try {
            while (isServerRunning) {
               Socket clientSocket = serverSocket.accept();
               logArea.append("Client Trying to Connect: " + clientSocket.getInetAddress() + "\n");

               // Handle the client's requests in a new thread
               new Thread(new ClientConnectionHandler(clientSocket)).start();
            }
         } catch (IOException ex) {
            logArea.append("Error accepting client connection: " + ex.getMessage() + "\n");
         }
      }
   }

   private class ClientConnectionHandler implements Runnable {
      private final Socket clientSocket;

      public ClientConnectionHandler(Socket clientSocket) {
         this.clientSocket = clientSocket;
      }

      @Override
      public void run() {
         try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
              PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Add this client to the list of clients
            synchronized (clientOutputs) {
               if(clientOutputs.contains(writer))
               {
                  clientOutputs.remove(writer);
               }
               clientOutputs.add(writer);
            }

            // Read the serialized UserRequest object
            Object request = in.readObject();

            if (request instanceof UserRequest) {

               DatabaseSerializer databaseSerializer = new DatabaseSerializer(userDatabaseFilePath, userDatabase);
               userDatabase = databaseSerializer.getUserDatabase();

               UserRequest userRequest = (UserRequest) request;

               String username = userRequest.getUsername();
               String password = userRequest.getPassword();

               if (userRequest.getAction().equals("LOGIN")) {
                  // Handle login
                  if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                     writer.println("Login successful.");
                     broatcastLogOnorLogOff(username, "LOGIN");
                  } else {
                     writer.println("Invalid username or password.");
                  }
               } else if (userRequest.getAction().equals("CREATE_ACCOUNT")) {
                  // Handle account creation
                  if (!userDatabase.containsKey(username)) {
                     userDatabase.put(username, password);
                     writer.println("Account created successfully.");
                  } else {
                     writer.println("Username already exists.");
                  }
               }
               else if (userRequest.getAction().equals("LOGOUT")) {
                  // Handle account creation
                  clientOutputs.remove(writer);
                  broatcastLogOnorLogOff(username, "LOGOUT");
               }

            }
            else if (request instanceof MessageRequest) {

               // Handle MessageRequest
               MessageRequest messageRequest = (MessageRequest) request;

               messageHistory.add(messageRequest);
               MessageSerializer messageSerializer = new MessageSerializer(messageHistory, userMessagesFilePath);
               // Add the message to the message history
               logArea.append("Received message: " + messageRequest.getMessage() + "\n");

               // Broadcast the message to all clients
               broadcastMessage(messageRequest);
            }

            else if (request instanceof DownloadRequest) {
               DownloadRequest downloadRequest = (DownloadRequest) request;

               if (downloadRequest.isOneMessage()) {
                  MessageSerializer messageSerializer = new MessageSerializer(messageHistory, userMessagesFilePath);
                  List<MessageRequest> messages = messageSerializer.getMessages();
                  for(MessageRequest message : messages)
                  {
                     System.out.print(message);
                  }
                  try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {
                     // Send the list as a serialized object
                     objectOutputStream.writeObject(messages);
                     objectOutputStream.flush();
                  } catch (IOException e) {
                     System.err.println("Error sending messages: " + e.getMessage());
                  }

               }

               else {
                  try (BufferedReader reader = new BufferedReader(new FileReader(userMessagesFilePath))) {
                     String line;
                     while ((line = reader.readLine()) != null) {
                        writer.println(line);
                     }
                     writer.flush();
                  } catch (IOException e) {
                     logArea.append("Error sending all messages: " + e.getMessage() + "\n");
                  }
               }
            }


         } catch (IOException | ClassNotFoundException ex) {
            logArea.append("Error handling client: " + ex.getMessage() + "\n");
         } finally {
            // Remove the client from the list of outputs when they disconnect
            synchronized (clientOutputs) {
               clientOutputs.remove(clientSocket);
            }
         }
      }
   }

   private void broatcastLogOnorLogOff(String username, String method)
   {
      if(method.equals("LOGIN")) {
         synchronized (clientOutputs) {
            // Send the new message to all connected clients
            for (PrintWriter clientOutput : clientOutputs) {
               clientOutput.println(username + " has joined the chat.");
            }
         }
      }
      else if(method.equals("LOGOUT"))
      {
         synchronized (clientOutputs) {
            // Send the new message to all connected clients
            for (PrintWriter clientOutput : clientOutputs) {
               clientOutput.println(username + " has left the chat.");
            }
         }
      }
   }

   private void broadcastMessage(MessageRequest messageRequest) {
      synchronized (clientOutputs) {
         // Send the new message to all connected clients
         for (PrintWriter clientOutput : clientOutputs) {
            clientOutput.println(messageRequest.getUsername() + " : " + messageRequest.getMessage());
         }
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         new EchoServerGui().setVisible(true);
      });
   }
}
