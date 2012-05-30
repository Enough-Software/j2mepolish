package de.enough.polish.authentication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.util.TimePoint;

public class AccessToken
implements Externalizable
{
	private static final int VERSION = 100;
	private String token;
	private TimePoint expires;
	
	public AccessToken(String token, TimePoint expires) {
		if (token == null) {
			throw new IllegalArgumentException();
		}
		this.token = token;
		this.expires = expires;
	}


	public String getToken() {
		return this.token;
	}


	public TimePoint getExpires() {
		return this.expires;
	}
	
	public boolean isExpired() {
		return (this.expires != null) && (this.expires.getTimeInMillis() < System.currentTimeMillis());
	}


	public void setToken(String token) {
		if (token == null) {
			throw new IllegalArgumentException();
		}
		this.token = token;
	}


	public void setExpires(TimePoint expires) {
		this.expires = expires;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(VERSION);
		out.writeUTF(this.token);
		boolean isNotNull = (this.expires != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.expires.write(out);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("for version " + version);
		}
		this.token = in.readUTF();
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.expires = new TimePoint(in);
		}
	}
	
	
}
