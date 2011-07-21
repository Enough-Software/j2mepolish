//#condition polish.api.mmapi || polish.midp2
/*
 * Created on Nov 21, 2006 at 6:16:24 PM.
 * 
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
package de.enough.polish.multimedia;


import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
import java.util.Hashtable;
import java.io.IOException;
import java.io.InputStream;

//#if polish.android
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.enough.polish.android.helper.ResourceInputStream;
import de.enough.polish.android.helper.ResourcesHelper;
import de.enough.polish.android.midlet.MidletBridge;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
//#endif
/**
 * <p>
 * Plays back audio files - at the moment this is only supported for MIDP 2.0 and devices that support the MMAPI and for Android devices.
 * </p>
 * 
 * <p>
 * Copyright Enough Software 2006 - 2009
 * </p>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AudioPlayer implements PlayerListener
//#if polish.android
	, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener
//#endif
{

	private final static Hashtable AUDIO_TYPES = new Hashtable();

	private final boolean doCachePlayer;

	private Player player;
	private PlayerListener listener;
	//#if polish.android
	private MediaPlayer androidPlayer;
	private int volumeControlStream;
	//#endif

	private final String defaultContentType;

	// -1 means the user has not set a volume level. Use the system default, i.e. alter nothing.
	private int userJ2MeLevel = -1;

	private int previousVolumeLevel = -1;

	private int androidMaxVolume = -1;

	/**
	 * Creates a new audio player with no default content type and no caching.
	 */
	public AudioPlayer() {
		this(false, null, null);
	}

	/**
	 * Creates a new audio player with no default content type.
	 * 
	 * @param doCachePlayer caches the player even though the end of the media is reached
	 */
	public AudioPlayer(boolean doCachePlayer) {
		this(doCachePlayer, null, null);
	}

	/**
	 * Creates a new audio player without caching and with no listener.
	 * @param contentType the type of the referenced media, this is being resolved to the phone's expected type automatically.
	 *        You can, for example, use the type "audio/mp3" and this method resolves the type to "audio/mpeg3", if this
	 *        is expected by the device.
	 */
	public AudioPlayer(String contentType) {
		this(false, contentType, null);
	}

	/**
	 * Creates a new audio player with no listener
	 * @param doCachePlayer caches the player even though the end of the media is reached
	 * @param contentType the type of the referenced media, this is being resolved to the phone's expected type automatically.
	 *        You can, for example, use the type "audio/mp3" and this method resolves the type to "audio/mpeg3", if this
	 *        is expected by the device.
	 */
	public AudioPlayer(boolean doCachePlayer, String contentType) {
		this(doCachePlayer, contentType, null);
	}

	/**
	 * Creates a new audio player
	 * @param doCachePlayer caches the player even though the end of the media is reached
	 * @param contentType the type of the referenced media, this is being resolved to the phone's expected type automatically.
	 *        You can, for example, use the type "audio/mp3" and this method resolves the type to "audio/mpeg3", if this
	 *        is expected by the device.
	 * @param listener an optional PlayerListener
	 */
	public AudioPlayer(boolean doCachePlayer, String contentType, PlayerListener listener)
	{
		this.listener = listener;
		this.doCachePlayer = doCachePlayer;
		if (contentType != null) {
			if (!contentType.startsWith("audio/")) {
				contentType = "audio/" + contentType;
			}
			String correctContentType = getAudioType(contentType, null);
			if (correctContentType != null) {
				contentType = correctContentType;
			}
		}
		this.defaultContentType = contentType;
		
		//#if polish.android
		this.androidPlayer = new MediaPlayer();
		this.androidPlayer.setOnCompletionListener(this);
		this.androidPlayer.setOnPreparedListener(this);
		this.volumeControlStream = MidletBridge.instance.getVolumeControlStream();
		AudioManager audioManager = (AudioManager) MidletBridge.instance.getSystemService(Context.AUDIO_SERVICE);			
		this.androidMaxVolume = audioManager.getStreamMaxVolume(this.volumeControlStream);
		//#debug
		System.out.println("The maximum volume is '"+this.androidMaxVolume+"'");
		//#debug
		System.out.println("The maximum volume for music is "+audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		int currentAndroidVolume = audioManager.getStreamVolume(this.volumeControlStream);
		//#debug
		System.out.println("The current volume is '"+currentAndroidVolume+"'");
		this.userJ2MeLevel = (int) (100f / this.androidMaxVolume * currentAndroidVolume);
		//#debug
		System.out.println("The current J2Me volume is '"+this.userJ2MeLevel+"'");
		
		StreamingMp3Server server = new StreamingMp3Server();
		try {
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//#endif
	}
	
	public void stop() {
		//#if polish.android
		// TODO: This call could crash if it happens while the asynchonous prepare takes place.
		if(this.androidPlayer.isPlaying()) {
			this.androidPlayer.stop();
		}
		//#endif
	}
	
	/**
	 * Sets a player listener, replacing any previously registered listener.
	 * 
	 * @param listener the new listener or null
	 */
	public void setPlayerListener( PlayerListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Retrieves the currently registered player listener.
	 * @return the current player listener
	 */
	public PlayerListener getPlayerListener() {
		return this.listener;
	}

	/**
	 * This method will play the files with the filenames given in the parameter one after the other.
	 * This method is only available for the Android platform at the moment.
	 * @param filenames Must not be null and no array element must be null. Each filename must start with 'file://'. Normally you want
	 * to access files under the directory 'System.getProperty("fileconn.dir.private")'.
	 * @throws IOException
	 */
	public void streamMp3s(String[] filenames) throws IOException {
		//#if polish.android
		if(filenames == null) {
			throw new IllegalArgumentException("Parameter 'filenames' is null. It should be a reference to a String array.");
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("http://localhost:"+StreamingMp3Server.PORT_STREAMING_MP3+"/bla?");
		for (int i = 0; i < filenames.length; i++) {
			String filename = filenames[i];
			if(filename == null) {
				throw new IllegalArgumentException("The value at index position '"+i+"' is null. This must not happen.");
			}
			if(filename.startsWith("file://")) {
				filename = filename.substring("file://".length());
			}
			else {
				throw new IllegalArgumentException("The filename at index position '"+i+"' must start with 'file://'. Every filename must be on the local filesystem.");
			}
			try {
				filename = URLEncoder.encode(filename, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				// Unlikely.
				return;
			}
			buffer.append("file=");
			buffer.append(filename);
			if(i<filenames.length-1) {
				buffer.append("&");
			}
		}
		this.androidPlayer.reset();
		String url = buffer.toString();
//		this.androidPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			this.androidPlayer.setDataSource(url);
			this.androidPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// This is done asynchonously.
//		this.androidPlayer.start();
		//#endif
	}
	
	/**
	 * Plays the media taken from the specified URL.
	 * @param url the URL of the media 
	 * @param type the type of the referenced media, this is being resolved to the phone's expected type automatically.
	 *        You can, for example, use the type "audio/mp3" and this method resolves the type to "audio/mpeg3", if this
	 *        is expected by the device.
	 * @throws MediaException when the media is not supported
	 * @throws IOException when the URL cannot be resolved
	 */
	public void play(String url, String type) throws MediaException, IOException
	{
		//#if polish.android
		this.androidPlayer.reset();
		if(url.startsWith("file://")) {
			String path = url.substring("file://".length());
			File file = new File(path);
			if(!file.exists()) {
				throw new IOException("Could not find file at url '"+url+"'");
			}
			System.out.println("The file is "+file.getAbsolutePath());
			FileInputStream fileInputStream = new FileInputStream(file);
			FileDescriptor fileDescriptor;
			fileDescriptor = fileInputStream.getFD();
			// rickyn: Do not use setDataSource(String) as it does not work. Use setDataSource(FileDescriptor) instead.
			this.androidPlayer.setDataSource(fileDescriptor);
			fileInputStream.close();
		} else {
			int resourceID = ResourcesHelper.getResourceID(url);
			AssetFileDescriptor assetFileDescriptor = MidletBridge.instance.getResources().openRawResourceFd(resourceID);
			if(assetFileDescriptor == null) {
				throw new IOException("Could not retrieve AssetFileDescriptor for resource id '"+resourceID+"'");
			}
			FileDescriptor fileDescriptor;
			fileDescriptor = assetFileDescriptor.getFileDescriptor();
			this.androidPlayer.setDataSource(fileDescriptor);
			assetFileDescriptor.close();
		}
		this.androidPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.androidPlayer.prepare();
		this.androidPlayer.start();
		//#else
			InputStream in = getClass().getResourceAsStream(url);
			if (in == null) {
				throw new IOException("not found: " + url);
			}
			play(in, type);
		//#endif
		if(this.userJ2MeLevel != -1) {
			setVolumeLevel(this.userJ2MeLevel);
		}
	}

	/**
	 * Plays the media taken from the specified input stream.
	 * @param in the media input 
	 * @param type the type of the referenced media, this is being resolved to the phone's expected type automatically.
	 *        You can, for example, use the type "audio/mp3" and this method resolves the type to "audio/mpeg3", if this
	 *        is expected by the device.
	 * @throws MediaException when the media is not supported
	 * @throws IOException when the input cannot be read
	 */
	public void play(InputStream in, String type) throws MediaException, IOException 
	{
		String correctType = getAudioType(type, "file");
		if (correctType == null) {
			//#debug warn
			System.out.println("Unable to find correct type for " + type + " with the file protocol");
			correctType = getAudioType(type, null);
			if (correctType == null) {
				//#debug warn
				System.out.println("Unable to find correct type for " + type);
				correctType = type;
			}
		}
		//#if polish.android
			if (in instanceof ResourceInputStream) {
				this.androidPlayer = MediaPlayer.create(MidletBridge.instance, ((ResourceInputStream)in).getResourceId());
				this.androidPlayer.setOnCompletionListener(this);
				this.androidPlayer.start();
			} 
			//#if polish.debug.warn
				else {
					//#debug warn
					System.out.println("Unable to play input stream: input stream does not originate from a resource.");
				}
			//#endif
		//#else
			this.player = Manager.createPlayer(in, correctType);
			this.player.addPlayerListener(this);
			this.player.start();
		//#endif
		if(this.userJ2MeLevel != -1) {
			setVolumeLevel(this.userJ2MeLevel);
		}
	}

	/**
	 * Plays the media taken from the specified URL  with the content type specified in the constructor.
	 * @param url the URL of the media 
	 * @throws MediaException when the media is not supported
	 * @throws IOException when the URL cannot be resolved
	 */
	public void play(String url) throws MediaException, IOException 
	{
		//#if polish.android
		this.androidPlayer.reset();
		if(url.startsWith("file://")) {
			String path = url.substring("file://".length());
			File file = new File(path);
			if(!file.exists()) {
				throw new IOException("Could not find file at url '"+url+"'");
			}
			System.out.println("The file is "+file.getAbsolutePath());
			FileInputStream fileInputStream = new FileInputStream(file);
			FileDescriptor fileDescriptor;
			fileDescriptor = fileInputStream.getFD();
			// rickyn: Do not use setDataSource(String) as it does not work. Use setDataSource(FileDescriptor) instead.
			this.androidPlayer.setDataSource(fileDescriptor);
			fileInputStream.close();
		} else if(url.startsWith("http://")) {
				this.androidPlayer.setDataSource(url);
		} else {
			int resourceID = ResourcesHelper.getResourceID(url);
			AssetFileDescriptor assetFileDescriptor = MidletBridge.instance.getResources().openRawResourceFd(resourceID);
			if(assetFileDescriptor == null) {
				throw new IOException("Could not retrieve AssetFileDescriptor for resource id '"+resourceID+"'");
			}
			FileDescriptor fileDescriptor;
			fileDescriptor = assetFileDescriptor.getFileDescriptor();
			this.androidPlayer.setDataSource(fileDescriptor);
			assetFileDescriptor.close();
		}
		
		this.androidPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.androidPlayer.prepare();
		this.androidPlayer.start();
		//#else
			InputStream in = getClass().getResourceAsStream(url);
			if (in == null) {
				throw new IOException("not found: " + url);
			}
			play(in);
		//#endif
		if(this.userJ2MeLevel != -1) {
			setVolumeLevel(this.userJ2MeLevel);
		}
	}

	/**
	 * Plays the media taken from the specified input stream with the content type specified in the constructor.
	 * @param in the media input 
	 * @throws MediaException when the media is not supported
	 * @throws IOException when the input cannot be read
	 */
	public void play(InputStream in)
	throws MediaException, IOException 
	{
		//#if polish.android
			if (in instanceof ResourceInputStream) {
				this.androidPlayer = MediaPlayer.create(MidletBridge.instance, ((ResourceInputStream)in).getResourceId());
				this.androidPlayer.setOnCompletionListener(this);
				this.androidPlayer.start();
			} 
			//#if polish.debug.warn
				else {
					//#debug warn
					System.out.println("Unable to play input stream: input stream does not originate from a resource.");
				}
			//#endif
		//#else
			String correctType = this.defaultContentType;
			this.player = Manager.createPlayer(in, correctType);
			this.player.addPlayerListener(this);
			this.player.start();
		//#endif
			if(this.userJ2MeLevel != -1) {
				setVolumeLevel(this.userJ2MeLevel);
			}
	}

	/**
	 * Plays back the last media again. This can only be used when doCachePlayer
	 * is set to true in the constructor
	 * 
	 * @throws MediaException
	 *             when the player cannot be started
	 * @see #AudioPlayer(boolean)
	 * @see #AudioPlayer(boolean, String)
	 * @see #AudioPlayer(boolean, String, PlayerListener)
	 * @see #AudioPlayer(String)
	 */
	public void play() throws MediaException
	{
		//#if polish.android
			if (this.androidPlayer != null) {
				this.androidPlayer.start();
			}
		//#else
			if (this.player != null) {
				this.player.start();
			}
		//#endif
	}

	/**
	 * Returns the original player.
	 * 
	 * @return the original player, this can be null when no audio has been
	 *         played back so far.
	 */
	//#if !polish.android
	public Player getPlayer() {
		return this.player;
	}
	//#endif

	/**
	 * Helper function for getting a supported media type.
	 * 
	 * @param type the type like "audio/mp3"
	 * @param protocol
	 *            the protocol, when null is given the content type will be
	 *            returned for any protocol
	 * @return the type supported by the device, for example "audio/mpeg3" -
	 *         null when the given type is not supported by the device.
	 */
	public static String getAudioType(String type, String protocol) {		
		//#if !polish.android
		if (AUDIO_TYPES.size() == 0) {
			addTypes(new String[] { "audio/3gpp", "audio/3gp" });
			addTypes(new String[] { "audio/x-mp3", "audio/mp3", "audio/x-mp3",
					"audio/mpeg3", "audio/x-mpeg3", "audio/mpeg-3" });
			addTypes(new String[] { "audio/midi", "audio/x-midi", "audio/mid",
					"audio/x-mid", "audio/sp-midi" });
			addTypes(new String[] { "audio/wav", "audio/x-wav" });
			addTypes(new String[] { "audio/amr", "audio/x-amr" });
			addTypes(new String[] { "audio/mpeg4", "audio/mpeg-4", "audio/mp4",
					"audio/mp4a-latm" });
			addTypes(new String[] { "audio/imelody", "audio/x-imelody",
					"audio/imy", "audio/x-imy" });
		}
		String[] supportedContentTypes = Manager
				.getSupportedContentTypes(protocol);
		if (supportedContentTypes == null || supportedContentTypes.length == 0) {
			return null;
		}
		Hashtable mappings = (Hashtable) AUDIO_TYPES.get(type);
		if (mappings == null) {
			//#debug warn
			System.out.println("The audio content type " + type
					+ " has no known synonyms.");
			for (int i = 0; i < supportedContentTypes.length; i++) {
				String contentType = supportedContentTypes[i];
				if (contentType.equals(type)) {
					return type;
				}
			}
		} else {
			for (int i = 0; i < supportedContentTypes.length; i++) {
				String contentType = supportedContentTypes[i];
				if (mappings.containsKey(contentType)) {
					return contentType;
				}
			}
		}
		//#endif
		return null;
	}
	
	/**
	 * Determines whether the given audio format is supported by this device for the specified protocol.
	 * @param type the type like "audio/mp3"
	 * @param protocol
	 *            the protocol, when null is given the content type will be
	 *            returned for any protocol
	 * @return true when the given audio type is supported
	 */
	public static boolean isSupportedAudioType( String type, String protocol ) {
		return getAudioType(type, protocol) != null;
	}

	/**
	 * Determines if the audio player is currently playing music
	 * @return true when audio is played back
	 */
	public boolean isPlaying() {
		//#if polish.android
			if (this.androidPlayer != null) {
				return this.androidPlayer.isPlaying();
			}
		//#else
			if (this.player != null) {
				return this.player.getState() == Player.STARTED;
			}
		//#endif
		return false;
	}

	private static void addTypes(String[] types) {
		Hashtable nestedMap = new Hashtable();
		for (int i = 0; i < types.length; i++) {
			String type = types[i];
			nestedMap.put(type, type);
			AUDIO_TYPES.put(type, nestedMap);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.media.PlayerListener#playerUpdate(javax.microedition.media.Player,
	 *      java.lang.String, java.lang.Object)
	 */
	public void playerUpdate(Player p, String event, Object data) {

		if (this.listener != null) {
			this.listener.playerUpdate(p, event, data);
		}
		if (!this.doCachePlayer && PlayerListener.END_OF_MEDIA.equals(event)) {
			//#if !polish.android
				p.removePlayerListener(this);
			//#endif
//			cleanUpPlayer();
		}
	}

	/**
	 * Closes and deallocates the player.
	 */
	public void cleanUpPlayer() {
		this.userJ2MeLevel = -1;
		//#if !polish.android
			if (this.player != null) {
				this.player.deallocate();
				this.player.close(); // necessary for some Motorola devices
				this.player = null;
			}
		//#else
			if (this.androidPlayer != null) {
				this.androidPlayer.release();
		 		this.androidPlayer = null;
			}
		//#endif
	}
	/**
	 * Gets the volume using a linear point scale with values between 0 and 100.
	 * 0 is silence; 100 is the loudest useful level that this VolumeControl supports. If the given level is less than 0 or greater than 100, the level will be set to 0 or 100 respectively.
	 * When setLevel results in a change in the volume level, a VOLUME_CHANGED event will be delivered through the PlayerListener. 
	 * @return the volume level between 0 and 100 or -1 when the player is not initialized
	 */
	public int getVolumeLevel() {
		int volume;
		//#if polish.android
			volume = this.userJ2MeLevel;
		//#else
			Player pl = this.player;
			if (pl != null) {
				VolumeControl volumeControl = (VolumeControl) pl.getControl("VolumeControl");
				if (volumeControl != null) {
					return volumeControl.getLevel();
				}
			}
			volume = this.userJ2MeLevel;
		//#endif
		return volume;
	}
	
	//TODO: The volume setup is a mess on android. 1) Map the keys 24/25 to volume change methods. 2) Find the right way to set the volume of individual streams.
	
	/**
	 * Sets the volume using a linear point scale with values between 0 and 100.
	 * 0 is silence; 100 is the loudest useful level that this VolumeControl supports. If the given level is less than 0 or greater than 100, the level will be set to 0 or 100 respectively.
	 * When setLevel results in a change in the volume level, a VOLUME_CHANGED event will be delivered through the PlayerListener.
	 *  
	 * @param j2MeLevel the volume level between 0 and 100
	 */
	public void setVolumeLevel(int j2MeLevel) {
		int boundJ2MeLevel = boundJ2MeLevel(j2MeLevel);
		this.userJ2MeLevel = boundJ2MeLevel;
		//#debug
		System.out.println("The current J2Me volume is '"+this.userJ2MeLevel+"'");
		//#if polish.android
			AudioManager audioManager = (AudioManager) MidletBridge.instance.getSystemService(Context.AUDIO_SERVICE);			
			int androidLevel = (int)(this.androidMaxVolume / 100f * boundJ2MeLevel);
			//#debug
			System.out.println("The android volume is set to '"+androidLevel+"'");
			audioManager.setStreamVolume(this.volumeControlStream,androidLevel,0);
//			if (this.androidPlayer != null) {
//				this.androidPlayer.setVolume(androidLevel,androidLevel);
//			}
		//#else
			Player pl = this.player;
			if (pl != null) {
				VolumeControl volumeControl = (VolumeControl) pl.getControl("VolumeControl");
				if (volumeControl != null) {
					volumeControl.setLevel(100);
					return;
				}
			}
		//#endif
	}
	
	/**
	 * Detects the the player is currently muted
	 * @return true when the player is muted
	 */
	public boolean isMuted() {
		int level = getVolumeLevel();
		return level == 0;
	}
	
	/**
	 * Mutes the player or restores the previous volume level
	 * @param mute true when the player should be muted, false when the previous volume level should be restored
	 */
	public void setMute( boolean mute ) {
		if (mute) {
			this.previousVolumeLevel = getVolumeLevel();
			setVolumeLevel(0);
		} else if (this.previousVolumeLevel != -1){
			setVolumeLevel(this.previousVolumeLevel);
		}
	}

	private int boundJ2MeLevel(int aJ2MeLevel) {
		if (aJ2MeLevel < 0) {
			aJ2MeLevel = 0;
		} else if (aJ2MeLevel > 100) {
			aJ2MeLevel = 100;
		}
		return aJ2MeLevel;
	}

	//#if polish.android
	/**
	 * Informs the audio player about a finished media on Android devices.
	 * @param mp the media player (should be the same as this.mediaPlayer)
	 */
	public void onCompletion(MediaPlayer mp) {
		playerUpdate( this.player, PlayerListener.END_OF_MEDIA, null );
	}
	
	public void onPrepared(MediaPlayer p) {
		//#debug
		System.out.println("onPrepared called.");
		p.start();
//		this.androidPlayer.start();
	}
	
	//#endif
	
	/**
	 * This method is part of a two-phase playback. The first phase is to prepare the content, the second phase plays it. This way
	 * gaps in the playback of subsequent contents are minimized.
	 * This method will only prepare the audio content given by the url. You can play this content with the {@link #play()} method.
	 * This method is only available on android at the moment.
	 * @param url
	 * @throws IOException
	 */
	public void prepare(String url) throws IOException {
		//#if polish.android
		this.androidPlayer.reset();
		if(url.startsWith("file://")) {
			String path = url.substring("file://".length());
			File file = new File(path);
			if(!file.exists()) {
				throw new IOException("Could not find file at url '"+url+"'");
			}
			System.out.println("The file is "+file.getAbsolutePath());
			FileInputStream fileInputStream = new FileInputStream(file);
			FileDescriptor fileDescriptor;
			fileDescriptor = fileInputStream.getFD();
			// rickyn: Do not use setDataSource(String) as it does not work. Use setDataSource(FileDescriptor) instead.
			this.androidPlayer.setDataSource(fileDescriptor);
			fileInputStream.close();
		} else {
			int resourceID = ResourcesHelper.getResourceID(url);
			AssetFileDescriptor assetFileDescriptor = MidletBridge.instance.getResources().openRawResourceFd(resourceID);
			if(assetFileDescriptor == null) {
				throw new IOException("Could not retrieve AssetFileDescriptor for resource id '"+resourceID+"'");
			}
			FileDescriptor fileDescriptor;
			fileDescriptor = assetFileDescriptor.getFileDescriptor();
			this.androidPlayer.setDataSource(fileDescriptor);
			assetFileDescriptor.close();
		}
		this.androidPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.androidPlayer.prepare();
		//#endif
	}

}
