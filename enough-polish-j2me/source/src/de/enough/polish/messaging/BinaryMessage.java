//#condition !polish.api.wmapi && polish.useWMAPIWrapper && polish.supportsWMAPIWrapper
package de.enough.polish.messaging;

import java.util.Date;

/**
 * An interface representing a binary message.
 * 
 * An interface representing a binary message.
 * This is a subinterface of
 * <A HREF="../../../../de/enough/polish/messaging/Message.html"><CODE>Message</CODE></A> which contains methods to get and set the
 * binary data payload. The <code>setPayloadData()</code>
 * method sets the value of the payload in the
 * data container without any checking whether the value
 * is valid in any way.
 * Methods for manipulating the address portion of
 * the message are inherited from <tt>Message</tt>.
 * 
 * <p>Object instances implementing this interface are just
 * containers for the data that is passed in.
 * </p>
 * <HR>
 * 
 * 
 */
public class BinaryMessage 
implements Message
{
	protected String msisdn;
	protected byte[] data;
	protected long timeStamp;
	
	protected BinaryMessage( String msisdn, byte[] data ) {
		this.data = data;
		this.msisdn = msisdn;
		this.timeStamp = System.currentTimeMillis();
	}

	/**
	 * Returns the message payload data as an array
	 * of bytes.
	 * 
	 * <p>Returns <code>null</code>, if the payload for the message
	 * is not set.
	 * </p>
	 * <p>The returned byte array is a reference to the
	 * byte array of this message and the same reference
	 * is returned for all calls to this method made before the
	 * next call to <code>setPayloadData</code>.
	 * 
	 * @return the payload data of this message or null if the data has not been set
	 * @see #setPayloadData(byte[])
	 */
	public byte[] getPayloadData() {
		return this.data;
	}

	/**
	 * Sets the payload data of this message. The payload may
	 * be set to <code>null</code>.
	 * <p>Setting the payload using this method only sets the
	 * reference to the byte array. Changes made to the contents
	 * of the byte array subsequently affect the contents of this
	 * <code>BinaryMessage</code> object. Therefore, applications
	 * should not reuse this byte array before the message is sent and the
	 * <code>MessageConnection.send</code> method returns.
	 * </p>
	 * 
	 * @param data - payload data as a byte array
	 * @see #getPayloadData()
	 */
	public void setPayloadData(byte[] data) {
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
