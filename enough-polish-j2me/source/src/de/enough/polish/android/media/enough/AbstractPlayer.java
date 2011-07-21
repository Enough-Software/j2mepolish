//#condition polish.android
package de.enough.polish.android.media.enough;

import java.util.ArrayList;

import de.enough.polish.android.media.Control;
import de.enough.polish.android.media.MediaException;
import de.enough.polish.android.media.Player;
import de.enough.polish.android.media.PlayerListener;

/**
 * This abstract Player class handles state change and listener notification.
 * @author rickyn
 *
 */
public abstract class AbstractPlayer implements Player {

	private int meState = Player.UNREALIZED;
	private ArrayList listeners = new ArrayList();
	
	public final void addPlayerListener(PlayerListener playerListener) {
		if( ! this.listeners.contains(playerListener)) {
			this.listeners.add(playerListener);
		}
	}

	public void close() {
		doClose();
		this.meState = CLOSED;
		fireEvent(PlayerListener.CLOSED, null);
	}

	protected abstract void doClose();

	/**
	 * This method will not interrupt a blocking {@link #realize()} call.
	 */
	public void deallocate() {
		if(this.meState == STARTED) {
			try {
				stop();
			} catch (MediaException e) {
				//#debug
				System.out.println("Could not deallocate player:"+e);
				return;
			}
		}
		doDeallocate();
		this.meState = REALIZED;
	}

	protected abstract void doDeallocate();

	public final String getContentType() {
		if(this.meState == UNREALIZED) {
			throw new IllegalStateException("Method must not be called in state 'UNREALIZED'.");
		}
		return doGetContentType();
	}

	protected abstract String doGetContentType();

	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	public final long getMediaTime() {
		if(this.meState == CLOSED) {
			throw new IllegalStateException("The method 'getMediaTime' must not be called in state 'CLOSED'.");
		}
		return doGetMediaTime();
	}

	protected abstract long doGetMediaTime();

	public final int getState() {
		return this.meState;
	}

	public final void prefetch() throws MediaException {
		switch(this.meState) {
			case UNREALIZED:
				realize();
				// Fall through intended.
			case REALIZED:
				doPrefetch();
				this.meState = PREFETCHED;
				break;
			case CLOSED:
				throw new IllegalStateException("The method 'prefetch' must not be called in state 'CLOSED'.");
			default:
				break;
		}
	}

	protected abstract void doPrefetch() throws MediaException;

	public void realize() throws MediaException {
		if(this.meState == UNREALIZED) {
			doRealize();
			this.meState = REALIZED;
		} else {
			throw new MediaException("The method 'realize' must only be called in the 'UNREALIZED' state");
		}
		
	}

	/**
	 * @see #realize()
	 */
	protected abstract void doRealize() throws MediaException;

	public final void removePlayerListener(PlayerListener playerListener) {
		this.listeners.remove(playerListener);
	}

	public void setLoopCount(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public long setMediaTime(long arg0) throws MediaException {
		if(this.meState == CLOSED) {
			throw new IllegalStateException("The method 'setMediaTime' must not be called in state 'CLOSED'.");
		}
		if(this.meState == UNREALIZED) {
			throw new IllegalStateException("The method 'setMediaTime' must not be called in state 'UNREALIZED'.");
		}
		return doSetMediaTime(arg0);
	}

	protected abstract long doSetMediaTime(long arg0);

	public final void start() throws MediaException {
		switch(this.meState) {
		case UNREALIZED:
			// Fall through intended.
		case REALIZED:
			prefetch();
			// Fall through intended.
		case PREFETCHED:
			doStart();
			this.meState = STARTED;
			fireEvent(PlayerListener.STARTED, null);
			doStarted();
			break;
		case CLOSED:
			throw new IllegalStateException("The method 'start' must not be called in the 'CLOSED' state.");
		}
	}

	protected abstract void doStarted();
	
	/**
	 * @see #start()
	 */
	protected abstract void doStart();

	public void stop() throws MediaException {
		switch(this.meState) {
		case STARTED:
			doStop();
			this.meState = PREFETCHED;
			break;
		case CLOSED:
			throw new IllegalStateException("When calling stop, the player must not be in state 'CLOSED'.");
		default: break;
		}
	}

	protected abstract void doStop() throws MediaException;

	public abstract Control getControl(String arg0);

	public abstract Control[] getControls();

	protected void fireEvent(String event, Object data) {
		int numberOfListeners = this.listeners.size();
		for (int i = 0; i < numberOfListeners; i++) {
			PlayerListener listener = (PlayerListener)this.listeners.get(i);
			listener.playerUpdate(this,event, data);
		}
	}
}
