import com.sun.nio.sctp.MessageInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This module contains the application logic of an echo server
 * which uses a connectionless datagram socket for interprocess
 * communication.
 * A command-line argument is required to specify the server port.
 * @author M. L. Liu
 */

public class ServerAPI {
    public static ArrayList<String> Users = new ArrayList();
    public static ArrayList<String> Passwords = new ArrayList();


    public static void main(String[] args) {
        int serverPort = 7;    // default port
        if (args.length == 1 )
            serverPort = Integer.parseInt(args[0]);
        try {
            // instantiates a datagram socket for both sending
            // and receiving data
            MyServerDatagramSocket mySocket = new MyServerDatagramSocket(serverPort);
            System.out.println("Echo server ready.");
            while (true) {  // forever loop
                DatagramMessage request =
                        mySocket.receiveMessageAndSender();
                System.out.println("Request received");
                String Message = "Enter 1, for User creation, 2 for Login, 3 for Exit";

                mySocket.sendMessage(request.getAddress( ),
                        request.getPort( ), Message);

                String messageReceived = GetMessageFromClient(mySocket).getMessage();

                messageReceived = messageReceived.replace(" ","");
                System.out.println(messageReceived);
                // Logon Option
                if(messageReceived.equals("1"))
                {
                    System.out.println("Started Account Creation");
                    Message = "Enter your username followed by a space and a password";
                    mySocket.sendMessage(request.getAddress( ),
                            request.getPort( ), Message);

                    messageReceived = GetMessageFromClient(mySocket).getMessage();

                    int index = messageReceived.indexOf(" ");

                    String user = "", password = "";
                    // User
                    for(int i = 0; i < index; i++){
                        user += (messageReceived.charAt(i));
                    }

                    // Password
                    for(int i = index; i < messageReceived.length( ); i++){
                        password += messageReceived.charAt(i);
                    }

                    Users.add(user);
                    Passwords.add(password);

                    SendMessageToClient(mySocket, request, "Account Created");
                }
                // Create Profile Option
                if(messageReceived.equals("2"))
                {

                }
                // Exit
                if(messageReceived.equals("3"))
                {
                    Message = "Exiting the Server...";
                    mySocket.sendMessage(request.getAddress( ),
                            request.getPort( ), Message);
                }

            } //end while
        } // end try
        catch (Exception ex) {
            ex.printStackTrace( );
        } // end catch
    } //end main


    static private synchronized void SendMessageToClient(MyServerDatagramSocket mySocket, DatagramMessage request, String Message)
    {
        try {
            mySocket.sendMessage(request.getAddress( ),
                    request.getPort( ), Message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static private synchronized DatagramMessage GetMessageFromClient(MyServerDatagramSocket mySocket)
    {
        try {
            DatagramMessage request =
                    mySocket.receiveMessageAndSender();
            return request;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static private synchronized void CreateAccount(String Username, String Password) {

    }

} // end class
