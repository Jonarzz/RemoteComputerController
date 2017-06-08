package jonasz.computer;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import jonasz.robot.RobotController;

public class ServerWorker implements Runnable {

    private static final Map<String, Method> STRING_MESSAGE_TO_METHOD;
    private static final Map<Character, Method> CHAR_MESSAGE_TO_METHOD;
    private static final Map<Character, Method> CHAR_MESSAGE_TO_METHOD_WITH_ARG;

    static {
        final Class<RobotController> robotControllerClass = RobotController.class;

        STRING_MESSAGE_TO_METHOD = new HashMap<String, Method>();
        CHAR_MESSAGE_TO_METHOD = new HashMap<Character, Method>();
        CHAR_MESSAGE_TO_METHOD_WITH_ARG = new HashMap<Character, Method>();

        try {
            STRING_MESSAGE_TO_METHOD.put("leftClicked", robotControllerClass.getDeclaredMethod("clickLeftMouseButton"));
            STRING_MESSAGE_TO_METHOD.put("rightClicked", robotControllerClass.getDeclaredMethod("clickRightMouseButton"));
            STRING_MESSAGE_TO_METHOD.put("leftReleased", robotControllerClass.getDeclaredMethod("releaseLeftMouseButton"));
            STRING_MESSAGE_TO_METHOD.put("rightReleased", robotControllerClass.getDeclaredMethod("releaseRightMouseButton"));

            CHAR_MESSAGE_TO_METHOD.put('<', robotControllerClass.getDeclaredMethod("clickBackspace"));
            CHAR_MESSAGE_TO_METHOD.put('>', robotControllerClass.getDeclaredMethod("clickEnter"));

            CHAR_MESSAGE_TO_METHOD_WITH_ARG.put('M', robotControllerClass.getDeclaredMethod("mouseMoveWithDistanceGivenAsString", String.class));
            CHAR_MESSAGE_TO_METHOD_WITH_ARG.put('S', robotControllerClass.getDeclaredMethod("scrollWithGivenDistance", String.class));
            CHAR_MESSAGE_TO_METHOD_WITH_ARG.put('.', robotControllerClass.getDeclaredMethod("clickLowerOnKeyboard", String.class));
            CHAR_MESSAGE_TO_METHOD_WITH_ARG.put('!', robotControllerClass.getDeclaredMethod("clickUpperOnKeyboard", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }
	
	private Socket clientSocket;
	private DataInputStream dataInputStream;
	private RobotController robotController;
	private TrayManager trayManager;
	private boolean wasDataInputStreamCreated;
	
	public ServerWorker(Socket clientSocket, RobotController robotController, TrayManager trayManager) {
		this.clientSocket = clientSocket;
		this.robotController = robotController;
		this.trayManager = trayManager;
			
		createDataInputStream();
	}
	
	private void createDataInputStream() {
		try {
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			wasDataInputStreamCreated = true;
			trayManager.setImageOn();
		} catch (IOException e) {
			wasDataInputStreamCreated = false;
			trayManager.setImageOff();
		}
	}

	public void run() {
		while (wasDataInputStreamCreated) {
			try {
				final String messageFromDevice = dataInputStream.readUTF();

				if (STRING_MESSAGE_TO_METHOD.containsKey(messageFromDevice)) {
                    STRING_MESSAGE_TO_METHOD.get(messageFromDevice).invoke(robotController);
                } else {
                    final char charKey = messageFromDevice.charAt(0);

                    if (CHAR_MESSAGE_TO_METHOD.containsKey(charKey)) {
                        CHAR_MESSAGE_TO_METHOD.get(charKey).invoke(robotController);
                    } else if (CHAR_MESSAGE_TO_METHOD_WITH_ARG.containsKey(charKey)) {
                        CHAR_MESSAGE_TO_METHOD_WITH_ARG.get(charKey).invoke(robotController, messageFromDevice.substring(1));
                    }
                }
            } catch (IOException e) {
				break;
			} catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
	}
}
