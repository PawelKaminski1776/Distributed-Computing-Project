package Client;

import Messages.MessageRequest;
import Messages.UserRequest;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EchoClientGui extends JFrame {
   private JTextField usernameField;
   private JPasswordField passwordField;
   private JTextArea messageArea;
   private JButton loginButton;
   private JButton createAccountButton;
   private JButton sendButton;
   private JTextArea echoArea;
   private String Username;
   private Socket clientSocket;
   private PrintWriter writer;
   private BufferedReader reader;

   // Panels for each screen
   private JPanel mainPanel;
   private JPanel loginPanel;
   private JPanel createAccountPanel;
   private JPanel messagePanel;

   public EchoClientGui() {
      setTitle("Echo Client");
      setSize(400, 600);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);

      // Initialize panels
      mainPanel = new JPanel();
      loginPanel = new JPanel();
      createAccountPanel = new JPanel();
      messagePanel = new JPanel();

      // Initialize components for the main panel (initial screen with buttons)
      JButton loginButton = new JButton("Login");
      JButton createAccountButton = new JButton("Create Account");

      loginButton.addActionListener(e -> showLoginPanel());
      createAccountButton.addActionListener(e -> showCreateAccountPanel());

      mainPanel.add(loginButton);
      mainPanel.add(createAccountButton);

      // Setup the frame layout initially with the main panel
      setLayout(new BorderLayout());
      add(mainPanel, BorderLayout.CENTER);
      setVisible(true);
   }

   // Show login panel
   private void showLoginPanel() {
      clearFrame();
      clearPanel(loginPanel);
      loginPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 30));
      // Create login panel components

      // Label 1
      loginPanel.add(new JLabel("Username:"));

      // Textfield 1
      usernameField = new JTextField(20);  // Optimized text field size
      usernameField.setPreferredSize(new Dimension(300, 30));  // Set preferred size
      loginPanel.add(usernameField);

      loginPanel.add(new JLabel("Password:"));
      passwordField = new JPasswordField(20);  // Optimized password field size
      passwordField.setPreferredSize(new Dimension(300, 30));  // Set preferred size
      loginPanel.add(passwordField);

      loginButton = new JButton("Login");
      loginButton.setPreferredSize(new Dimension(120, 30));  // Set preferred button size
      loginButton.addActionListener(e -> login());
      loginPanel.add(loginButton);

      JButton backButton = new JButton("Back");
      backButton.setPreferredSize(new Dimension(120, 30));  // Set preferred button size
      backButton.addActionListener(e -> showMainPanel());
      loginPanel.add(backButton);

      echoArea = new JTextArea();
      echoArea.setPreferredSize(new Dimension(300, 100));
      loginPanel.add(echoArea);

      add(loginPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
   }

   // Show create account panel
   private void showCreateAccountPanel() {
      clearFrame();
      clearPanel(createAccountPanel);
      createAccountPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 30));

      // Create account panel components
      createAccountPanel.add(new JLabel("Username:"));
      usernameField = new JTextField(20);
      usernameField.setPreferredSize(new Dimension(200, 30));  // Optimized text field size
      createAccountPanel.add(usernameField);

      createAccountPanel.add(new JLabel("Password:"));
      passwordField = new JPasswordField(20);
      passwordField.setPreferredSize(new Dimension(200, 30));  // Optimized password field size
      createAccountPanel.add(passwordField);

      createAccountButton = new JButton("Create Account");
      createAccountButton.setPreferredSize(new Dimension(120, 30));  // Set preferred button size
      createAccountButton.addActionListener(e -> createAccount());
      createAccountPanel.add(createAccountButton);

      JButton backButton = new JButton("Back");
      backButton.setPreferredSize(new Dimension(120, 30));  // Set preferred button size
      backButton.addActionListener(e -> showMainPanel());
      createAccountPanel.add(backButton);

      echoArea = new JTextArea();
      echoArea.setPreferredSize(new Dimension(300, 100));
      createAccountPanel.add(echoArea);

      add(createAccountPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
   }

   // Show message panel after successful login
   private void showMessagePanel() {
      clearFrame();
      clearPanel(messagePanel);
      messagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 30));

      messageArea = new JTextArea();
      messageArea.setPreferredSize(new Dimension(300, 100));
      messageArea.setLineWrap(true);
      messagePanel.add(new JScrollPane(messageArea), BorderLayout.SOUTH);

      sendButton = new JButton("Send Message");
      sendButton.setPreferredSize(new Dimension(120, 30));  // Set preferred button size
      sendButton.addActionListener(e -> sendMessage());
      messagePanel.add(sendButton, BorderLayout.SOUTH);

      JButton backButton = new JButton("Back");
      backButton.setPreferredSize(new Dimension(120, 30));  // Set preferred button size
      backButton.addActionListener(e -> showMainPanel());
      messagePanel.add(backButton);

      // Create message panel components
      echoArea = new JTextArea();
      echoArea.setPreferredSize(new Dimension(300, 300));
      echoArea.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(echoArea);
      messagePanel.add(scrollPane, BorderLayout.CENTER);

      add(messagePanel, BorderLayout.CENTER);
      revalidate();
      repaint();
   }

   // Clear current content of the frame
   private void clearFrame() {
      getContentPane().removeAll();
      revalidate();
      repaint();
   }

   private void clearPanel(JPanel panel) {
      panel.removeAll();
      panel.revalidate();
      panel.repaint();
   }

   // Show the initial screen with buttons
   private void showMainPanel() {
      clearFrame();
      add(mainPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
   }

   // Login logic
   private void login() {
      String username = usernameField.getText();
      String password = new String(passwordField.getPassword());
      if (username.isEmpty() || password.isEmpty()) {
         echoArea.append("Please enter both username and password.\n");
         return;
      }

      try(Socket clientSocket = new Socket("localhost", 7);
         ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
         BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

         // Creating a Login Request Object that implements serialization
         UserRequest loginRequest = new UserRequest(username, password, "LOGIN");

         // Sending Json Over to Server
         out.writeObject(loginRequest);
         // Read the server's response
         String serverResponse = reader.readLine();
         System.out.println("Server Response: " + serverResponse);


         if(serverResponse.equals("Login successful.")) {
            // Show message panel after successful login
            this.Username = username;
            showMessagePanel();
         }
         else {
            echoArea.append(serverResponse + "\n");
         }

      } catch (IOException ex) {
         echoArea.append("Error connecting to server: " + ex.getMessage() + "\n");
      }
   }

   // Create account logic
   private void createAccount() {
      String username = usernameField.getText();
      String password = new String(passwordField.getPassword());
      if (username.isEmpty() || password.isEmpty()) {
         echoArea.append("Please enter both username and password.\n");
         return;
      }

      try(Socket clientSocket = new Socket("localhost", 7);
          ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

         // Creating a create Account Request Object that implements serialization
         UserRequest createAccountRequest = new UserRequest(username, password, "CREATE_ACCOUNT");

         // Sending Json Over to Server
         out.writeObject(createAccountRequest);

         // Read the server's response
         String serverResponse = reader.readLine();
         System.out.println("Server Response: " + serverResponse);

         echoArea.append(serverResponse + "\n");


      } catch (IOException ex) {
         echoArea.append("Error connecting to server: " + ex.getMessage() + "\n");
      }
   }

   // Send message to server
   private void sendMessage() {
      String message = messageArea.getText().trim();
      if (message.isEmpty()) {
         echoArea.append("Please enter a message.\n");
         return;
      }

      try(Socket clientSocket = new Socket("localhost", 7);
          ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

         MessageRequest messageRequest = new MessageRequest(this.Username, message);

         // Sending Json Over to Server
         out.writeObject(messageRequest);

         // Read the server's response
         String serverResponse = reader.readLine();
         System.out.println("Server Response: " + serverResponse);

         echoArea.append(serverResponse + "\n");

         messageArea.setText("");


      } catch (IOException ex) {
         echoArea.append("Error connecting to server: " + ex.getMessage() + "\n");
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> new EchoClientGui());
   }
}
