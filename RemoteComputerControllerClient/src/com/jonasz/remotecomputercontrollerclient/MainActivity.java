package com.jonasz.remotecomputercontrollerclient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jonasz.remotecomputercontrollerclient.util.SignalSender;
import com.jonasz.remotecomputercontrollerclient.util.Utilities;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;

// TODO scroll bar
// TODO klawiatura
// TODO (klawisze dodatkowe: print screen, wycisz, g³oœniej, ciszej)
public class MainActivity extends ActionBarActivity {
	
	private final String CONNECTION_SUCCESFUL_MESSAGE = "Connected succesfuly!";
	private final String UNKNOWN_HOST_MESSAGE = "Unknown host!";
	private final String IOEXCEPTION_MESSAGE = "Error encountered!\nCheck your wi-fi connection.";
	
	private final int PRESS_LENGTH_TO_BLOCK_LEFT_CLICK = 500;
	private final int SINGLE_CLICK_DELAY = 200;
	private final int ACCIDENTAL_SWIPE_LENGTH = 8;
	
	private LogConfigurator logConfigurator;
	private Logger logger;

	private String IPAddress;

	private Socket socket;
	private DataOutputStream dos;
	private SignalSender sender;

	private Handler handler;
	private Runnable longPressed, singleClick;
	
	private Button leftButton;
	private Button rightButton;

	private boolean leftClickBlocked;
	
	private int x1, x2, y1, y2, xOnActionDown, yOnActionDown;
	private volatile int numberOfMouseAreaClicks, numberOfMouseAreaReleases;
	
	private String coordinatesDifference;
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		configureLogger();
		
		leftButton = (Button)findViewById(R.id.left_button);
		rightButton = (Button)findViewById(R.id.right_button);

		getIPAndStartTheApp();
		createLongPressedHandler();
	}
	
	private void configureLogger() {
		logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                        + File.separator + "com.jonasz.remotecomputercontrollerclient" + File.separator + "logs"
                        + File.separator + "log4j.txt");
        String str = Environment.getExternalStorageDirectory()
                + File.separator + "com.jonasz.remotecomputercontrollerclient" + File.separator + "logs"
                + File.separator + "log4j.txt";
        Log.i("A", str);
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
        logger = Logger.getLogger(MainActivity.class);
        logger.info("Log created.");
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

				if (!Utilities.isIPAddressValid(IPAddress))
					Toast.makeText(getApplicationContext(), "Invalid IP address.", Toast.LENGTH_LONG).show();
				else
					successfullyStartApp();
			}
		});
		
		getIPDialog.setCanceledOnTouchOutside(false);
		getIPDialog.show();
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
			sender = new SignalSender(dos, logger);
			
			Toast.makeText(this, CONNECTION_SUCCESFUL_MESSAGE, Toast.LENGTH_LONG).show();
		} catch (UnknownHostException e) {
			Toast.makeText(this, UNKNOWN_HOST_MESSAGE, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			logger.warn("IOException - Couldn't create socket/output stream.");
			Toast.makeText(this, IOEXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
		}		
	}

	private void createLongPressedHandler() {
		handler  = new Handler();
		
		longPressed = new Runnable() { 
		    public void run() { 		
		        leftClickBlocked = true;
		        leftButton.setPressed(true);
		    }   
		};
		
		singleClick = new Runnable() { 
		    public void run() {		    	
				if (numberOfMouseAreaClicks == 1 && numberOfMouseAreaReleases == 1) {
					sender.leftButtonClicked();
					leftClickBlocked = false;
			        leftButton.setPressed(false);
					sender.leftButtonReleased();
				}
				else if (numberOfMouseAreaClicks == 2 && numberOfMouseAreaReleases == 1) {
					sender.leftButtonClicked();
					leftClickBlocked = true;
			        leftButton.setPressed(true);
				}				
				else if (numberOfMouseAreaClicks == 2 && numberOfMouseAreaReleases == 2) {
					sender.leftButtonClicked();
					sender.leftButtonReleased();
					leftClickBlocked = false;
			        leftButton.setPressed(false);
			        sender.leftButtonClicked();
					sender.leftButtonReleased();
				}		
				
				numberOfMouseAreaClicks = 0;
				numberOfMouseAreaReleases = 0;
		    }   
		};
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if (wereSocketAndStreamCreated())
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:		
					numberOfMouseAreaClicks++;
					
					if (numberOfMouseAreaClicks == 1)
						handler.postDelayed(singleClick, SINGLE_CLICK_DELAY);
					
					x1 = (int)event.getX();
					y1 = (int)event.getY();
					
					xOnActionDown = x1;
					yOnActionDown = y1;
					
					break;

				case MotionEvent.ACTION_MOVE:					
					if (!leftClickBlocked)
						sender.leftButtonReleased();

					x2 = (int)event.getX();
					y2 = (int)event.getY(); 
					
					coordinatesDifference = Integer.toString(x2 - x1) + "," + Integer.toString(y2 - y1);

					if (Math.abs(x2 - x1) > ACCIDENTAL_SWIPE_LENGTH && Math.abs(y2 - y1) > ACCIDENTAL_SWIPE_LENGTH) {
						numberOfMouseAreaClicks = 0;
						numberOfMouseAreaReleases = 0;
						handler.removeCallbacks(singleClick); 
					}
					
					x1 = x2;
					y1 = y2;
					
					try {
						dos.writeUTF(coordinatesDifference);
					} catch (IOException e) {
						logger.warn("IOException - Couldn't send coordinates to server.");
					}
					
					break;
					
				case MotionEvent.ACTION_UP:					
					x2 = (int)event.getX();
					y2 = (int)event.getY(); 
				
					if (Math.abs(x2 - xOnActionDown) < ACCIDENTAL_SWIPE_LENGTH && Math.abs(y2 - yOnActionDown) < ACCIDENTAL_SWIPE_LENGTH)
						numberOfMouseAreaReleases++;
					
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
	            	leftClickBlocked = false;
	            	leftButton.setPressed(false);
	            	
	            	if (button.getId() == leftButton.getId()) {
	            		handler.postDelayed(longPressed, PRESS_LENGTH_TO_BLOCK_LEFT_CLICK);
	            		
	            		sender.leftButtonClicked();
	            	}
	            	
	    			if (button.getId() == rightButton.getId())	
	    				sender.rightButtonClicked();

	                return true;
	                
	            case MotionEvent.ACTION_UP:
	            	handler.removeCallbacks(longPressed); 
	            	
	            	if (button.getId() == leftButton.getId()) {
	            		if (!leftClickBlocked)
	            			sender.leftButtonReleased();
	            	}
	            	
	    			if (button.getId() == rightButton.getId())	
	    				sender.rightButtonReleased();
	    			
	                return true;
			}
			
			v.performClick();
			
			return false;
		}
	}
	
	public void onDestroy() {
		closeSocketAndStream();
		super.onDestroy();		
	}
	
	private void closeSocketAndStream() {
		if (!wereSocketAndStreamCreated())
			return;

		try {
			socket.close();
			dos.close();
		} catch (IOException e) {
			logger.warn("IOException - Couldn't close socket/stream.");
		}
	}
	
	private boolean wereSocketAndStreamCreated() {
		if (socket == null || dos == null) 
			return false;
		
		return true;
	}
}
