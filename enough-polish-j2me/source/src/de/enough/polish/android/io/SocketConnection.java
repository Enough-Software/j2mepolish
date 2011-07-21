//#condition polish.android
package de.enough.polish.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SocketConnection extends StreamConnection {

	byte DELAY = 0;
	
	byte KEEPALIVE = 2;
	
	byte LINGER = 1;
	
	byte RCVBUF = 3;
	
	byte SNDBUF = 4;
	       
	void close() throws IOException;
	
	InputStream openInputStream() throws IOException;
	
	OutputStream openOutputStream() throws IOException;
	
	DataInputStream openDataInputStream() throws IOException;

	DataOutputStream openDataOutputStream() throws IOException;
	
	String getAddress();
	
	String getLocalAddress();
	
	int getLocalPort();
	
	int getPort();
	
	void setSocketOption(byte option, int value) throws IOException;
		
	int getSocketOption(byte option) throws IOException;
}
