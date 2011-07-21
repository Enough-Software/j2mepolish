//#condition !polish.api.wmapi && polish.useWMAPIWrapper && polish.supportsWMAPIWrapper
package de.enough.polish.messaging;

/**
 * The <code>MessageListener</code> interface provides
 * a mechanism for the application to be notified
 * of incoming messages.
 * 
 * <p>When an incoming message arrives, the <code>notifyIncomingMessage()</code>
 * method is called. The application MUST retrieve the
 * message using the <code>receive()</code> method of the
 * <code>MessageConnection</code>. <tt>MessageListener</tt> should not call
 * <tt>receive()</tt> directly. Instead, it can start a new thread which will
 * receive the message or call another
 * method of the application (which is outside of the
 * listener) that will call <tt>receive()</tt>. For an example of how to use
 * MessageListener, see <a href="#example">A Sample MessageListener Implementation</a>.
 * </p>
 * <p>The listener mechanism allows applications to receive
 * incoming messages without needing to have a thread blocked
 * in the <code>receive()</code> method call.
 * </p>
 * <p>If multiple messages arrive very closely together in time,
 * the implementation has the option of calling this listener from multiple
 * threads in parallel. Applications MUST be prepared to handle
 * this and implement any necessary synchronization as part
 * of the application code, while obeying the requirements
 * set for the listener method.
 * </p>
 * <a name="example"></a>
 * <h2>A Sample MessageListener Implementation</h2>
 * <p>The following sample code illustrates how lightweight and resource-friendly
 * a <tt>MessageListener</tt> can be. In the sample, a separate thread is
 * spawned to handle message reading.
 * The MIDlet life cycle is respected by releasing connections and signalling
 * threads to terminate when the MIDlet is paused or destroyed.</p>
 * <pre>
 * // Sample message listener program.
 * import java.io.IOException;
 * import javax.microedition.midlet.*;
 * import javax.microedition.io.*;
 * import javax.wireless.messaging.*;
 * public class Example extends MIDlet implements MessageListener {
 * MessageConnection messconn;
 * boolean done;
 * Reader reader;
 * // Initial tests setup and execution.
 * public void startApp() {
 * try {
 * // Get our receiving port connection.
 * messconn = (MessageConnection)
 * Connector.open("sms://:6222");
 * // Register a listener for inbound messages.
 * messconn.setMessageListener(this);
 * // Start a message-reading thread.
 * done = false;
 * reader = new Reader();
 * new Thread(reader).start();
 * } catch (IOException e) {
 * // Handle startup errors
 * }
 * }
 * // Asynchronous callback for inbound message.
 * public void notifyIncomingMessage(MessageConnection conn) {
 * if (conn == messconn) {
 * reader.handleMessage();
 * }
 * }
 * // Required MIDlet method - release the connection and
 * // signal the reader thread to terminate.
 * public void pauseApp() {
 * done = true;
 * try {
 * messconn.close();
 * } catch (IOException e) {
 * // Handle errors
 * }
 * }
 * // Required MIDlet method - shutdown.
 * // @param unconditional forced shutdown flag
 * public void destroyApp(boolean unconditional) {
 * done = true;
 * try {
 * messconn.setMessageListener(null);
 * messconn.close();
 * } catch (IOException e) {
 * // Handle shutdown errors.
 * }
 * }
 * // Isolate blocking I/O on a separate thread, so callback
 * // can return immediately.
 * class Reader implements Runnable {
 * private int pendingMessages = 0;
 * 
 * // The run method performs the actual message reading.
 * public void run() {
 * while (!done) {
 * synchronized(this) {
 * if (pendingMessages == 0) {
 * try {
 * wait();
 * } catch (Exception e) {
 * // Handle interruption
 * }
 * }
 * pendingMessages--;
 * }
 * 
 * // The benefit of the MessageListener is here.
 * // This thread could via similar triggers be
 * // handling other kind of events as well in
 * // addition to just receiving the messages.
 * 
 * try {
 * Message mess = messconn.receive();
 * } catch (IOException ioe) {
 * // Handle reading errors
 * }
 * }
 * }
 * 
 * public synchronized void handleMessage() {
 * pendingMessages++;
 * notify();
 * }
 * 
 * }
 * }
 * </pre>
 * <p>&nbsp;</p>
 * <HR>
 * 
 * 
 */
public interface MessageListener
{
	/**
	 * Called by the platform when an incoming
	 * message arrives to a <code>MessageConnection</code>
	 * where the application has registered this listener
	 * object.
	 * 
	 * <p>This method is called once for each incoming
	 * message to the <code>MessageConnection</code>.
	 * </p>
	 * <p><strong>NOTE</strong>: The implementation of this method MUST
	 * return quickly and MUST NOT perform any extensive
	 * operations. The application SHOULD NOT receive and
	 * handle the message during this method call. Instead, it
	 * should act only as a trigger to start the activity
	 * in the application's own thread.
	 * </p>
	 * 
	 * @param conn - the MessageConnection where the incoming message has arrived
	 */
	void notifyIncomingMessage( MessageConnection conn);

}
