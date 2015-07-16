package jonasz.computer;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import jonasz.robot.RobotController;

public class ServerWorker implements Runnable {
	
	private Socket clientSocket;
	private DataInputStream dis;
	private RobotController rc;
	private TrayManager tm;
	
	private String messageFromDevice;
	private Point distanceVector;
	
	private boolean wasDataInputStreamCreated;
	
	public ServerWorker(Socket clientSocket, RobotController rc, TrayManager tm) {
		this.clientSocket = clientSocket;
		this.rc = rc;
		this.tm = tm;
			
		createDataInputStream();
	}
	
	private void createDataInputStream() {
		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			wasDataInputStreamCreated = true;
			tm.setImageOn();
		} catch (IOException e) {
			wasDataInputStreamCreated = false;
		}
	}

	public void run() {
		while (wasDataInputStreamCreated) {
			try {				
				messageFromDevice = dis.readUTF();
				
				if ("leftClicked".equals(messageFromDevice))
					rc.clickLeftMouseButton();
				else if ("rightClicked".equals(messageFromDevice))
					rc.clickRightMouseButton();
				else if ("leftReleased".equals(messageFromDevice))
					rc.releaseLeftMouseButton();
				else if ("rightReleased".equals(messageFromDevice))
					rc.releaseRightMouseButton();
				else if (messageFromDevice.charAt(0) == 'M') {
					getDistanceVectorFromInput(messageFromDevice.substring(1));
					rc.mouseMoveWithGivenDistance(distanceVector);
				}
				else if (messageFromDevice.charAt(0) == 'S')
					rc.scrollWithGivenDistance(Integer.parseInt(messageFromDevice.substring(1)));
				else if (messageFromDevice.charAt(0) == '.')
					rc.clickLowerOnKeyboard(messageFromDevice.charAt(1));
				else if (messageFromDevice.charAt(0) == '!')
					rc.clickUpperOnKeyboard(messageFromDevice.charAt(1));
				else if (messageFromDevice.charAt(0) == '<')
					rc.clickBackspace();
				else if (messageFromDevice.charAt(0) == '>')
					rc.clickEnter();
				
			} catch (IOException e) {
				break;
			}
		}
	}
	
	private void getDistanceVectorFromInput(String messageFromDevice) {
		distanceVector = new Point();
		
		String temp = "";
		int i = 0;
		
		while (messageFromDevice.charAt(i) != ',')
			temp += messageFromDevice.charAt(i++);
		
		distanceVector.x = Integer.parseInt(temp);
		
		i++;
		temp = "";
		
		while (i < messageFromDevice.length())
			temp += messageFromDevice.charAt(i++);
		
		distanceVector.y = Integer.parseInt(temp);
	}
}
