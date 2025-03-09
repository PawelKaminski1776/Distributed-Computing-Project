package ServerAPIHelpers;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.*;

public class EchoServer3 {
   private static final int serverPort = 12345;

   public static void main(String[] args) {
      try {
         // Set up SSL server socket
         SSLContext sslContext = SSLContext.getInstance("TLS");
         KeyStore keyStore = KeyStore.getInstance("JKS");
         FileInputStream keyFile = new FileInputStream("D:/Distributed-Computing-Project/Messaging-App/src/ServerAPIHelpers/serverKeyStore.jks");
         keyStore.load(keyFile, "mtu12345".toCharArray());
         KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
         kmf.init(keyStore, "mtu12345".toCharArray());
         sslContext.init(kmf.getKeyManagers(), null, new java.security.SecureRandom());

         SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
         SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(serverPort);
         System.out.println("Server started, waiting for connections...");

         while (true) {
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            System.out.println("Client connected");

            new Thread(new EchoServerThread(clientSocket)).start();
         }
      } catch (IOException | NoSuchAlgorithmException | java.security.KeyStoreException | java.security.cert.CertificateException e) {
         e.printStackTrace();
      } catch (UnrecoverableKeyException e) {
          throw new RuntimeException(e);
      } catch (KeyManagementException e) {
          throw new RuntimeException(e);
      }
   }
}
