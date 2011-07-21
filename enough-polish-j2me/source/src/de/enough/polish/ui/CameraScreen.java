//#condition polish.usePolishGui && polish.api.mmapi && polish.api.amms
/*
 * Created on Sep 8, 2006 at 5:00:10 PM.
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
package de.enough.polish.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.amms.control.FormatControl;
import javax.microedition.amms.control.ImageFormatControl;
import javax.microedition.amms.control.camera.CameraControl;
import javax.microedition.amms.control.camera.ExposureControl;
import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.FocusControl;
import javax.microedition.amms.control.camera.SnapshotControl;
import javax.microedition.amms.control.camera.ZoomControl;
import javax.microedition.amms.control.imageeffect.WhiteBalanceControl;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;

import de.enough.polish.camera.CameraScreenEvent;
import de.enough.polish.camera.CameraScreenListener;
import de.enough.polish.camera.CameraUtil;
import de.enough.polish.camera.CameraResolution;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.DeviceInfo;
import de.enough.polish.util.TextUtil;

/**
 * <p>
 * A convenience screen for taking snapshots. This screen requires support of
 * the MMAPI by the current target device!
 * </p>
 * 
 * <pre>
 * //#if polish.api.mmapi
 *    import de.enough.polish.ui.SnapshotScreen;
 * //#endif
 * ...
 * //#if polish.api.mmapi
 *    //#style snapshotScreen
 *    SnapshotScreen screen = new SnapshotScreen(&quot;Snapshot&quot;);
 * //#endif
 * </pre>
 * 
 * <p>
 * Copyright Enough Software 2006 - 2009
 * </p>
 * 
 * <pre>
 * history
 *        Sep 8, 2006 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CameraScreen extends SnapshotScreen implements Runnable,
		PlayerListener {

	private Player player;
	private VideoControl videoControl;
	private boolean cameraOverlay = false;
	private boolean isSnapshotInProgress;
	//#if polish.api.advancedmultimedia
	private boolean isAutofocusEnabled = true;
	//#endif
	private Command cmdCapture;
	private boolean isInitializing;
	private CameraControl cameraControl;
	private ExposureControl exposureControl;
	private FlashControl flashControl;
	private FocusControl focusControl;
	private SnapshotControl snapshotControl;
	private ZoomControl zoomControl;
	private RecordControl recordControl;
	private ImageFormatControl imageFormatControl;

	private WhiteBalanceControl whiteBalanceControl;

	private String burstFilePrefix;

	private String burstFileSuffix;

	private String burstFileDirectory;

	private String snapshotFormat = null;
	private String videoCaptureFormat = null;

	private boolean isIntialized = false;
	private boolean isRecording = false;
	private boolean videoCapturing = false;
	private boolean autoScale = true;

	private ArrayList observers = new ArrayList();

	private long startRecordTime;

	private boolean readyToScale = false;

	//#if polish.group.SE-JavaPlatform8 
	//private DisplayModeControl displayModeControl;

	//#endif

	/**
	 * Creates a new screen for taking screenshots.
	 * 
	 * @param title
	 *            the title of the screen
	 */
	public CameraScreen(String title, boolean cameraOverlay,
			boolean videoCapturing, boolean autoScale, String burstFilePrefix,
			String burstFileSuffix, String burstFileDirectory) {
		this(title);
		this.autoScale = autoScale;
		this.burstFilePrefix = burstFilePrefix;
		this.burstFileSuffix = burstFileSuffix;
		this.burstFileDirectory = burstFileDirectory;
		this.cameraOverlay = cameraOverlay;
		this.videoCapturing = videoCapturing;
	}

	public CameraScreen(String title, boolean cameraOverlay,
			boolean videoCapturing, boolean autoScale, String burstFilePrefix,
			String burstFileSuffix, String burstFileDirectory, Style style) {
		super(title, style);

		this.autoScale = autoScale;
		this.burstFilePrefix = burstFilePrefix;
		this.burstFileSuffix = burstFileSuffix;
		this.burstFileDirectory = burstFileDirectory;
		this.cameraOverlay = cameraOverlay;
		this.videoCapturing = videoCapturing;
	}

	public CameraScreen(String title) {
		this(title, null);
	}

	/**
	 * Creates a new screen for taking screenshots.
	 * 
	 * @param title
	 *            the title of the screen
	 * @param style
	 *            the style
	 */
	public CameraScreen(String title, Style style) {
		super(title, style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.Screen#hideNotify()
	 */
	public void hideNotify() {
		super.hideNotify();
		//		if (!this.isSnapshotInProgress) {
		//			stopSnapshot(false);
		//		}
	}

	public void run() {
		try {
			initPlayer();
		} catch (MediaException e) {
			// ignore
		}
	}

	public CameraResolution[] getVideoCaptureResolutions() {
		if (this.cameraControl != null) {
			int[] resolution = this.cameraControl
					.getSupportedVideoResolutions();
			CameraResolution[] videoResolution = new CameraResolution[resolution.length / 2];

			for (int i = 0; i < videoResolution.length; i++) {
				int iTemp = i * 2;
				videoResolution[i] = new CameraResolution();
				videoResolution[i].width = resolution[iTemp];
				videoResolution[i].height = resolution[iTemp + 1];
			}
			return videoResolution;
		}

		return null;

	}

	public CameraResolution getSnapshotResolution() {
		if (this.cameraControl != null) {
			int i = this.cameraControl.getStillResolution();
			if (i != -1) {
				return CameraUtil.getSnapshotResolutions()[i];
			}
		}
		return null;
	}

	public CameraResolution getVideoCaptureResolution() {
		if (this.cameraControl != null) {
			int i = this.cameraControl.getVideoResolution();
			if (i != -1) {
				return this.getVideoCaptureResolutions()[i];
			}
		}
		return null;
	}

	public void setSnapshotResolution(int width, int height) {
		if (this.cameraControl != null) {
			int[] stillResolutions = this.cameraControl
					.getSupportedStillResolutions();
			for (int i = 0; i < stillResolutions.length; i += 2) {
				if (stillResolutions[i] == width
						&& stillResolutions[i + 1] == height) {
					this.cameraControl.setStillResolution(i / 2);
				}
			}
		}
	}

	public void setSnapshotResolution(CameraResolution dim) {
		int width = dim.width;
		int height = dim.height;
		if (this.cameraControl != null) {
			int[] stillResolutions = this.cameraControl
					.getSupportedStillResolutions();
			for (int i = 0; i < stillResolutions.length; i += 2) {
				if (stillResolutions[i] == width
						&& stillResolutions[i + 1] == height) {
					this.cameraControl.setStillResolution(i / 2);
				}
			}
		}
	}

	public void setVideoResolution(int width, int height) {
		if (this.cameraControl != null) {
			int[] videoResolutions = this.cameraControl
					.getSupportedVideoResolutions();
			for (int i = 0; i < videoResolutions.length; i += 2) {
				if (videoResolutions[i] == width
						&& videoResolutions[i + 1] == height) {
					this.cameraControl.setVideoResolution(i / 2);
				}
			}
		}
	}

	public void setVideoResolution(CameraResolution dim) {
		int width = dim.width;
		int height = dim.height;
		if (this.cameraControl != null) {
			int[] videoResolutions = this.cameraControl
					.getSupportedVideoResolutions();
			for (int i = 0; i < videoResolutions.length; i += 2) {
				if (videoResolutions[i] == width
						&& videoResolutions[i + 1] == height) {
					this.cameraControl.setVideoResolution(i / 2);
				}
			}
		}
	}

	/**
	 * Retrieves the default resolution.
	 * 
	 * @return the default resolution as a point object - x represents the
	 *         width, y the height. This can be null if no resolution is
	 *         specified in "video.snapshot.encodings"!
	 */
	public static CameraResolution getDefaultResolution() {
		String encoding = System.getProperty("video.snapshot.encodings");
		if (encoding == null) {
			return null;
		}
		int splitPos = encoding.indexOf(' ');
		if (splitPos != -1) {
			encoding = encoding.substring(0, splitPos);
		}
		int widthIndex = encoding.indexOf("width=");
		if (widthIndex != -1) {
			splitPos = encoding.indexOf('&', widthIndex + 1);
			String width;
			if (splitPos != -1) {
				width = encoding.substring(widthIndex + "width=".length(),
						splitPos);
			} else {
				width = encoding.substring(widthIndex + "width=".length());
			}
			int heightIndex = encoding.indexOf("height=");
			if (heightIndex != -1) {
				splitPos = encoding.indexOf('&', heightIndex + 1);
				String height;
				if (splitPos == -1) {
					height = encoding.substring(heightIndex
							+ "height=".length());
				} else {
					height = encoding.substring(heightIndex
							+ "height=".length(), splitPos);
				}
				return new CameraResolution(Integer.parseInt(width), Integer
						.parseInt(height));
			}
		} else {
			int middlePos = encoding.indexOf('x');
			if (middlePos != -1) {
				int start = middlePos - 1;
				int end = middlePos + 1;
				while (start >= 0 && Character.isDigit(encoding.charAt(start))) {
					start--;
				}
				if (start != 0) {
					start++;
				}
				while (end < encoding.length()
						&& Character.isDigit(encoding.charAt(end))) {
					end++;
				}
				if (end != encoding.length() - 1) {
					end--;
				}
				if (start != middlePos && end != middlePos) {
					String resolution = encoding.substring(start, end + 1);
					splitPos = resolution.indexOf('x');
					return new CameraResolution(Integer.parseInt(resolution
							.substring(0, splitPos)), Integer
							.parseInt(resolution.substring(splitPos + 1)));
				}
			}
		}
		return null;
	}

	/**
	 * Determines the best fitting encoding for reaching snapshots of at least
	 * the specified dimensions
	 * 
	 * @param width
	 *            the minimum width, e.g. 800
	 * @param height
	 *            the minum height, e.g. 640
	 * @return an array of suitable encodings, when format is specified this
	 *         will typically only contain one element; the array will be empty
	 *         (but not null) when no suitable encoding is found
	 */
	public static String[] getSnapshotEncodingsWithResolutionCloseTo(int width,
			int height) {
		return getSnapshotEncodingsWithResolutionCloseTo(width, height, false,
				false, null);
	}

	/**
	 * Determines the best fitting encoding
	 * 
	 * @param format
	 *            the format, e.g. jpg
	 * @param width
	 *            the desired width, e.g. 800
	 * @param height
	 *            the desired height, e.g. 640
	 * @param needsToBeLarger
	 *            true when the found encodings needs to be the same size or
	 *            larger than the specified dimensions
	 * @param needsToBeSmaller
	 *            true when the found encodings needs to be the same size or
	 *            smaller than the specified dimensions
	 * @return an array of suitable encodings, when format is specified this
	 *         will typically only contain one element; the array will be empty
	 *         (but not null) when no suitable encoding is found
	 */
	public static String[] getSnapshotEncodingsWithResolutionCloseTo(int width,
			int height, boolean needsToBeLarger, boolean needsToBeSmaller,
			String format) {
		CameraResolution[] resolutions = CameraUtil.getSnapshotResolutions();
		String defaultEncoding;
		if (format != null) {
			defaultEncoding = "encoding=" + format + "&width=" + width
					+ "&height=" + height;
		} else {
			defaultEncoding = "encoding=jpeg&width=" + width + "&height="
					+ height;
		}
		if (resolutions.length == 0) {
			return new String[] { defaultEncoding };
		}
		int minDistance = Integer.MAX_VALUE;
		int closestWidth = 0;
		int closestHeight = 0;
		for (int i = 0; i < resolutions.length; i++) {
			CameraResolution dimension = resolutions[i];
			int resWidth = dimension.width;
			int resHeight = dimension.height;
			if ((needsToBeLarger && (resWidth < width || resHeight < height))
					|| (needsToBeSmaller && (resWidth > width || resHeight > height))) {
				continue;
			}
			int distance = Math.abs(width - resWidth)
					+ Math.abs(height - resHeight);
			if (distance < minDistance) {
				closestWidth = resWidth;
				closestHeight = resHeight;
			}
		}
		if (minDistance == Integer.MAX_VALUE) {
			return new String[] { defaultEncoding };
		}
		String[] encodings = CameraUtil.getSnapshotEncodings();
		if (format != null) {
			format = format.toLowerCase();
		}
		ArrayList bestEncodings = new ArrayList();
		String closestWidthStr = Integer.toString(closestWidth);
		String closestHeightStr = Integer.toString(closestHeight);
		for (int i = 0; i < encodings.length; i++) {
			String encoding = encodings[i];
			if (format == null || encoding.toLowerCase().indexOf(format) != -1) {
				if (encoding.indexOf(closestWidthStr) != -1
						&& encoding.indexOf(closestHeightStr) != -1) {
					bestEncodings.add(encoding);
				}
			}
		}
		if (bestEncodings.size() == 0) {
			return new String[] { defaultEncoding };
		}
		encodings = (String[]) bestEncodings.toArray(new String[bestEncodings
				.size()]);
		Arrays.sort(encodings);
		return encodings;
	}

	/**
	 * Tries to determine the available formats from the supported encodings
	 * 
	 * @return an array of detected formats (in lower case), may be emtpy but
	 *         not null
	 */
	public static String[] getSnapshotFormats() {
		String[] encodings = CameraUtil.getSnapshotEncodings();
		ArrayList formats = new ArrayList();
		boolean pngAdded = false;
		boolean jpegAdded = false;
		boolean bmpAdded = false;
		boolean gifAdded = false;
		for (int i = 0; i < encodings.length; i++) {
			String encoding = encodings[i].toLowerCase();
			if (!pngAdded && encoding.indexOf("png") != -1) {
				formats.add("png");
				pngAdded = true;
			} else if (!jpegAdded && encoding.indexOf("jpg") != -1) {
				formats.add("jpg");
				jpegAdded = true;
			} else if (!jpegAdded && encoding.indexOf("jpeg") != -1) {
				formats.add("jpeg");
				jpegAdded = true;
			} else if (!gifAdded && encoding.indexOf("gif") != -1) {
				formats.add("gif");
				gifAdded = true;
			} else if (!bmpAdded && encoding.indexOf("bmp") != -1) {
				formats.add("bmp");
				bmpAdded = true;
			}
		}

		return (String[]) formats.toArray(new String[formats.size()]);
	}

	public String getSnapshotFormat() {
		if (this.snapshotFormat == null) {
			return "png";
		}
		return this.snapshotFormat;
	}

	public void setSnapshotFormat(String format) {
		if (this.imageFormatControl != null && format != null) {
			this.imageFormatControl.setFormat(format);
		}
	}

	public void setSnapshotParameterVersionType(String parameter) {
		if (this.imageFormatControl != null && parameter != null) {
			this.imageFormatControl.setParameter(
					FormatControl.PARAM_VERSION_TYPE, parameter);
		}
	}

	public void setSnapshotParameterQuality(int parameter) {
		if (this.imageFormatControl != null) {
			this.imageFormatControl.setParameter(FormatControl.PARAM_QUALITY,
					parameter);
		}
	}

	public void setSnapshotMetadata(String metadataKey, String description)
			throws MediaException {
		if (this.imageFormatControl != null && metadataKey != null
				&& description != null) {

			this.imageFormatControl.setMetadata(metadataKey, description);

		}

	}

	public void setSnapshotMetadataOverride(boolean override) {
		if (this.imageFormatControl != null) {

			this.imageFormatControl.setMetadataOverride(override);

		}

	}

	public boolean getSnapshotMetadataOverride() {
		if (this.imageFormatControl != null) {

			return this.imageFormatControl.getMetadataOverride();

		}
		return false;
	}

	public String[] getSnapshotMetadataKeys() {
		if (this.imageFormatControl != null) {
			return this.imageFormatControl.getSupportedMetadataKeys();
		}
		return null;
	}

	public int getSnapshotEstimateSize() {
		if (this.imageFormatControl != null) {
			return this.imageFormatControl.getEstimatedImageSize();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public String[] getVideoCaptureFormats() {
		String supportedEncodingsStr = System.getProperty("video.encodings");
		if (supportedEncodingsStr == null) {
			return new String[0];
		}
		String[] encodings = TextUtil.splitAndTrim(supportedEncodingsStr, ' ');
		Arrays.sort(encodings);

		ArrayList formats = new ArrayList();

		boolean h263Added = false;
		boolean mp4vesAdded = false;
		boolean mpegAdded = false;
		boolean h2632000Added = false;
		boolean mpeg4Added = false;
		boolean mp4Added = false;
		boolean h264Added = false;

		for (int i = 0; i < encodings.length; i++) {
			String encoding = encodings[i].toLowerCase();
			if (!h263Added && encoding.indexOf("h263") != -1) {
				formats.add("h263");
				h263Added = true;
			} else if (!mp4vesAdded && encoding.indexOf("mp4v-es") != -1) {
				formats.add("mp4v-es");
				mp4vesAdded = true;
			} else if (!mpegAdded && encoding.indexOf("mpeg") != -1) {
				formats.add("mpeg");
				mpegAdded = true;
			} else if (!h2632000Added && encoding.indexOf("h263-2000") != -1) {
				formats.add("h263-2000");
				h2632000Added = true;
			} else if (!mpeg4Added && encoding.indexOf("mpeg4") != -1) {
				formats.add("mpeg4");
				mpeg4Added = true;
			} else if (!mp4Added && encoding.indexOf("mp4") != -1) {
				formats.add("mp4");
				mp4Added = true;
			} else if (!h264Added && encoding.indexOf("h264") != -1) {
				formats.add("h264");
				h264Added = true;
			}

		}
		return (String[]) formats.toArray(new String[formats.size()]);

	}

	public String getVideoCaptureFormat() {
		return this.videoCaptureFormat;
	}

	public void setVideoCaptureFormat(String name) {
		String[] formats = this.getVideoCaptureFormats();
		for (int i = 0; i < formats.length; i++) {
			if (formats[i].equals(name)) {
				this.videoCaptureFormat = name;
				return;
			}
		}
	}

	public boolean isVideoCaptureSupported() {
		if (System.getProperty("supports.video.capture").equals("true"))
			return true;
		else
			return false;

	}

	public boolean isSnapshotCaptureSupported() {
		if (System.getProperty("video.snapshot.encodings") != null) {
			return true;
		} else
			return false;
	}

	public void setFocusMacro(boolean enable) {
		if (focusControl != null) {
			if (focusControl.isMacroSupported()) {
				try {
					if (this.focusControl.getMacro() != enable) {
						this.focusControl.setMacro(enable);
					}
				} catch (MediaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setFocus(int distance) {
		if (this.focusControl != null) {
			if (distance == FocusControl.AUTO) {
				if (this.focusControl.isAutoFocusSupported()) {
					try {
						this.notifyObservers(new CameraScreenEvent(
								CameraScreenEvent.START_AUTO_FOUCS));
						this.focusControl.setFocus(FocusControl.AUTO);
						this.notifyObservers(new CameraScreenEvent(
								CameraScreenEvent.END_AUTO_FOUCS));
					} catch (MediaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					try {
						this.focusControl.setFocus(distance);

					} catch (MediaException e) {
						//#debug
						System.out.println("distance could not be set");
					}

			}
		}
	}

	public int getFocus() {
		if (this.focusControl != null) {
			return this.focusControl.getFocus();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getFocusSteps() {
		if (this.focusControl != null) {
			return this.focusControl.getFocusSteps();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public boolean getFocusMacro() {
		if (this.focusControl != null) {
			return this.focusControl.getMacro();
		}
		return false;
	}

	public int getFocusMin() {
		if (this.focusControl != null) {
			return this.focusControl.getMinFocus();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public boolean isFocusAutoSupported() {
		if (this.focusControl != null) {
			return this.focusControl.isAutoFocusSupported();
		}
		return false;
	}

	public boolean isFocusMacroSupported() {
		if (this.focusControl != null) {
			return this.focusControl.isMacroSupported();
		}
		return false;
	}

	public boolean isFocusManualSupported() {
		if (this.focusControl != null) {
			return this.focusControl.isManualFocusSupported();
		}
		return false;
	}

	public boolean isDigitalZoomSupported() {
		if (this.zoomControl != null) {
			if (this.zoomControl.getMaxDigitalZoom() == 100)
				return false;
			else
				return true;
		}
		return false;
	}

	public boolean isOpticalZoomSupported() {
		if (this.zoomControl != null) {
			if (this.zoomControl.getMaxOpticalZoom() == 100)
				return false;
			else
				return true;
		}
		return false;
	}

	public int getDigitalZoom() {
		if (this.zoomControl != null) {
			return this.zoomControl.getDigitalZoom();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getDigitalZoomLevels() {
		if (this.zoomControl != null) {
			return this.zoomControl.getDigitalZoomLevels();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getMaxDigitalZoom() {
		if (this.zoomControl != null) {
			return this.zoomControl.getMaxDigitalZoom();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getMaxOpticalZoom() {
		if (this.zoomControl != null) {
			return this.zoomControl.getMaxOpticalZoom();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getMinFocalLength() {
		if (this.zoomControl != null) {
			return this.zoomControl.getMinFocalLength();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getFocalLength() {
		if (this.zoomControl != null) {
			if (this.isOpticalZoomSupported()) {
				return getOpticalZoom() * getMinFocalLength() / 100;
			}
		}
		return CameraUtil.Feature_NOT_SUPPORTED;

	}

	public int getOpticalZoom() {
		if (this.zoomControl != null) {
			return this.zoomControl.getOpticalZoom();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getOpticalZoomLevels() {
		if (this.zoomControl != null) {
			return this.zoomControl.getOpticalZoomLevels();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int setDigitalZoom(int level) {
		if (this.zoomControl != null) {
			if (level % this.getDigitalZoomLevelFactor() == 0 && level >= 100
					&& level <= this.getMaxDigitalZoom())
				return this.zoomControl.setDigitalZoom(level);
		}
		return CameraUtil.Feature_NOT_SUPPORTED;

	}

	public int getDigitalZoomLevelFactor() {
		if (this.zoomControl != null) {
			int factor = (this.zoomControl.getMaxDigitalZoom() - 100)
					/ (this.getDigitalZoomLevels() - 1);
			return factor;
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getOpticalZoomLevelFactor() {
		if (this.zoomControl != null) {
			int factor = (this.zoomControl.getMaxOpticalZoom() - 100)
					/ (this.getOpticalZoomLevels() - 1);
			return factor;
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int setOpticalZoom(int level) {
		if (this.zoomControl != null) {
			if (level % this.getOpticalZoomLevelFactor() == 0 && level >= 100
					&& level <= this.getMaxOpticalZoom())
				return this.zoomControl.setOpticalZoom(level);
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public boolean isFlashSupported() {
		if (this.flashControl != null)
			return true;
		else
			return false;
	}

	public int getFlashMode() {
		if (this.flashControl != null) {
			return this.flashControl.getMode();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public void setFlashMode(int mode) {
		if (this.flashControl != null && this.isThisFlashModeSupported(mode)) {
			this.flashControl.setMode(mode);
		}
	}

	public boolean isThisFlashModeSupported(int mode) {
		if (this.flashControl != null) {
			int[] flashModes = this.getSupportedFlashModes();
			for (int i = 0; i < flashModes.length; i++) {
				if (mode == flashModes[i])
					return true;
			}
		}
		return false;
	}

	public boolean isFlashReady() {
		if (this.flashControl != null) {
			return this.flashControl.isFlashReady();
		}
		return false;
	}

	public int[] getSupportedFlashModes() {
		if (this.flashControl != null) {
			return this.flashControl.getSupportedModes();
		}
		return new int[] { CameraUtil.Feature_NOT_SUPPORTED };
	}

	public boolean isExporsureSupported() {
		if (this.exposureControl != null)
			return true;
		else
			return false;
	}

	public int getExposureCompensation() {
		if (this.exposureControl != null) {
			return this.exposureControl.getExposureCompensation();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getExposureTime() {
		if (this.exposureControl != null) {
			return this.exposureControl.getExposureTime();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getExposureValue() {
		if (this.exposureControl != null) {
			return this.exposureControl.getExposureValue();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getExposureFStop() {
		if (this.exposureControl != null) {
			return this.exposureControl.getFStop();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getExposureISO() {
		if (this.exposureControl != null) {
			return this.exposureControl.getISO();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public String getExposureLightMetering() {
		if (this.exposureControl != null) {
			return this.exposureControl.getLightMetering();
		}
		return "ExposureControl is not supported";
	}

	public int getExposureMaxExposureTime() {
		if (this.exposureControl != null) {
			return this.exposureControl.getMaxExposureTime();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int getExposureMinExposureTime() {
		if (this.exposureControl != null) {
			return this.exposureControl.getMinExposureTime();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public int[] getSupportedExposureCompensations() {
		if (this.exposureControl != null) {
			return this.exposureControl.getSupportedExposureCompensations();
		}
		return new int[] { CameraUtil.Feature_NOT_SUPPORTED };
	}

	public int[] getSupportedExposureFStops() {
		if (this.exposureControl != null) {
			return this.exposureControl.getSupportedFStops();
		}
		return new int[] { CameraUtil.Feature_NOT_SUPPORTED };
	}

	public int[] getSupportedExposureISOs() {
		if (this.exposureControl != null) {
			return this.exposureControl.getSupportedISOs();
		}
		return new int[] { CameraUtil.Feature_NOT_SUPPORTED };
	}

	public String[] getSupportedExposureLightMeterings() {
		if (this.exposureControl != null) {
			return this.exposureControl.getSupportedLightMeterings();
		}
		return new String[] { "ExposureControl is not supported" };
	}

	public boolean isThisExposureISOSupported(int value) {
		if (this.exposureControl != null) {
			int[] supportedISOs = this.exposureControl.getSupportedISOs();
			for (int i = 0; i < supportedISOs.length; i++) {
				if (supportedISOs[i] == value)
					return true;
			}
		}
		return false;
	}

	public boolean isThisExposureLightMeteringSupported(String value) {
		if (this.exposureControl != null) {
			String[] supportedLightMeterings = this.exposureControl
					.getSupportedLightMeterings();
			for (int i = 0; i < supportedLightMeterings.length; i++) {
				if (supportedLightMeterings[i].equals(value))
					return true;
			}
		}
		return false;
	}

	public boolean isThisExposureCompensationSupported(int value) {
		if (this.exposureControl != null) {
			int[] supportedCompensations = this.exposureControl
					.getSupportedExposureCompensations();
			for (int i = 0; i < supportedCompensations.length; i++) {
				if (supportedCompensations[i] == value)
					return true;
			}
		}
		return false;
	}

	public boolean isThisExposureFStopSupported(int value) {
		if (this.exposureControl != null) {
			int[] supportedFStops = this.exposureControl.getSupportedFStops();
			for (int i = 0; i < supportedFStops.length; i++) {
				if (supportedFStops[i] == value)
					return true;
			}
		}
		return false;
	}

	public int getExposureCompansationLevel() {
		if (this.exposureControl != null) {
			int[] supportedCompensations = this
					.getSupportedExposureCompensations();
			for (int i = 0; i < supportedCompensations.length; i++) {
				if (supportedCompensations[i] == this.getExposureCompensation())
					return i;
			}
			return CameraUtil.Feature_NOT_SUPPORTED;
		}
		return CameraUtil.Feature_NOT_SUPPORTED;

	}

	public int getExposureFStopLevel() {
		if (this.exposureControl != null) {
			int[] supportedFStops = this.getSupportedExposureFStops();
			for (int i = 0; i < supportedFStops.length; i++) {
				if (supportedFStops[i] == this.getExposureFStop())
					return i;
			}
			return CameraUtil.Feature_NOT_SUPPORTED;
		}
		return CameraUtil.Feature_NOT_SUPPORTED;

	}

	public int getExposureISOLevel() {
		if (this.exposureControl != null) {
			int[] supportedISOs = this.getSupportedExposureISOs();
			for (int i = 0; i < supportedISOs.length; i++) {
				if (supportedISOs[i] == this.getExposureISO())
					return i;
			}
			return CameraUtil.Feature_NOT_SUPPORTED;
		}
		return CameraUtil.Feature_NOT_SUPPORTED;

	}

	public void setExposureCompensation(int ec) {
		if (this.exposureControl != null
				&& this.isThisExposureCompensationSupported(ec)) {
			try {
				this.exposureControl.setExposureCompensation(ec);
			} catch (MediaException e) {
				//#debug
				System.out
						.println("cannot set ExposureCompensation with this value");
			}
		}
	}

	public int setExposureTime(int time) {
		if (this.exposureControl != null) {
			if (time <= this.getExposureMinExposureTime()
					&& time >= this.getExposureMaxExposureTime())
				try {
					return this.exposureControl.setExposureTime(time);
				} catch (MediaException e) {
					//#debug
					System.out
							.println("cannot set ExposureTime with this value");
				}
			else
				return this.getExposureTime();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public void setExposureFStop(int aperture) {
		if (this.exposureControl != null
				&& this.isThisExposureFStopSupported(aperture)) {
			try {
				this.exposureControl.setFStop(aperture);
			} catch (MediaException e) {
				//#debug
				System.out.println("cannot set ExposureFStop with this value");
			}
		}
	}

	public void setExposureISO(int iso) {
		if (this.exposureControl != null
				&& this.isThisExposureISOSupported(iso)) {
			try {
				this.exposureControl.setISO(iso);
			} catch (MediaException e) {
				//#debug
				System.out.println("cannot set ExposureISO with this value");
			}
		}
	}

	public void setExposureLightMetering(String metering) {
		if (this.exposureControl != null
				&& this.isThisExposureLightMeteringSupported(metering)) {
			this.exposureControl.setLightMetering(metering);
		}
	}

	public boolean isWhiteBalanceSupported() {
		if (this.whiteBalanceControl != null) {
			return true;
		}
		return false;
	}

	public boolean isCameraControlSupported() {
		if (this.cameraControl != null) {
			return false;
		}
		return false;
	}

	public void setShutterFeedback(boolean enable) {
		if (this.cameraControl != null) {
			try {
				this.cameraControl.enableShutterFeedback(enable);
			} catch (MediaException e) {
				//#debug
				System.out.println("cannot set ShutterFeedback");
			}
		}
	}

	public int getCameraRotation() {
		if (this.cameraControl != null) {
			return this.getCameraRotation();
		}
		return CameraUtil.Feature_NOT_SUPPORTED;
	}

	public String getExposureMode() {
		if (this.cameraControl != null) {
			return this.cameraControl.getExposureMode();
		}
		return "CameraControl is not supported";
	}

	public boolean isThisExposureModeSupported(String mode) {
		if (this.cameraControl != null) {
			String[] supportedExposureModes = this.cameraControl
					.getSupportedExposureModes();
			for (int i = 0; i < supportedExposureModes.length; i++) {
				if (supportedExposureModes[i].equals(mode))
					return true;
			}
		}
		return false;
	}

	public void setExposureMode(String mode) {
		if (this.cameraControl != null
				&& this.isThisExposureModeSupported(mode)) {
			this.cameraControl.setExposureMode(mode);
		}
	}

	public String[] getSupportedExposureModes() {
		if (this.cameraControl != null) {
			return this.cameraControl.getSupportedExposureModes();
		}
		return new String[] { "CameraControl is not supported" };
	}

	public boolean isShutterFeedbackEnabled() {
		if (this.cameraControl != null) {
			return this.cameraControl.isShutterFeedbackEnabled();
		}
		return false;
	}

	public boolean isRotationSupported() {
		boolean result = false;
		//#if polish.video.rotate
		result = true;
		//#endif
		return result;
	}

	public boolean isBurstSupported() {
		if (this.snapshotControl != null) {
			return true;
		} else {
			return false;
		}
	}

	public String getBurstDirectory() {
		if (this.snapshotControl != null) {
			return this.snapshotControl.getDirectory();
		}
		return "Feature is not supported";
	}

	public String getBurstFilePrefix() {
		if (this.snapshotControl != null) {
			return this.snapshotControl.getFilePrefix();
		}
		return "Feature is not supported";
	}

	public String getBurstFileSuffix() {
		if (this.snapshotControl != null) {
			return this.snapshotControl.getFileSuffix();
		}
		return "Feature is not supported";
	}

	public void setBurstDirectory(String directory) {
		if (this.snapshotControl != null) {
			this.snapshotControl.setDirectory(directory);
		}
	}

	public void setBurstFilePrefix(String prefix) {
		if (this.snapshotControl != null) {
			this.snapshotControl.setFilePrefix(prefix);
		}
	}

	public void setBurstFileSuffix(String suffix) {
		if (this.snapshotControl != null) {
			this.snapshotControl.setFileSuffix(suffix);
		}
	}

	public void startBurst(int maxShots) {
		if (this.snapshotControl != null) {
			try {
				this.snapshotControl.start(maxShots);
			} catch (NumberFormatException nf) {
				//#debug
				System.out.println("error while taking a burst shot");
			}
		}
	}

	public void stopBurst() {
		if (this.snapshotControl != null) {
			this.snapshotControl.stop();
		}
	}

	public void unfreezeBurst(boolean save) {
		if (this.snapshotControl != null) {
			this.snapshotControl.unfreeze(save);
		}
	}

	public void takeSnapshot() {
		try {
			byte[] snap = this.videoControl.getSnapshot(null);
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the autofocus mode is enabled. Note that autofocusing is only
	 * supported for devices with the camera supplements of the Advanced Media
	 * API (JSR 234).
	 * 
	 * @return true when the autofocus mode should be enabled and when JSR 234
	 *         is supported
	 */
	public boolean isAutofocus() {
		boolean result;
		//#if polish.api.advancedmultimedia
		result = this.isAutofocusEnabled;
		//#else
		result = false;
		//#endif
		return result;
	}

	public void initPlayerAgain(boolean videoCapturing) {
		this.videoCapturing = videoCapturing;
		new Thread(this).start();
	}

	/**
	 * Initializes the snapshot player.
	 * 
	 * @throws MediaException
	 *             when initialization fails
	 */
	private synchronized void initPlayer() throws MediaException {

		//#debug
		System.out.println("initPlayer()");
		this.readyToScale = false;
		if (this.player != null) {
			stopSnapshot();
		}
		this.isInitializing = true;
		try {
			String[] contentTypes = Manager.getSupportedContentTypes("capture");
			if (contentTypes == null || contentTypes.length == 0) {
				throw new MediaException("capture not supported");
			}
			String protocol;
			if (!this.videoCapturing) {
				//#if polish.identifier.motorola/v3xx
				protocol = "capture://camera";
				//#elif polish.group.series60e3
				protocol = "capture://devcam0";
				//#elif polish.group.series40				
				protocol = "capture://image";
				//#elif polish.vendor == Generic
				if (DeviceInfo.getVendor() == DeviceInfo.VENDOR_MOTOROLA)
					protocol = "capture://camera";
				else
					protocol = "capture://video";
				//#else
				protocol = "capture://video";
				String device = "video";
				// #debug info
				System.out.println("Here are the supported contentTypes");
				boolean deviceIsSupported = false;
				for (int i = 0; i < contentTypes.length; i++) {
					String contentType = contentTypes[i];
					// #debug info
					System.out.println("ContentType " + i + " " + contentType);
					if (contentType.equals("image")) { // this is the case on Series 40, for example
						protocol = "capture://image";
						deviceIsSupported = true;
						break;
					}
					if (contentType.startsWith(device)) {
						deviceIsSupported = true;
					}
				}
				if (!deviceIsSupported && contentTypes.length > 0) {
					protocol = "capture://" + contentTypes[0];
				}
				//#endif
			} else {
				//#if polish.identifier.motorola/v3xx
				protocol = "capture://camera";
				//#elif polish.group.series60e3
				protocol = "capture://devcam0";
				//#elif polish.vendor == Sony-Ericsson
				protocol = "capture://audio_video";
				//#elif polish.vendor == Generic
				if (DeviceInfo.getVendor() == DeviceInfo.VENDOR_MOTOROLA)
					protocol = "capture://camera";
				else if (DeviceInfo.getVendor() == DeviceInfo.VENDOR_SONY_ERICSSON)
					protocol = "capture://audio_video";
				else
					protocol = "capture://video";
				//#else
				protocol = "capture://video";
				//#endif
			}
			//#debug info
			System.out.println("The capture protocol is " + protocol);
			try {
				this.player = Manager.createPlayer(protocol);
			} catch (MediaException e) {
				if (!"capture://video".equals(protocol)) {
					protocol = "capture://video";
					this.player = Manager.createPlayer(protocol);
				}
			}
			this.player.addPlayerListener(this);
			this.player.realize();

			Control[] controls = player.getControls();
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof VideoControl)
					this.videoControl = (VideoControl) controls[i];
				else if (controls[i] instanceof CameraControl)
					this.cameraControl = (CameraControl) controls[i];
				else if (controls[i] instanceof ExposureControl)
					this.exposureControl = (ExposureControl) controls[i];
				else if (controls[i] instanceof FlashControl)
					this.flashControl = (FlashControl) controls[i];
				else if (controls[i] instanceof FocusControl)
					this.focusControl = (FocusControl) controls[i];
				else if (controls[i] instanceof SnapshotControl)
					this.snapshotControl = (SnapshotControl) controls[i];
				else if (controls[i] instanceof ZoomControl)
					this.zoomControl = (ZoomControl) controls[i];
				else if (controls[i] instanceof WhiteBalanceControl)
					this.whiteBalanceControl = (WhiteBalanceControl) controls[i];
				else if (controls[i] instanceof RecordControl)
					this.recordControl = (RecordControl) controls[i];
				else if (controls[i] instanceof ImageFormatControl)
					this.imageFormatControl = (ImageFormatControl) controls[i];

			}
			//#if polish.group.SE-JavaPlatform8 

			//			this.displayModeControl = (DisplayModeControl) this.player
			//					.getControl("DisplayModeControl");

			//#endif
			if (this.videoControl != null) {
				try {
					//#if polish.group.SE-JavaPlatform7 || polish.group.SE-JavaPlatform8 
					if (!this.cameraOverlay)
						this.videoControl.initDisplayMode(
								VideoControl.USE_DIRECT_VIDEO, Display
										.getInstance());
					else {

						this.videoControl.initDisplayMode(
								VideoControl.USE_DIRECT_VIDEO | 1 << 8, Display
										.getInstance());
					}
					//#else
					if (DeviceInfo.getVendor() == DeviceInfo.VENDOR_SONY_ERICSSON)

						this.videoControl.initDisplayMode(
								VideoControl.USE_DIRECT_VIDEO, Display
										.getInstance());
					//#endif
					//					}

					this.videoControl.setVisible(true);

					this.player.prefetch();
					if (this.burstFilePrefix != null)
						this.setBurstFilePrefix(this.burstFilePrefix);
					if (this.burstFileSuffix != null)
						this.setBurstFileSuffix(this.burstFileSuffix);
					if (this.burstFileDirectory != null)
						this.setBurstDirectory(this.burstFileDirectory);

					// it's a bit weird to first start the player before setting the size and location,
					// but in this way it works fine on Nokia Series 60 as well:
					//#if polish.Bugs.videoControlBeforePlayer

					//#endif
					//					int width = this.contentWidth;
					//					int height = this.contentHeight;
					//					int locX = this.contentX;
					//					int locY = this.contentY;
					//					//					Point resolution = getDefaultResolution();
					//					//					if (resolution != null) {
					//					int resW = this.videoControl.getDisplayWidth();
					//					int resH = this.videoControl.getDisplayHeight();
					//					//					//#debug
					//					//					System.out.println("default resolution: " + resW + "x"
					//					//							+ resH + ", currentRes=" + width + "x" + height);
					//
					//					//						if ( (resW != 640 || resH != 480) && ( (resW > resH && getWidth() <= getHeight()) || (resW < resH && getWidth() > getHeight()) ) ) {
					//					//							//#debug
					//					//							System.out.println("resolution is switched to horizontal mode");
					//					//							int tmp = resW;
					//					//							resW = resH;
					//					//							resH = tmp;
					//					//						}
					//					// 1: assume height stays the same:
					//					int adjustedWidth = (resW * height) / resH;
					//					if (adjustedWidth <= width) {
					//						// height can stay the same, but the width is shrinked:
					//						locX += (width - adjustedWidth) / 2;
					//						width = adjustedWidth;
					//					} else {
					//						// 2. width can stay the same, but the height is shrinked:
					//						int adjustedHeight = (width * resH) / resW;
					//						locY += (height - adjustedHeight) / 2;
					//						height = adjustedHeight;
					//					}
					//					//					width = this.contentWidth;
					//					//					height = this.contentHeight;
					//					//					locX = this.contentX;
					//					//					locY = this.contentY;
					//					//					locX = (int) ((float) ((1 / 3) * height));
					//					//					height = (int) ((float) ((2 / 3) * height));
					//					//#debug
					//					System.out.println("Switched res to " + width + "x"
					//							+ height);
					//					//					}
					//					//#debug
					//					System.out.println("x " + locX + "  y " + locY);
					if (this.autoScale) {
						int x = 1;
						int y = 1;
						this.videoControl.setDisplaySize(this.getWidth() - 2,
								this.getHeight() - 2);
						this.videoControl.setDisplayLocation(x, y);
						//#if !polish.Bugs.videoControlBeforePlayer
						this.player.start();
						//#endif
					} else {
						this.readyToScale = true;
					}

					////#debug
				} catch (MediaException e) {
					//#debug error
					System.out
							.println("Cannot start video player. The error is: "
									+ e);
					throw e;
				}
			}
		} catch (MediaException e) {
			throw e;
		} catch (Throwable e) {
			//#debug error
			System.out.println("unable to initialize capture player" + e);
			throw new MediaException(e.toString());
		} finally {
			//#if polish.Bugs.SnapshotRequiresScreenChange
			//#debug
			//System.out.println("Screen toggle for preview window.");
			this.isSnapshotInProgress = true;
			Display.getInstance().toggleScreen();
			this.isSnapshotInProgress = false;
			//#endif
			this.isInitializing = false;
			repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		//		if (this.error != null) {
		//			g.drawString( this.error.toString(), getWidth() - 10, getHeight()/2, Graphics.RIGHT | Graphics.TOP );
		//		}
	}

	//#ifdef polish.useDynamicStyles
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "camerascreen";
	}
	//#endif

	public boolean isFocusSupported() {
		if (this.focusControl != null)
			return true;
		else
			return false;
	}

	public void initVideoCapture() {
		if (this.recordControl == null) {
			try {

				String protocol;
				//#if polish.identifier.motorola/v3xx
				protocol = "capture://camera";
				//#elif polish.group.series60e3
				protocol = "capture://devcam0";
				//#elif polish.vendor == Sony-Ericsson
				protocol = "capture://audio_video";
				//#else
				protocol = "capture://video";
				//#endif
				this.player = Manager.createPlayer(protocol);
				//#debug
				System.out.println("" + this.player);
				this.videoControl = (VideoControl) this.player
						.getControl("VideoControl");
				//#debug
				System.out.println("" + this.videoControl);
				this.recordControl = (RecordControl) this.player
						.getControl("RecordControl");
				//#debug
				System.out.println("" + this.recordControl);
				this.player.realize();
				this.videoControl.initDisplayMode(
						VideoControl.USE_DIRECT_VIDEO, Display.getInstance());
				System.out.println("" + this.recordControl);
				this.player.start();
			} catch (IOException e) {
				//#debug
				System.out.println("" + e.getMessage());
			} catch (MediaException e) {
				//#debug
				System.out.println("" + e.getMessage());
			}
		}

	}

	public void startCapture(String recordLoaction,
			ByteArrayOutputStream stream, int sizeLimit) {
		if (this.recordControl != null) {
			FileConnection fc;

			try {
				fc = (FileConnection) Connector.open(recordLoaction);
				try {

					fc.delete();

				} catch (IOException ex) {
				}

				fc.create();
				fc.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if (stream != null) {
				recordControl.setRecordStream(stream);
			}

			try {
				this.recordControl.setRecordSizeLimit(sizeLimit);

				this.recordControl.setRecordLocation(recordLoaction);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MediaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.isRecording = true;
			this.recordControl.startRecord();
			this.startRecordTime = System.currentTimeMillis();

		}
	}

	public void stopCapture() {
		if (this.recordControl != null && this.isRecording) {
			this.isRecording = false;
			this.recordControl.stopRecord();
			this.startRecordTime = 0;
			try {
				this.recordControl.commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//#debug
			System.out.println("gestoppt");
		}
	}

	public Control getControl(String name) {
		if (name.equals("VideoControl"))
			return this.videoControl;
		else if (name.equals("RecordControl"))
			return this.recordControl;
		else if (name.equals("CameraControl"))
			return this.cameraControl;
		else if (name.equals("ExposureControl"))
			return this.exposureControl;
		else if (name.equals("FlashControl"))
			return this.flashControl;
		else if (name.equals("FocusControl"))
			return this.focusControl;
		else if (name.equals("SnapshotControl"))
			return this.snapshotControl;
		else if (name.equals("ZoomControl"))
			return this.zoomControl;
		return null;
	}

	public void playerUpdate(Player p, String event, Object arg2) {

		switch (p.getState()) {
		case Player.CLOSED:
			break;
		case Player.PREFETCHED:
			break;
		case Player.REALIZED:
			break;
		case Player.STARTED:
			this.isIntialized = true;

			break;

		case Player.UNREALIZED:
			break;
		}
	}

	public void addEventListner(CameraScreenListener observer) {
		this.observers.add(observer);
	}

	public void removeEventListner(CameraScreenListener observer) {
		this.observers.remove(observer);
	}

	private void notifyObservers(final CameraScreenEvent event) {
		for (int i = 0; i < observers.size(); i++) {
			final CameraScreenListener observer = (CameraScreenListener) observers
					.get(i);

			if (observer != null) {
				new Thread() {
					public void run() {
						observer.cameraDoSomething(event);
					}
				}.start();
			}

		}
	}

	public boolean isIntialized() {
		return isIntialized;
	}

	public boolean isVideoCapturing() {
		return videoCapturing;
	}

	public long getStartRecordTime() {
		return startRecordTime;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public boolean isAutoScale() {
		return autoScale;
	}

	public boolean isReadyToScale() {
		return readyToScale;
	}

	//#if polish.group.SE-JavaPlatform8 
	//	public DisplayModeControl getDisplayModeControl() {
	//		return displayModeControl;
	//	}
	//#endif
}
