package com.jonasz.remotecomputercontrollerclient.util;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class SignalSender {
	
	private DataOutputStream dos;
	private Logger logger;
	
	public SignalSender(DataOutputStream dos, Logger logger) {
		this.dos = dos;
		this.logger = logger;
	}
	
	public void leftButtonClicked() {
		try {
			dos.writeUTF("leftClicked");
		} catch (IOException e) {
			logger.warn("IOException - Couldn't send left click signal to server.");
		}
	}
	
	public void leftButtonReleased() {
		try {
			dos.writeUTF("leftReleased");
		} catch (IOException e) {
			logger.warn("IOException - Couldn't send left release signal to server.");
		}
	}

	public void rightButtonClicked() {
		try {
			dos.writeUTF("rightClicked");
		} catch (IOException e) {
			logger.warn("IOException - Couldn't send right click signal to server.");
		}
	}
	
	public void rightButtonReleased() {
		try {
			dos.writeUTF("rightReleased");
		} catch (IOException e) {
			logger.warn("IOException - Couldn't send right release signal to server.");
		}
	}
	
	public void sendString(String str) {
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			logger.warn("IOException - Couldn't send right release signal to server.");
		}
	}
}
