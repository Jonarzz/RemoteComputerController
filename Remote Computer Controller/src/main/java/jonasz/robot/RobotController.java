package jonasz.robot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import jonasz.robot.util.ServerUtilities;

public class RobotController {

	private static final int ROBOT_AUTO_DELAY = 10;
	private static final int LEFT_MOUSE_BUTTON = InputEvent.BUTTON1_MASK;
	private static final int RIGHT_MOUSE_BUTTON = InputEvent.BUTTON3_MASK;
    private static final int SCROLL_DIVIDER = 10;
    private static final String IGNORED_STRING = "/";

    private Robot robot;
    private int currentXMousePosition, currentYMousePosition;

    public RobotController() throws AWTException {
		robot = new Robot();
		robot.setAutoDelay(ROBOT_AUTO_DELAY);
	}

	/**
	 * @param distanceAsString Distance as string represented by two values separated with a comma, e.g. "7,42".
	 */
	public void mouseMoveWithDistanceGivenAsString(String distanceAsString) {
		final String[] xyValues = distanceAsString.split(",");
		Point distanceVector = new Point(Integer.parseInt(xyValues[0]), Integer.parseInt(xyValues[1]));

		currentXMousePosition = MouseInfo.getPointerInfo().getLocation().x;
		currentYMousePosition = MouseInfo.getPointerInfo().getLocation().y;

		robot.mouseMove(currentXMousePosition + distanceVector.x, currentYMousePosition + distanceVector.y);
	}
	
	public void clickLowerOnKeyboard(String keyAsString) {
		int keyCode = ServerUtilities.getRobotKeycode(keyAsString);
        robot.keyPress(keyCode);
		robot.keyRelease(keyCode);
	}

	public void clickUpperOnKeyboard(String keyAsString) {
        if (IGNORED_STRING.equals(keyAsString)) {
            return; // clause needed because of a change in Android API
        }
		int keyCode = ServerUtilities.getRobotKeycode(keyAsString);

		robot.keyPress(KeyEvent.VK_SHIFT);
        if (ServerUtilities.isKeyQuestionMark(keyAsString)) {
            robot.keyPress(KeyEvent.VK_SLASH);
            robot.keyRelease(KeyEvent.VK_SLASH);
        } else {
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
	
	public void scrollWithGivenDistance(String distanceAsString) {
        final int distance = Integer.parseInt(distanceAsString);
        final int distanceToScroll = distance / SCROLL_DIVIDER;
        robot.mouseWheel(distance > 0 ? distanceToScroll + 1 : distanceToScroll - 1);
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
