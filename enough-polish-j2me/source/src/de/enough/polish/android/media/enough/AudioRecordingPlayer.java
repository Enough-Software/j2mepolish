//#condition polish.android
package de.enough.polish.android.media.enough;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.media.MediaRecorder;
import de.enough.polish.android.media.Control;
import de.enough.polish.android.media.MediaException;
import de.enough.polish.android.media.control.RecordControl;

public class AudioRecordingPlayer extends AbstractPlayer implements RecordControl{

	private final static int RECORDING_START_PENDING = 1;
	private final static int RECORDING_STARTING = 2;
	private final static int RECORDING_STARTED = 3;
	private final static int RECORDING_STOPPING = 4;
	private final static int RECORDING_STOPPED = 5;
	
	private int recordingState = RECORDING_STOPPED;
	private MediaRecorder androidMediaRecorder;
	private OutputStream recordOutputStream;
	private File tempRecordingFile;
	
	public AudioRecordingPlayer(String locator) throws IOException, MediaException {
		this.androidMediaRecorder = new MediaRecorder();
		setRecordLocation(locator);
	}

	@Override
	protected void doClose() {
		if(this.recordingState == RECORDING_STARTED) {
			try {
				reset();
			} catch (IOException e) {
				//#debug error
				System.out.println("Could not reset Recorder:"+e);
				return;
			}
		}
		this.androidMediaRecorder.release();
		if(this.tempRecordingFile != null) {
//		this.tempRecordingFile.delete();
		}
	}

	@Override
	protected void doDeallocate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doGetContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long doGetMediaTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doPrefetch() throws MediaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doRealize() throws MediaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected long doSetMediaTime(long arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doStop() throws MediaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Control getControl(String controlType) {
		if("RecordControl".equals(controlType)) {
			return this;
		}
		return null;
	}

	@Override
	public Control[] getControls() {
		return new Control[] {this};
	}

	/**
	 * When this method is called, all recorded data is written in one bulk to a given OutputStream.
	 */
	public void commit() throws IOException {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(this.tempRecordingFile);
		} catch (FileNotFoundException e1) {
			//#debug error
			System.out.println("Could not find temp file '"+this.tempRecordingFile+"':"+e1+". This sould not happen!");
			return;
		}
		byte[] buffer = new byte[4096];
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		if(this.recordOutputStream != null) {
			int numberOfReadBytes = 0;
			while(numberOfReadBytes != -1) {
				try {
					numberOfReadBytes = fileInputStream.read(buffer);
					if(numberOfReadBytes <= 0) {
						break;
					}
					result.write(buffer,0,numberOfReadBytes);
				} catch (IOException e) {
					//#debug error
					System.out.println("Could not read or write from file '"+this.tempRecordingFile+"':"+e);
					return;
				}
			}
			
			byte[] bytes = result.toByteArray();
			try {
				this.recordOutputStream.write(bytes);
			} catch (IOException e) {
				//#debug error
				System.out.println("Could not deliver bytes to output stream. Number of bytes to deliver:"+result.size()+":"+e);
				return;
			}
			this.tempRecordingFile.delete();
		}
	}

	public void reset() throws IOException {
		if(this.recordingState == RECORDING_STARTED) {
			stopRecord();
		}
		this.androidMediaRecorder.reset();
		this.androidMediaRecorder = new MediaRecorder();
		if(this.recordOutputStream != null) {
			this.tempRecordingFile.delete();
			this.tempRecordingFile = File.createTempFile("enoughMMAPI", ".amr");
		}
	}

	/**
	 * Only audio capture is supported. Supported encodings: amr,3gpp,mpeg4
	 */
	public void setRecordLocation(String locator) throws IOException, MediaException {
		if(this.recordingState != RECORDING_STOPPED) {
			throw new IllegalStateException("The method 'setRecordLocation' must only be called in the RECORDING_STOPPED state");
		}
		if( ! locator.startsWith("capture://audio")) {
			throw new MediaException("Only audio capture is supported at the moment");
		}
		
		int outputFormat = MediaRecorder.OutputFormat.RAW_AMR;
		
		// Easy parsing
		if(locator.indexOf("encoding=amr") != -1) {
			outputFormat = MediaRecorder.OutputFormat.RAW_AMR;
		}

		if(locator.indexOf("encoding=3gpp") != -1) {
			outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
		}
		
		if(locator.indexOf("encoding=mpeg4") != -1) {
			outputFormat = MediaRecorder.OutputFormat.MPEG_4;
		}
		
		if(this.tempRecordingFile != null) {
			this.tempRecordingFile.delete();
		}
		this.tempRecordingFile = File.createTempFile("enoughMMAPI", ".sound");
		FileDescriptor fileDescriptor = new FileOutputStream(this.tempRecordingFile).getFD();
		this.androidMediaRecorder.setOutputFile(fileDescriptor);
		this.androidMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		this.androidMediaRecorder.setOutputFormat(outputFormat);
		this.androidMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	}

	public int setRecordSizeLimit(int size) throws MediaException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setRecordStream(OutputStream stream) {
		if(stream == null) {
			throw new RuntimeException("The parameter 'stream' must not be null.");
		}
		this.recordOutputStream = stream;
	}

	public void startRecord() {
		if(getState() == STARTED) {
			if(this.recordingState == RECORDING_STARTED) {
				return;
			}
			this.recordingState = RECORDING_STARTING;
			doStartRecording();
			this.recordingState = RECORDING_STARTED;
			fireEvent("RECORD_STARTED",null);
		} else {
			this.recordingState = RECORDING_START_PENDING;
		}
	}

	public void stopRecord() {
		if(this.recordingState != RECORDING_STARTED) {
			return;
		}
		this.recordingState = RECORDING_STOPPING;
		this.androidMediaRecorder.stop();
		this.recordingState = RECORDING_STOPPED;
	}
	
	private void doStartRecording() {
		try {
			this.androidMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			//#debug error
			System.out.println("Coult not prepare MediaRecorder:"+e);
			return;
		} catch (IOException e) {
			//#debug error
			System.out.println("Coult not prepare MediaRecorder:"+e);
			return;
		}
		this.androidMediaRecorder.start();
	}

	@Override
	protected void doStarted() {
		if(this.recordingState == RECORDING_START_PENDING) {
			startRecord();
		}
	}

}
