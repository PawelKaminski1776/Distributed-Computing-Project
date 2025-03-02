package ServerAPIHelpers;

import ClientAPIHelpers.DatagramMessage;
import ClientAPIHelpers.Message;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerAPI {
    public static MyServerDatagramSocket mySocket;
    public static DatagramMessage RequestDetails;

    public static void InitializeServer(int serverPort)
    {
        try {
            mySocket = new MyServerDatagramSocket(serverPort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SetRequestDetails(DatagramMessage requestDetails)
    {
        RequestDetails = requestDetails;
    }

    public static void main(String[] args) {
        int serverPort = 7;
        if (args.length == 1 )
            serverPort = Integer.parseInt(args[0]);
        try {
            InitializeServer(serverPort);
            System.out.println("Echo server ready.");
            while (true) {
                DatagramMessage messageReceived = GetMessageFromClient(mySocket);
                SetRequestDetails(messageReceived);

                String message = "";
                message = messageReceived.getMessage().replace(" ", "");
                // Account Creation
                if (message.equals("/CreateAccount"))
                {

                }
                // Login
                else if (message.equals("/Login")) {

                }
                // Logout
                else if (message.equals("/Logout"))
                {

                }
                // Chat
                else if (message.equals("/Chat"))
                {

                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace( );
        }
    }


    static private synchronized void SendMessageToClient(String Message)
    {
        try {
            mySocket.sendMessage(RequestDetails.getAddress( ),
                    RequestDetails.getPort( ), Message);
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

}
