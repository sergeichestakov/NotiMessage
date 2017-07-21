import java.net.*;
import java.io.*;

public class Server extends Thread {
   private ServerSocket serverSocket;
   
   public Server(int port) throws IOException {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(0);
   }

   public void run() {
	   	 
      while(true) {
         try {
            Socket server = serverSocket.accept();
            
            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            
            String[] notification = (String []) in.readObject();
            
            Notification.display(notification);
            
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF("Message Recieved");
            server.close();
            
         }catch(Exception e) {
            e.printStackTrace();
            break;
         }
      }
   }
   
   public static void main(String [] args) {
      int port = 6066;
      try {
         Thread t = new Server(port);
         t.start();
      }catch(IOException e) {
         e.printStackTrace();
      }
   }
}