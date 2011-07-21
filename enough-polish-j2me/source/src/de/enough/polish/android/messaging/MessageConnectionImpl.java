//#condition polish.android
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.android.messaging;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Date;

import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;


/**
 * Provides MessageConnection functionalities.
 * Note: <uses-permission id="android.permission.RECEIVE_SMS" />  is required for receiving SMS
 * Note 2: when using the PushRegistry, we could register the service the Manifest.xml as well.
 */
public class MessageConnectionImpl
extends BroadcastReceiver
implements MessageConnection 
{
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private final String url;
//	private final int mode;
//	private final boolean timeouts;
	private MessageListener messageListener;
	private short port;
	private final Object receiveLock;
	private final ArrayList<Message> receivedMessages;
	private boolean isListeningForMessages;

	public MessageConnectionImpl(String url, int mode, boolean timeouts) {
		url = url.substring( "sms://".length() );
		this.url = url;
		// TODO: Handle the mode and the timeout.
//		this.mode = mode;
//		this.timeouts = timeouts;
		this.receiveLock = new Object();
		this.receivedMessages = new ArrayList<Message>();
		int colonIndex = url.indexOf(':');
		if (colonIndex != -1 && colonIndex < url.length() - 1) {
			String portStr = url.substring(colonIndex + 1);
			for (int i=0; i<portStr.length(); i++) {
				if (!Character.isDigit( portStr.charAt(i))) {
					portStr = portStr.substring(0, i);
					break;
				}
			}
			this.port = (short) Integer.parseInt(portStr);
		}
	}

	public Message newMessage(String type) {
		return newMessage( type, this.url );
	}

	public Message newMessage(String type, String address) {
		if (MessageConnection.TEXT_MESSAGE.equals(type)) {
			return new TextMessageImpl( address );
		} else if (MessageConnection.BINARY_MESSAGE.equals(type)) {
			return new BinaryMessageImpl( address );
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int numberOfSegments(Message msg) {
		// maximum length is 140 byts or 160 characters (with 7bit characters):
		if (msg instanceof TextMessage) {
			String text = ((TextMessage) msg).getPayloadText();
			if (text == null) {
				return 1;
			} else {
				return (text.length() / 160) + 1;
			}
		} else if (msg instanceof BinaryMessage) {
			byte[] data = ((BinaryMessage)msg).getPayloadData();
			if (data == null) {
				return 1;
			} else {
				return (data.length / 140) + 1;
			}
		}
		return 0;
	}

	public Message receive() throws IOException, InterruptedIOException {
		if (!this.isListeningForMessages) {
			setupMessageReceiver();
		}
		synchronized ( this.receiveLock ) {
			if (this.receivedMessages.size() > 0) {
				Message msg = this.receivedMessages.remove(0);
				return msg;
			}
			try {
				this.receiveLock.wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		if (this.receivedMessages.size() > 0) {
			Message msg = this.receivedMessages.remove(0);
			return msg;
		} else {
			throw new InterruptedIOException();
		}
	}

	private void setupMessageReceiver() {
		this.isListeningForMessages = true;
		IntentFilter filter = new IntentFilter(ACTION);
		MidletBridge.instance.registerReceiver( this, filter );
	}

	public void send(Message msg) throws IOException, InterruptedIOException {
		String address = msg.getAddress();
		if (msg instanceof TextMessage) {
			//TODO when a port is specified use data message?
			String text = ((TextMessage) msg).getPayloadText();
			SmsManager.getDefault().sendTextMessage(address, null, text, null, null );
		} else if (msg instanceof BinaryMessage) {
			byte[] data = ((BinaryMessage)msg).getPayloadData();
			SmsManager.getDefault().sendDataMessage(address, null, this.port, data, null, null );
		} else {
			throw new IOException("invalid type: " + msg);
		}
	}

	public void setMessageListener(MessageListener l) throws IOException {
		if (!this.isListeningForMessages) {
			setupMessageReceiver();
		}
		this.messageListener = l;
	}

	public void close() throws IOException {
		// TODO check if SmsManager needs to be released 
		synchronized (this.receiveLock) {
			this.receiveLock.notify();
		}
		if (this.isListeningForMessages) {
			MidletBridge.instance.unregisterReceiver( this );
			this.isListeningForMessages = false;
		}
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		//#debug
		System.out.println("SMS:onReceive.");
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");
		for (int n = 0; n < pdus.length; n++) {
			SmsMessage androidSmsMessage = SmsMessage.createFromPdu((byte[]) pdus[n]);
			//TODO: how to create binary messages?
			TextMessage textMessage = new TextMessageImpl( androidSmsMessage.getOriginatingAddress(), new Date(androidSmsMessage.getTimestampMillis()) );
			textMessage.setPayloadText(androidSmsMessage.getMessageBody());
			this.receivedMessages.add(textMessage);
			synchronized (this.receiveLock) {
				this.receiveLock.notify();
			}
			if (this.messageListener != null) {
				this.messageListener.notifyIncomingMessage(this);
			}
		}
		
	}

}
