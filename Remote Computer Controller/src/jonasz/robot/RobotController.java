package jonasz.robot;

import java.awt.*;
import java.awt.event.*;

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
