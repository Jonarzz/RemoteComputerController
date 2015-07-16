package jonasz.robot;

import java.awt.AWTException;
import java.io.IOException;

import jonasz.computer.Server;
import jonasz.computer.ServerWorker;
import jonasz.computer.TrayManager;

public class RemoteComputerController {
	
	private static Server server;
	private static RobotController rc;
	private static ServerWorker serverWorker;
	private static TrayManager tm;
	
	public static void main(String[] args) {
		tm = new TrayManager();
		
		try {
			rc = new RobotController();
		} catch (AWTException e1) {
			System.exit(-1);
		}
		
		try {
			server = new Server(8888, tm);
		} catch (IOException e2) {
			tm.setTooltip("Could not get IP address.");
		}

		while (true) {
			try {
				serverWorker = new ServerWorker(server.getClientSocket(), rc, tm);
				Thread t = new Thread(serverWorker);
				t.start();
			} catch (IOException e) {
				server.closeSocket();
				System.exit(-1);
			}
		}
	}
	
}
