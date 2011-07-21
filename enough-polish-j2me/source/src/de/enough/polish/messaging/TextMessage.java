//#condition !polish.api.wmapi && polish.useWMAPIWrapper && polish.supportsWMAPIWrapper
package de.enough.polish.messaging;

import java.util.Date;

/**
 * An interface representing a text message.
 * 
 * An interface representing a text message.
 * This is a subinterface of
 * <A HREF="../../../javax/wireless/messaging/Message.html"><CODE>Message</CODE></A> which
 * contains methods to get and set the text payload. The
 * <A HREF="../../../javax/wireless/messaging/TextMessage.html#setPayloadText(java.lang.String)"><CODE>setPayloadText</CODE></A> method sets the value
 * of the payload in the data container without any checking whether the value
 * is valid in any way.  Methods for manipulating the address portion of
 * the message are inherited from <tt>Message</tt>.
 * 
 * <p>Object instances implementing this interface are just
 * containers for the data that is passed in.
 * </p>
 * 
 * <h3>Character Encoding Considerations</h3>
 * <p>Text messages using this interface
 * deal with <code>String</code>s encoded in Java.
 * The underlying implementation will convert the
 * <code>String</code>s into a suitable encoding for the messaging
 * protocol in question. Different protocols recognize different character
 * sets. To ensure that characters are transmitted
 * correctly across the network, an application should use the
 * character set(s) recognized by the protocol.
 * If an application is unaware of the protocol, or uses a
 * character set that the protocol does not recognize, then some characters
 * might be transmitted incorrectly.
 * </p>
 * <HR>
 * 
 * 
 */
public class TextMessage 
implements Message
{
	protected String msisdn;
	protected String data;
	protected long timeStamp;
	
	protected TextMessage( String msisdn, String data ) {
		this.data = data;
		this.msisdn = msisdn;
		this.timeStamp = System.currentTimeMillis();
	}
	
	/**
	 * Returns the message payload data as a <code>String</code>.
	 * 
	 * @return the payload of this message, or null if the payload for the message is not set
	 * @see #setPayloadText(java.lang.String)
	 */
	public String getPayloadText() {
		return this.data;
	}

	/**
	 * Sets the payload data of this message. The payload data
	 * may be <code>null</code>.
	 * 
	 * @param data - payload data as a String
	 * @see #getPayloadText()
	 */
	public void setPayloadText(String data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.messaging.Message#getAddress()
	 */
	public String getAddress() {
		return this.msisdn;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.messaging.Message#setAddress(java.lang.String)
	 */
	public void setAddress(String addr) {
		this.msisdn = addr;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.messaging.Message#getTimestamp()
	 */
	public Date getTimestamp() {
		return new Date( this.timeStamp );
	}

}
