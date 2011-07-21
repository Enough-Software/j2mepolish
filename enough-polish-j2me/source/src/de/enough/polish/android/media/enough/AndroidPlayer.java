//#condition polish.android
package de.enough.polish.android.media.enough;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import de.enough.polish.android.media.Control;
import de.enough.polish.android.media.MediaException;
import de.enough.polish.android.media.Player;
import de.enough.polish.android.media.PlayerListener;
import de.enough.polish.android.media.control.RecordControl;
import de.enough.polish.android.media.control.VolumeControl;
import de.enough.polish.android.midlet.MidletBridge;

public class AndroidPlayer implements Player,VolumeControl, RecordControl, Runnable {

	private final MediaPlayer mediaPlayer;
	
	private int meState = Player.UNREALIZED;
	private ArrayList listeners = new ArrayList();

	private AudioManager androidAudioManager;

	private OutputStream recordOutputStream;

//	private AudioRecord androidAudioRecord;

	private MediaRecorder androidMediaRecorder;

	private FileInputStream fileInputStream;

	private boolean startRecording = false;
	
	public AndroidPlayer() {
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.androidAudioManager = (AudioManager)MidletBridge.instance.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void addPlayerListener(PlayerListener playerListener) {
		if( ! this.listeners.contains(playerListener)) {
			this.listeners.add(playerListener);
		}
	}

	// TODO: Check how the different states are mapped between J2ME and Android.
	public void close() {
		this.meState = CLOSED;
		this.mediaPlayer.release();
	}

	/**
	 * This method does nothing if in the STARTED state and the stream can not be stopped.
	 */
	public void deallocate() {
		if(this.meState == STARTED) {
			try {
				stop();
			} catch (MediaException e) {
				return;
			}
		}
		if(this.meState == PREFETCHED) {
			this.mediaPlayer.release();
			this.meState = REALIZED;
		}
	}

	public String getContentType() {
		throw new RuntimeException("Not supported.");
	}

	public long getDuration() {
		return this.mediaPlayer.getDuration();
	}

	public long getMediaTime() {
		return this.mediaPlayer.getCurrentPosition();
	}

	public int getState() {
		return this.meState;
	}

	public void prefetch() throws MediaException {
		switch(this.meState) {
			case UNREALIZED: throw new RuntimeException("The Player is in UNREALIZED state. But this should never happen.");
			case REALIZED:
				try {
					this.mediaPlayer.prepare();
					this.meState = PREFETCHED;
				} catch (IllegalStateException e) {
					throw new MediaException("Could not prefetch."+e.getMessage());
				} catch (IOException e) {
					throw new MediaException("Could not prefetch."+e.getMessage());
				}
				return;
			case PREFETCHED: return;
			case STARTED: return;
			case CLOSED: return;
			default: throw new RuntimeException("The meState '"+meState+"' is unknown.");
		}
		
		
	}

	/**
	 * This method is not really needed as realization happens at construction time with the {@link #setDataSource(String)} method.
	 */
	public void realize() throws MediaException {
		// Does nothing as the realization already happened at creation time of the Player.
	}

	public void removePlayerListener(PlayerListener playerListener) {
		this.listeners.remove(playerListener);
	}

	// TODO: Register a CompletionListener to stop when the loop count is reached.
	public void setLoopCount(int count) {
		throw new RuntimeException("Not supported.");
	}

	/**
	 * This method is not supported.
	 */
	public long setMediaTime(long now) throws MediaException {
		throw new MediaException("Not supported.");
	}

	public void start() throws MediaException {
		if(this.meState == REALIZED || this.meState == UNREALIZED) {
			prefetch();
		}
		if(this.meState == STARTED) {
			return;
		}
		this.meState = STARTED;
		System.out.println("Starting audio");
		this.mediaPlayer.start();
	}

	public void stop() throws MediaException {
		this.meState = PREFETCHED;
		this.mediaPlayer.stop();
	}

	public Control getControl(String controlType) {
		if("VolumeControl".equals(controlType)) {
			return this;
		}
		if("RecordControl".equals(controlType)) {
			return this;
		}
		return null;
	}

	public Control[] getControls() {
		return new Control[] {this};
	}

	/**
	 * This method is internal. It should only be called right after creation of a MediaPlayer.
	 * @param locator
	 * @throws IOException
	 * @throws MediaException 
	 */
	public void setDataSource(String locator) throws IOException, MediaException{
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
			}
		}
		if(locator.equals("capture://audio")) {
//			this.androidAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,22050,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_8BIT,441000);
			this.androidMediaRecorder = new MediaRecorder();
			File tempFile = File.createTempFile("enough", "tmp");
			System.out.println(tempFile.getAbsolutePath());
			FileDescriptor fileDescriptor = new FileOutputStream(tempFile).getFD();
			this.androidMediaRecorder.setOutputFile(fileDescriptor);
			this.androidMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			this.androidMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			this.androidMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			this.fileInputStream = new FileInputStream(tempFile);
		}
		this.meState = REALIZED;
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

	public void commit() throws IOException {
//		throw new RuntimeException("Not supported.");
	}

	public void reset() throws IOException {
		stopRecord();
//		this.androidAudioRecord.release();
//		this.androidAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,22050,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_8BIT,441000);
	}

	public void setRecordLocation(String locator) throws IOException,
			MediaException {
		throw new RuntimeException("Not supported.");
	}

	public int setRecordSizeLimit(int size) throws MediaException {
		throw new RuntimeException("Not supported.");
	}

	public void setRecordStream(OutputStream stream) {
		if(stream == null) {
			throw new RuntimeException("The parameter 'stream' must not be null.");
		}
		this.recordOutputStream = stream;
	}

	public void startRecord() {
		if(meState == STARTED) {
			doStartRecording();
		} else {
			this.startRecording = true;
		}
	}
	
	private void doStartRecording() {
		try {
			this.androidMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.androidMediaRecorder.start();
		new Thread(this).run();
	}

	public void stopRecord() {
//		this.androidAudioRecord.stop();
	}

	public void run() {
		byte[] buffer = new byte[1024];
		int numberOfReadBytes = 0;
		while(numberOfReadBytes != -1) {
			try {
				numberOfReadBytes = this.fileInputStream.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			if(numberOfReadBytes <= 0) {
				continue;
			}
			try {
				this.recordOutputStream.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
