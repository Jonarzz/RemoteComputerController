package com.jonasz.remotecomputercontrollerclient.util;

public class Utilities {
	
	public static boolean isIPAddressValid(String IPAddress) {
		if (IPAddress == null)
			return false;
		
		for (char c : IPAddress.toCharArray()) 
			if (!Character.isDigit(c))
				if (c != '.')
					return false;

		return true;
	}

}
