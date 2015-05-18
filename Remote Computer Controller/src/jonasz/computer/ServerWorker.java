package jonasz.computer;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import jonasz.robot.RobotController;

public class ServerWorker implements Runnable {
	
	private Socket clientSocket;
	private DataInputStream dis;
	private RobotController rc;
	
	private String messageFromDevice;
	
	private boolean wasDataInputStreamCreated;
	
	public ServerWorker(Socket clientSocket, RobotController rc) {
		this.clientSocket = clientSocket;
		this.rc = rc;
			
		createDataInputStream();
	}
	
	private void createDataInputStream() {
		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			wasDataInputStreamCreated = true;
		} catch (IOException e) {
			wasDataInputStreamCreated = false;
		}
	}

	public void run() {
		Point distanceVector = new Point();
		
		while (wasDataInputStreamCreated) {
			try {				
				messageFromDevice = dis.readUTF();
				
				if (messageFromDevice.equals("leftClicked"))
					rc.clickLeftMouseButton();
				else if (messageFromDevice.equals("rightClicked"))
					rc.clickRightMouseButton();
				else if (messageFromDevice.equals("leftReleased"))
					rc.releaseLeftMouseButton();
				else if (messageFromDevice.equals("rightReleased"))
					rc.releaseRightMouseButton();
				else {
					getDistanceVectorFromInput(messageFromDevice, distanceVector);
					rc.mouseMoveWithGivenDistance(distanceVector);
				}
				
			} catch (IOException e) {
				break;
			}
		}
	}
	
	private void getDistanceVectorFromInput(String messageFromDevice, Point distanceVector) {
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
