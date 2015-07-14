package com.jonasz.remotecomputercontrollerclient.util;

import android.view.KeyEvent;

public class ClientUtilities {
	
	public static char EMPTY_CHAR = '/';
	
	public static boolean isIPAddressValid(String IPAddress) {
		if (IPAddress == null)
			return false;
		
		for (char c : IPAddress.toCharArray()) 
			if (!Character.isDigit(c))
				if (c != '.')
					return false;

		return true;
	}
	
	public static char getKeyFromID(int actionId) {
		switch (actionId) {
			case KeyEvent.KEYCODE_SPACE:
				return ' ';
			case KeyEvent.KEYCODE_A:
				return 'A';
			case KeyEvent.KEYCODE_B:
				return 'B';
			case KeyEvent.KEYCODE_C:
				return 'C';
			case KeyEvent.KEYCODE_D:
				return 'D';
			case KeyEvent.KEYCODE_E:
				return 'E';
			case KeyEvent.KEYCODE_F:
				return 'F';
			case KeyEvent.KEYCODE_G:
				return 'G';
			case KeyEvent.KEYCODE_H:
				return 'H';
			case KeyEvent.KEYCODE_I:
				return 'I';
			case KeyEvent.KEYCODE_J:
				return 'J';
			case KeyEvent.KEYCODE_K:
				return 'K';
			case KeyEvent.KEYCODE_L:
				return 'L';
			case KeyEvent.KEYCODE_M:
				return 'M';
			case KeyEvent.KEYCODE_N:
				return 'N';
			case KeyEvent.KEYCODE_O:
				return 'O';
			case KeyEvent.KEYCODE_P:
				return 'P';
			case KeyEvent.KEYCODE_Q:
				return 'Q';
			case KeyEvent.KEYCODE_R:
				return 'R';
			case KeyEvent.KEYCODE_S:
				return 'S';
			case KeyEvent.KEYCODE_T:
				return 'T';
			case KeyEvent.KEYCODE_V:
				return 'V';
			case KeyEvent.KEYCODE_U:
				return 'U';
			case KeyEvent.KEYCODE_W:
				return 'W';
			case KeyEvent.KEYCODE_X:
				return 'X';
			case KeyEvent.KEYCODE_Y:
				return 'Y';
			case KeyEvent.KEYCODE_Z:
				return 'Z';
			case KeyEvent.KEYCODE_1:
				return '1';
			case KeyEvent.KEYCODE_2:
				return '2';
			case KeyEvent.KEYCODE_3:
				return '3';
			case KeyEvent.KEYCODE_4:
				return '4';
			case KeyEvent.KEYCODE_5:
				return '5';
			case KeyEvent.KEYCODE_6:
				return '6';
			case KeyEvent.KEYCODE_7:
				return '7';
			case KeyEvent.KEYCODE_8:
				return '8';
			case KeyEvent.KEYCODE_9:
				return '9';
			case KeyEvent.KEYCODE_0:
				return '0';
			case KeyEvent.KEYCODE_PERIOD:
				return '.';
			case KeyEvent.KEYCODE_COMMA:
				return ',';
			default:
				return EMPTY_CHAR;
		}
	}
}