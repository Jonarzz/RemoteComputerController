package jonasz.robot;

import java.awt.*;
import java.awt.event.*;

import util.ServerUtilities;

public class RobotController {
	
	private static final int ROBOT_AUTO_DELAY = 10;
	private static final int LEFT_MOUSE_BUTTON = InputEvent.BUTTON1_MASK;
	private static final int RIGHT_MOUSE_BUTTON = InputEvent.BUTTON3_MASK;

	private Robot robot;
	
	private int currentXMousePosition, currentYMousePosition;
	
	public RobotController() throws AWTException {
		robot = new Robot();
		robot.setAutoDelay(ROBOT_AUTO_DELAY);
	}
	
	public void mouseMoveWithGivenDistance(Point distanceVector) {
		currentXMousePosition = MouseInfo.getPointerInfo().getLocation().x;
		currentYMousePosition = MouseInfo.getPointerInfo().getLocation().y;
		robot.mouseMove(currentXMousePosition + distanceVector.x, currentYMousePosition + distanceVector.y);
	}
	
	public void clickLowerOnKeyboard(char key) {
		int keyCode = ServerUtilities.getRobotKeycode(key);
		
		if (keyCode == ServerUtilities.EMPTY_CHAR)
			return;
		
		robot.keyPress(keyCode);
		robot.keyRelease(keyCode);
	}
	
	public void clickUpperOnKeyboard(char key) {
		int keyCode = ServerUtilities.getRobotKeycode(key);
		
		if (keyCode == ServerUtilities.EMPTY_CHAR)
			return;
		
		robot.keyPress(KeyEvent.VK_SHIFT);
		
		if (keyCode == ServerUtilities.QUESTION_MARK) {
			robot.keyPress(KeyEvent.VK_SLASH);
			robot.keyRelease(KeyEvent.VK_SLASH);
		}
		else {
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
		}
		
		robot.keyRelease(KeyEvent.VK_SHIFT);
	}
	
	public void clickBackspace() {
		robot.keyPress(KeyEvent.VK_BACK_SPACE);
		robot.keyRelease(KeyEvent.VK_BACK_SPACE);
	}
	
	public void clickEnter() {
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}
	
	public void scrollWithGivenDistance(int distance) {
		if (distance > 0)
			robot.mouseWheel(distance/10 + 1);
		else
			robot.mouseWheel(distance/10 - 1);
	}
	
	public void clickLeftMouseButton() {
		robot.mousePress(LEFT_MOUSE_BUTTON);
	}
	
	public void releaseLeftMouseButton() {
		robot.mouseRelease(LEFT_MOUSE_BUTTON);
	}
	
	public void clickRightMouseButton() {
		robot.mousePress(RIGHT_MOUSE_BUTTON);
	}
	
	public void releaseRightMouseButton() {
		robot.mouseRelease(RIGHT_MOUSE_BUTTON);
	}
}
