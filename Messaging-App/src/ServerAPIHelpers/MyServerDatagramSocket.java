package ServerAPIHelpers;

import ClientAPIHelpers.DatagramMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * A subclass of DatagramSocket which contains 
 * methods for sending and receiving messages
 * @author M. L. Liu
 */

public class MyServerDatagramSocket extends DatagramSocket {
static final int MAX_LEN = 100; 
   MyServerDatagramSocket(int portNo) throws SocketException{
     super(portNo);
   }
   public void sendMessage(InetAddress receiverHost,
                           int receiverPort,
                           String message)
   		          throws IOException {	
         byte[ ] sendBuffer = message.getBytes( );                                     
         DatagramPacket datagram = 
            new DatagramPacket(sendBuffer, sendBuffer.length, 
                                  receiverHost, receiverPort);
         this.send(datagram);
   } // end sendMessage

   public String receiveMessage( )
		throws IOException {		
         byte[ ] receiveBuffer = new byte[MAX_LEN];
         DatagramPacket datagram =
            new DatagramPacket(receiveBuffer, MAX_LEN);
         this.receive(datagram);
         String message = new String(receiveBuffer);
         return message;
   } //end receiveMessage

   public DatagramMessage receiveMessageAndSender( )
		throws IOException {		
         byte[ ] receiveBuffer = new byte[MAX_LEN];
         DatagramPacket datagram =
            new DatagramPacket(receiveBuffer, MAX_LEN);
         this.receive(datagram);
         // create a ClientAPIHelpers.DatagramMessage object, to contain message
         //   received and sender's address
         DatagramMessage returnVal = new DatagramMessage( );
         returnVal.putVal(new String(receiveBuffer),
                          datagram.getAddress( ),
                          datagram.getPort( ));
         return returnVal;
   } //end receiveMessage
} //end class
