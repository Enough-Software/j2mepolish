//#condition polish.android
package de.enough.polish.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class SocketConnectionImpl implements SocketConnection {
	
	private int mode = -1;
	
	private int dstPort;
	
	private String dstName;
	
	private Socket internalSocket;

    SocketConnectionImpl(String url, int mode) throws IOException {
    	this.mode = mode;
    	splitUrl(url);
//    	InetAddress address = InetAddress.getByName(dstName);
//    	internalSocket = new Socket(address, dstPort);
    	this.internalSocket = new Socket(this.dstName, this.dstPort);
    }
        
	private void splitUrl(String url) {
		if (url.indexOf(Connector.SOCKET_PREFIX)!=0 || 
				url.equals(Connector.SOCKET_PREFIX)) {
			throw new IllegalArgumentException("invalid URL");
		}
		//cut off prefix
		url = url.substring(Connector.SOCKET_PREFIX.length());
		//find ":"
		int splitIdx = url.indexOf(":");
		if (splitIdx == -1 || !(url.length()>splitIdx+2) ) {
			throw new IllegalArgumentException("Invalid URL format");
		}
		this.dstName = url.substring(0, splitIdx);
		String portStr = url.substring(splitIdx+1);
		this.dstPort = Integer.parseInt(portStr);		
	}

	public void close() throws IOException {
		this.internalSocket.close();
	}
	
	public InputStream openInputStream() throws IOException {
		if (this.mode == Connector.WRITE) {
			throw new IOException("connection is write only");
		} else {
			return this.internalSocket.getInputStream();
		}
	}
	
	public OutputStream openOutputStream() throws IOException {
		if (this.mode == Connector.READ) {
			throw new IOException("connection is read only");
		} else {
			return this.internalSocket.getOutputStream();			
		}
	}
	
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}
	
	public String getAddress() {
		return this.internalSocket.getInetAddress().toString();
	}
	
	public String getLocalAddress() {
		return this.internalSocket.getLocalAddress().toString();
	}
	
	public int getLocalPort() {
		return this.internalSocket.getLocalPort();
	}
	
	public int getPort() {
		return this.internalSocket.getPort();
	}
	
	public void setSocketOption(byte option, int value) throws IOException {
		switch (option) {
			case DELAY:
				if (value==0) {
					this.internalSocket.setTcpNoDelay(false);
				} else {
					this.internalSocket.setTcpNoDelay(true);						
				}
				break;
			case KEEPALIVE:
				if (value==0) {
					this.internalSocket.setKeepAlive(false);
				} else {
					this.internalSocket.setKeepAlive(true);						
				}
				break;
			case LINGER:
				if (value<=0) {
					this.internalSocket.setSoLinger(false, 0);
				} else {
					this.internalSocket.setSoLinger(true, value);						
				}
				break;
			case RCVBUF:
				this.internalSocket.setReceiveBufferSize(value);
				break;
			case SNDBUF:
				this.internalSocket.setSendBufferSize(value);
				break;
			default:
				throw new IllegalArgumentException("invalid socket option");
		}
	}
	
	public int getSocketOption(byte option) throws IOException {
		switch (option) {
			case DELAY:
				if (this.internalSocket.getTcpNoDelay()) {
					return 1;						
				}
				else {
					return 0;
				}
			case KEEPALIVE:
				if (this.internalSocket.getKeepAlive()) {
					return 1;
				} else {
					return 0;						
				}
			case LINGER:
				return this.internalSocket.getSoLinger();
			case RCVBUF:
					return this.internalSocket.getReceiveBufferSize();
			case SNDBUF:
				return this.internalSocket.getSendBufferSize();
			default:
				throw new IllegalArgumentException("invalid socket option");
		}
	}
}
