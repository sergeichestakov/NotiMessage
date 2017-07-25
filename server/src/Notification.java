import java.awt.*;

public class Notification {
	
	//Displays a notification that was received by the phone
    public static void display(String[] msg, Image image) throws AWTException, java.net.MalformedURLException {  
    	String device = msg[0];
        String app = msg[1];
        String title = msg[2];
        String text = msg[3];
    	
        SystemTray tray = SystemTray.getSystemTray();

        TrayIcon trayIcon = new TrayIcon(image, device);
        
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        if(title == null || text.contains(title)) {
            trayIcon.displayMessage(app, text, TrayIcon.MessageType.NONE);
        } else {
            trayIcon.displayMessage(app, title + "\n" + text, TrayIcon.MessageType.NONE);
        }
        tray.remove(trayIcon);
    }
}