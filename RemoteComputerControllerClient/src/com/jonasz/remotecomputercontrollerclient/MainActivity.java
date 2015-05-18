package com.jonasz.remotecomputercontrollerclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.StrictMode;
// TODO scroll bar
// TODO lockowanie left clicka przez przytrzymanie (zmiana w opcjach)
// TODO klawiatura
// TODO (klawisze dodatkowe: print screen, wycisz, g³oœniej, ciszej)
public class MainActivity extends ActionBarActivity {
	
	private final String CONNECTION_SUCCESFUL_MESSAGE = "Connected succesfuly!";
	private final String UNKNOWN_HOST_MESSAGE = "Unknown host!";
	private final String IOEXCEPTION_MESSAGE = "Error encountered!";
	
	private String IPAddress;

	private Socket socket;
	private DataOutputStream dos;
	
	private Button leftButton;
	private Button rightButton;
	
	private boolean leftButtonHardClicked;
	
	private int x1, x2, y1, y2, xOnActionDown, yOnActionDown;
	
	private String coordinatesDifference;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		leftButton = (Button)findViewById(R.id.left_button);
		rightButton = (Button)findViewById(R.id.right_button);
		
		getIPAndStartTheApp();			
	}
	
	private void getIPAndStartTheApp() {		
		final EditText input = new EditText(this);
		input.setHint("IP address (from the desktop app)");
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		
		final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Type in your IP address")
			.setView(input)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {           
					IPAddress = input.getText().toString();
				}
			});		
		
		Dialog getIPDialog = builder.create();
		
		getIPDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {		
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

				if (!isIPAddressValid())
					Toast.makeText(getApplicationContext(), "Invalid IP address.", Toast.LENGTH_LONG).show();
				else
					successfullyStartApp();
			}
		});
		
		getIPDialog.setCanceledOnTouchOutside(false);
		getIPDialog.show();
	}
	
	private boolean isIPAddressValid() {
		if (IPAddress == null)
			return false;
		
		for (char c : IPAddress.toCharArray()) 
			if (!Character.isDigit(c))
				if (c != '.')
					return false;

		return true;
	}
	
	private void successfullyStartApp() {
		createSocketAndOutputStream();	
		
		leftButton.setOnTouchListener(new AddButtonTouchListener(leftButton));
		rightButton.setOnTouchListener(new AddButtonTouchListener(rightButton));
	}
	
	private void createSocketAndOutputStream() { 
		try {
			socket = new Socket(IPAddress, 8888);
			socket.setTcpNoDelay(true);
			
			dos = new DataOutputStream(socket.getOutputStream());
			
			Toast.makeText(this, CONNECTION_SUCCESFUL_MESSAGE, Toast.LENGTH_LONG).show();
		} catch (UnknownHostException e) {
			Toast.makeText(this, UNKNOWN_HOST_MESSAGE, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, IOEXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
		}		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if (wereSocketAndStreamCreated())
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!leftButtonHardClicked) 
						leftButtonRelease();
					
					x1 = (int)event.getX();
					y1 = (int)event.getY();
					
					xOnActionDown = x1;
					yOnActionDown = y1;
					
					break;
					
				case MotionEvent.ACTION_MOVE:
					x2 = (int)event.getX();
					y2 = (int)event.getY(); 
					
					coordinatesDifference = Integer.toString(x2 - x1) + "," + Integer.toString(y2 - y1);
					
					x1 = x2;
					y1 = y2;
					
					try {
						dos.writeUTF(coordinatesDifference);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					break;
					
				case MotionEvent.ACTION_UP:
					x2 = (int)event.getX();
					y2 = (int)event.getY(); 
					
					if (Math.abs(x2 - xOnActionDown) < 5 && Math.abs(y2 - yOnActionDown) < 5) {
						leftButtonClicked();
						leftButtonHardClicked = false;
					}
					break;
			}
		
		return super.onTouchEvent(event);
	}
	
	private class AddButtonTouchListener implements OnTouchListener {
		
		private Button button;
		
		public AddButtonTouchListener(Button button) {
			this.button = button;
		}
		
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	            	if (button.getId() == leftButton.getId())		
	    				leftButtonClicked();
	    			if (button.getId() == rightButton.getId())	
	    				rightButtonClicked();
	    			
	    			v.performClick();
	    			
	                return true;
	            case MotionEvent.ACTION_UP:
	            	if (button.getId() == leftButton.getId())		
	    				leftButtonRelease();
	    			if (button.getId() == rightButton.getId())	
	    				rightButtonRelease();
	                return true;
			}
			
			
			return false;
		}
	}
	
	private void leftButtonClicked() {
		try {
			leftButtonHardClicked = true;
			dos.writeUTF("leftClicked");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void leftButtonRelease() {
		try {
			leftButtonHardClicked = false;
			dos.writeUTF("leftReleased");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void rightButtonClicked() {
		try {
			dos.writeUTF("rightClicked");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void rightButtonRelease() {
		try {
			dos.writeUTF("rightReleased");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		closeSocketAndStream();
	}
	
	private void closeSocketAndStream() {
		if (!wereSocketAndStreamCreated())
			return;

		try {
			socket.close();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean wereSocketAndStreamCreated() {
		if (socket == null || dos == null) 
			return false;
		
		return true;
	}
}
