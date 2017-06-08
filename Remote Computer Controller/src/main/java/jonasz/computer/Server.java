package jonasz.computer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
	
	private ServerSocket socket;

	public Server(int socketPort, TrayManager tm) throws IOException {
		tm.setTooltip("IP address: " + getIpAddress());
		socket = new ServerSocket(socketPort);
	}

	private String getIpAddress() throws SocketException { 
        for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = (NetworkInterface) en.nextElement();
            for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    String ipAddress=inetAddress.getHostAddress().toString();
                    return ipAddress;
                }
            }
        }
        
        return null; 
	}
	
	public Socket getClientSocket() throws IOException {
		return socket.accept();
	}
	
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
