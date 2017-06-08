package jonasz.computer;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TrayManager {

    private static final String TRAY_ON_IMG = "/images/tray_on.png";
    private static final String TRAY_OFF_IMG = "/images/tray_off.png";
    private static final String APP_NAME = "Remote Computer Controller";
    private static final String EXIT_TEXT = "Exit";

    private SystemTray systemTray;
	private Image image;
	private PopupMenu trayPopupMenu;
	private MenuItem actionExit;
	private TrayIcon trayIcon;

    public TrayManager() {
		systemTray = SystemTray.getSystemTray();
		try {
			image = ImageIO.read(getClass().getResource(TRAY_OFF_IMG));
		} catch (IOException e) {
			throw new RuntimeException();
		}
		trayPopupMenu = new PopupMenu();
		
		addActionExit();

	    trayIcon = new TrayIcon(image, APP_NAME, trayPopupMenu);
	    trayIcon.setImageAutoSize(true);
	    
	    try{
	        systemTray.add(trayIcon);
	    } catch(AWTException e){
	        System.exit(-1);
	    }
	}
	
	private void addActionExit() {
		actionExit = new MenuItem(EXIT_TEXT);
	    actionExit.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            System.exit(0);        
	        }
	    });     
	    trayPopupMenu.add(actionExit);
	}
	
	public void setTooltip(String str) {
		trayIcon.setToolTip(str);
	}
	
	public void setImageOn() {
		try {
            trayIcon.setImage(ImageIO.read(getClass().getResource(TRAY_ON_IMG)));
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void setImageOff() {
		try {
			trayIcon.setImage(ImageIO.read(getClass().getResource(TRAY_OFF_IMG)));
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
