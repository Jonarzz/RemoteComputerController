package jonasz.robot.util;

public class ServerUtilities {
    public static final String QUESTION_MARK = "?";

    public static int getRobotKeycode(String keyAsString) {
        return (int) Character.toUpperCase(keyAsString.charAt(0));
    }

    public static boolean isKeyQuestionMark(final String keyAsString) {
        return QUESTION_MARK.equals(keyAsString);
    }
}
