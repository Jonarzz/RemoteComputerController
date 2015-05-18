package jonasz.robot;

import java.awt.AWTException;
import java.io.IOException;

import jonasz.computer.Server;
import jonasz.computer.ServerWorker;

public class Test {
	public static void main(String[] args) {
		Server server = null;
		RobotController rc = null;
		ServerWorker serverWorker =  null;

		try {
			rc = new RobotController();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		try {
			server = new Server(8888);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				serverWorker = new ServerWorker(server.getClientSocket(), rc);
				Thread t = new Thread(serverWorker);
				t.start();
			} catch (IOException e) {
				server.closeSocket();
				System.exit(-1);
			}
		}
	}
}
