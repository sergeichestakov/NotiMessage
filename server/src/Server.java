import java.net.*;
import javax.imageio.ImageIO;
import java.awt.Image;
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
            InputStream inputStream = server.getInputStream();
            
            ObjectInputStream objectInput = new ObjectInputStream(inputStream);
            DataInputStream dataInput = new DataInputStream(inputStream);
            
            String[] notification = (String []) objectInput.readObject();
                        
            int len = dataInput.readInt();
            byte[] data = new byte[len];
            if(len > 0) {
            	dataInput.readFully(data, 0, len);
            }
            InputStream byteStream = new ByteArrayInputStream(data);
            Image img = ImageIO.read(byteStream);
            Notification.display(notification, img);
            
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
      final int port = 6066;
      try {
         Thread t = new Server(port);
         t.start();
      }catch(IOException e) {
         e.printStackTrace();
      }
   }
}