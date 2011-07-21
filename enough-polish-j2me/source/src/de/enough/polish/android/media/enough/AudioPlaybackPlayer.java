//#condition polish.android
package de.enough.polish.android.media.enough;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import de.enough.polish.android.media.Control;
import de.enough.polish.android.media.MediaException;
import de.enough.polish.android.media.control.VolumeControl;
import de.enough.polish.android.midlet.MidletBridge;

public class AudioPlaybackPlayer extends AbstractPlayer implements VolumeControl {

	private final MediaPlayer mediaPlayer;
	private final AudioManager androidAudioManager;
	
	public AudioPlaybackPlayer(String locator) throws MediaException {
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		if(locator == null) {
			throw new RuntimeException("The parameter 'locator' must not be null.");
		}
		if(locator.startsWith("http://")){
			try {
				this.mediaPlayer.setDataSource(locator);
			} catch (IllegalArgumentException e) {
				throw new MediaException(e.getMessage());
			} catch (IllegalStateException e) {
				throw new MediaException(e.getMessage());
			} catch (IOException e) {
				throw new MediaException(e.getMessage());
			}
		}
		this.androidAudioManager = (AudioManager)MidletBridge.instance.getSystemService(Context.AUDIO_SERVICE);
	}

	protected void doClose() {
		this.mediaPlayer.release();
	}

	protected void doDeallocate() {
		this.mediaPlayer.reset();
	}

	protected String doGetContentType() {
		throw new RuntimeException("Not supported.");
	}

	protected long doGetMediaTime() {
		return this.mediaPlayer.getCurrentPosition();
	}

	protected void doPrefetch() throws MediaException {
		try {
			this.mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			throw new MediaException(e.getMessage());
		} catch (IOException e) {
			throw new MediaException(e.getMessage());
		}
	}

	protected void doRealize() throws MediaException {
		// not needed. Done with setDataSource.
	}

	protected long doSetMediaTime(long arg0) {
		throw new RuntimeException("Not supported.");
	}

	protected void doStart() {
		this.mediaPlayer.start();
	}

	protected void doStop() throws MediaException {
		this.mediaPlayer.stop();
	}

	public int getLevel() {
		int androidMaxVolume = this.androidAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int androidVolume = this.androidAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int meLevel = (int) (100f / androidMaxVolume * androidVolume);
		return meLevel;
	}

	public boolean isMuted() {
		int streamVolume = this.androidAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		return streamVolume <= 0;
	}

	public int setLevel(int level) {
		if(level < 0) {
			level = 0;
		}
		if(level > 100) {
			level = 100;
		}
		int androidMaxVolume = this.androidAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int androidVolume = (int)(androidMaxVolume / 100f * level);
		this.androidAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, androidVolume, AudioManager.FLAG_SHOW_UI);
		return level;
	}

	public void setMute(boolean mute) {
		this.androidAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
	}

	public Control getControl(String controlType) {
		if("VolumeControl".equals(controlType)) {
			return this;
		}
		return null;
	}

	public Control[] getControls() {
		return new Control[] {this};
	}

	protected void doStarted() {
		// TODO Auto-generated method stub
		
	}

}
