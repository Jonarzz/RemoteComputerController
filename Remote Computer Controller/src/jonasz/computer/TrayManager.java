package jonasz.computer;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TrayManager {
	
	private SystemTray systemTray;
	private Image image;
	private PopupMenu trayPopupMenu;
	private MenuItem actionExit;
	private TrayIcon trayIcon;
	
	public TrayManager() {
		systemTray = SystemTray.getSystemTray();
		try {
			image = ImageIO.read(getClass().getResource("/images/tray_off.png"));
		} catch (IOException e) {
			throw new RuntimeException();
		}
		trayPopupMenu = new PopupMenu();
		
		addActionExit();

	    trayIcon = new TrayIcon(image, "Remote Computer Controller", trayPopupMenu);
	    trayIcon.setImageAutoSize(true);
	    
	    try{
	        systemTray.add(trayIcon);
	    } catch(AWTException e){
	        System.exit(-1);
	    }
	}
	
	private void addActionExit() {
		actionExit = new MenuItem("Exit");
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
			trayIcon.setImage(ImageIO.read(getClass().getResource("/images/tray_on.png")));
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
