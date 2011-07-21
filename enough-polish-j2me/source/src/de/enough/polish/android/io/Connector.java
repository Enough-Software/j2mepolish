//#condition polish.android
package de.enough.polish.android.io;

import java.io.IOException;

import de.enough.polish.android.io.file.FileConnection;
import de.enough.polish.android.io.file.FileConnectionImpl;
import de.enough.polish.android.messaging.MessageConnectionImpl;

public class Connector {
	
	public static final int READ = 1;
	
	public static final int READ_WRITE = 3;
	
	public static final int WRITE = 2;	
	
	private static final int TYPE_SOCKET = 1;
	
	private static final int TYPE_HTTP = 2;
	
	private static final int TYPE_HTTPS = 3;
	
	private static final int TYPE_FILE = 4;
	
	private static final int TYPE_SMS = 5;
	
	
	static final String SOCKET_PREFIX = "socket://";
	
	static final String HTTP_PREFIX = "http://";

	static final String HTTPS_PREFIX = "https://";

	static final String FILE_PREFIX = "file://";

	
	private static String SMS_PREFIX = "sms://";
	
	private Connector() {
		// Hidden.
	}
	
	public static Connection open(String name) throws IOException {
		return createConnection(name, READ_WRITE, false);
	}
	
	public static Connection open(String name, int mode) throws IOException {
		return createConnection(name, mode, false);
	}
	
	public static Connection open(String name, int mode, boolean timeouts) throws IOException {
		return createConnection(name, mode, timeouts);
	}
	
	private static Connection createConnection(String name, int mode, boolean timeouts) throws IOException {
		if (mode < READ || mode > READ_WRITE) {
			throw new IllegalArgumentException("invalid connection mode:"+mode);
		}
		int type = detectConnectionType(name);
		
		switch (type) {
			case TYPE_SOCKET:
				return createSocketConnection(name, mode, timeouts);				
			case TYPE_HTTP:
				return createHttpConnection(name, mode, timeouts);
			case TYPE_HTTPS:
				return createHttpsConnection(name, mode, timeouts);
			case TYPE_FILE:
				return createFileConnection(name, mode, timeouts);
			case TYPE_SMS:
				return createSmsConnection(name, mode, timeouts);
			default:
				throw new IllegalArgumentException("Unknown connection type:"+type);
		}
	}
	

	private static Connection createSmsConnection(String url, int mode, boolean timeouts) {
		return new MessageConnectionImpl(url,mode,timeouts);
	}

	private static int detectConnectionType(String name) {
		if (name.indexOf(SOCKET_PREFIX)==0) {
			return TYPE_SOCKET;
		}
		else if (name.indexOf(HTTP_PREFIX)==0) {
			return TYPE_HTTP;
		} 
		else if (name.indexOf(HTTPS_PREFIX)==0) {
			return TYPE_HTTPS;
		} 
		else if (name.indexOf(FILE_PREFIX)==0) {
			return TYPE_FILE;
		} if(name.indexOf(SMS_PREFIX) == 0) {
			return TYPE_SMS;
		}
		else {
			return -1;
		}
	}
	
	private static SocketConnection createSocketConnection(String url, int mode,
			boolean timeouts) throws IOException {
		
		return new SocketConnectionImpl(url, mode);		
	}
	
	private static HttpConnection createHttpConnection(String url, int mode, boolean timeouts) 
	throws IOException 
	{
		return new HttpConnectionImpl(url, mode);		
	}
	
	private static Connection createHttpsConnection(String url, int mode, boolean timeouts)
	throws IOException 
	{
		return new HttpsConnectionImpl(url, mode);	
	}

	
	private static FileConnection createFileConnection(String url, int mode, boolean timeouts) 
	throws IOException 
	{
		return new FileConnectionImpl(url, mode);		
	}
}
