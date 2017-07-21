import java.awt.*;

public class Notification {
	
	//Displays a notification that was received by the phone
    public static void display(String[] msg) throws AWTException, java.net.MalformedURLException {
    
    	String device = msg[0];
        String app = msg[1];
        String title = msg[2];
        String text = msg[3];
    	
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        if(title == null || text.contains(title)) {
            trayIcon.displayMessage(app, text, TrayIcon.MessageType.NONE);
        } else {
            trayIcon.displayMessage(app, title + "\n" + text, TrayIcon.MessageType.NONE);
        }
        
    }
}