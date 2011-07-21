//#condition polish.api.mmapi
package de.enough.polish.snapshot;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

import de.enough.polish.camera.CameraResolution;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.TextUtil;

public class SnapshotUtil {

	//private static final long PLAYER_TIMEOUT = 300;
	/**
	 * The mount position of the camera is not known.
	 * @see #getCameraMountPosition()
	 */
	private static final int MOUNT_POSITION_UNKNOWN = -1;
	/**
	 * The mount position of the camera is landscape (wider than high).
	 * @see #getCameraMountPosition()
	 */
	private static final int MOUNT_POSITION_LANDSCAPE = 1;
	/**
	 * The mount position of the camera is portrait (higher than wide).
	 * @see #getCameraMountPosition()
	 */
	private static final int MOUNT_POSITION_PORTRAIT = 2;
	
	
	/**
	 * Returns the protocol to capture an image
	 * @return the protocol string 
	 * @throws MediaException if capture is not supported
	 */
	public static String getProtocol() throws MediaException {
			String[] contentTypes = Manager.getSupportedContentTypes("capture");
			if (contentTypes == null || contentTypes.length == 0) {
				throw new MediaException("capture not supported");
			}
			String protocol;
			//#if polish.identifier.motorola/v3xx
				protocol = "capture://camera";
			//#elif polish.group.series60e3
				protocol = "capture://devcam0";
			//#elif polish.group.series40				
				protocol = "capture://image";
			//#elif polish.blackberry
				protocol = "capture://video";
			//#else
				protocol = "capture://video";
				String device = "video";
				//#debug info
				System.out.println("Here are the supported contentTypes:");
				boolean deviceIsSupported = false;
				for (int i = 0; i < contentTypes.length; i++) {
					String contentType = contentTypes[i];
	                //#debug info
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
			return protocol;
	}
	

	/**
	 * Retrieves the position of the camera.
	 * @return either portrait, landscape or unknown
	 * @see #MOUNT_POSITION_LANDSCAPE
	 * @see #MOUNT_POSITION_PORTRAIT
	 * @see #MOUNT_POSITION_UNKNOWN
	 */
	public static int getCameraMountPosition() {
		try {
			String positionStr = System.getProperty("camera.mountorientation");
			if (positionStr != null) {
				positionStr = positionStr.toUpperCase();
				if (positionStr.indexOf("LANDSCAPE") != -1) {
					return MOUNT_POSITION_LANDSCAPE;
				} else if (positionStr.indexOf("PORTRAIT") != -1) {
					return MOUNT_POSITION_PORTRAIT;	
				}
			}
		} catch (Exception e) {
			// ignore, unsupported system property
		}
		return MOUNT_POSITION_UNKNOWN;
	}
	

	/**
	 * Retrieves the supported snapshot encodings available on the current device.
	 *
	 * @return an array of encodings.
	 *         When the "video.snapshot.encodings" system property is null, an empty array is returned.
	 */
	public static String[] getSnapshotEncodings(){
		String supportedEncodingsStr = System.getProperty("video.snapshot.encodings");
		if(supportedEncodingsStr == null){
			return new String[0];
		}
		String[] encodings = TextUtil.splitAndTrim(supportedEncodingsStr, ' ');
		Arrays.sort(encodings);
		return encodings;
	}
	
	/**
	 * Tries to determine the available resolutions from the supported encodings
	 * @return an array of detected resolutions, may be empty but not null. Each resolution is returned as a Point object, where x defines the width and y defines the height of the resolution.
	 */
	public static CameraResolution[] getSnapshotResolutions() {
		String[] encodings = getSnapshotEncodings();
		ArrayList resolutionsList = new ArrayList();
		for (int i = 0; i < encodings.length; i++)
		{
			String encoding = encodings[i];
			int widthIndex = encoding.indexOf("width="); 
			if (widthIndex != -1) {
				int splitPos = encoding.indexOf('&', widthIndex + 1);
				String width;
				if (splitPos != -1) {
					width = encoding.substring(widthIndex + "width=".length(), splitPos);
				} else {
					width = encoding.substring(widthIndex + "width=".length());
				}
				int heightIndex = encoding.indexOf("height=");
				if (heightIndex != -1) {
					splitPos = encoding.indexOf('&', heightIndex + 1);
					String height;
					if (splitPos == -1) {
						height = encoding.substring(heightIndex + "height=".length());
					} else {
						height = encoding.substring(heightIndex + "height=".length(), splitPos);
					}
					CameraResolution resolution = new CameraResolution( Integer.parseInt( width ), Integer.parseInt( height ) );
					if (!resolutionsList.contains(resolution)) {
						resolutionsList.add(resolution);
					}
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
					while (end < encoding.length() && Character.isDigit(encoding.charAt(end)) ) {
						end++;
					}
					if (end != encoding.length() - 1) {
						end--;
					}
					if (start != middlePos && end != middlePos) {
						String resolutionStr = encoding.substring(start, end + 1 );
						int xIndex = resolutionStr.indexOf('x');
						CameraResolution resolution = new CameraResolution( Integer.parseInt( resolutionStr.substring(0, xIndex) ), 
								Integer.parseInt( resolutionStr.substring(xIndex + 1) ) );
						if (!resolutionsList.contains(resolution)) {
							resolutionsList.add(resolution);
						}
					}
				}
			}
		}
		CameraResolution[] resolutions = (CameraResolution[]) resolutionsList.toArray( new CameraResolution[ resolutionsList.size() ] );
		Arrays.sort( resolutions );
		return resolutions;
	}
	
	/**
	 * Retrieves the default resolution.
	 * 
	 * @return the default resolution as a point object - x represents the width, y the height. This can be null if no resolution is specified in "video.snapshot.encodings"!
	 */
	public static CameraResolution getDefaultResolution() {
		String encoding = System.getProperty("video.snapshot.encodings");
		if(encoding == null){
			return null;
		}
		int splitPos = encoding.indexOf(' ');
		if (splitPos != -1) {
			encoding = encoding.substring( 0, splitPos );
		}
		int widthIndex = encoding.indexOf("width="); 
		if (widthIndex != -1) {
			splitPos = encoding.indexOf('&', widthIndex + 1);
			String width;
			if (splitPos != -1) {
				width = encoding.substring(widthIndex + "width=".length(), splitPos);
			} else {
				width = encoding.substring(widthIndex + "width=".length());
			}
			int heightIndex = encoding.indexOf("height=");
			if (heightIndex != -1) {
				splitPos = encoding.indexOf('&', heightIndex + 1);
				String height;
				if (splitPos == -1) {
					height = encoding.substring(heightIndex + "height=".length());
				} else {
					height = encoding.substring(heightIndex + "height=".length(), splitPos);
				}
				return new CameraResolution( Integer.parseInt(width), Integer.parseInt( height ));
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
				while (end < encoding.length() && Character.isDigit(encoding.charAt(end)) ) {
					end++;
				}
				if (end != encoding.length() - 1) {
					end--;
				}
				if (start != middlePos && end != middlePos) {
					String resolution = encoding.substring(start, end + 1 );
					splitPos = resolution.indexOf('x');
					return new CameraResolution( Integer.parseInt(resolution.substring(0, splitPos)), Integer.parseInt( resolution.substring(splitPos + 1)  ));
				}
			}
		}
		return null;
	}
	
	/**
	 * Determines the best fitting encoding for reaching snapshots of at least the specified dimensions
	 * @param width the minimum width, e.g. 800
	 * @param height the minum height, e.g. 640
	 * @return an array of suitable encodings, when format is specified this will typically only contain one element;
	 *         the array will be empty (but not null) when no suitable encoding is found
	 */
	public static String[] getSnapshotEncodingsWithResolutionCloseTo( int width, int height) {
		return getSnapshotEncodingsWithResolutionCloseTo(width, height, false, false, null);
	}

	/**
	 * Determines the best fitting encoding
	 * @param format the format, e.g. jpg
	 * @param width the desired width, e.g. 800
	 * @param height the desired height, e.g. 640
	 * @param needsToBeLarger true when the found encodings needs to be the same size or larger than the specified dimensions
	 * @param needsToBeSmaller true when the found encodings needs to be the same size or smaller than the specified dimensions 
	 * @return an array of suitable encodings, when format is specified this will typically only contain one element;
	 *         the array will be empty (but not null) when no suitable encoding is found
	 */
	public static String[] getSnapshotEncodingsWithResolutionCloseTo( int width, int height, boolean needsToBeLarger, boolean needsToBeSmaller, String format ) {
		CameraResolution[] resolutions = getSnapshotResolutions();
		String defaultEncoding;
		if (format != null) {
			defaultEncoding = "encoding=" + format + "&width=" + width + "&height=" + height;
		} else {
			defaultEncoding = "encoding=jpeg&width=" + width + "&height=" + height;
		}
		if (resolutions.length == 0) {
			return new String[]{ defaultEncoding };
		}
		int minDistance = Integer.MAX_VALUE;
		int closestWidth = 0;
		int closestHeight = 0;
		for (int i = 0; i < resolutions.length; i++)
		{
			CameraResolution resolution = resolutions[i];
			int resWidth = resolution.width;
			int resHeight = resolution.height;
			if (
				(needsToBeLarger && (resWidth < width || resHeight < height))
				|| (needsToBeSmaller && (resWidth > width || resHeight > height))
				) 
			{
				continue;
			}
			int distance = Math.abs( width - resWidth ) + Math.abs( height - resHeight );
			if (distance < minDistance) {
				closestWidth = resWidth;
				closestHeight = resHeight;
				minDistance = distance;
			}
		}
		if (minDistance == Integer.MAX_VALUE) {
			return new String[] { defaultEncoding };
		}
		String[] encodings = getSnapshotEncodings();
		if (format != null) {
			format = format.toLowerCase();
		}
		ArrayList bestEncodings = new ArrayList();
		String closestWidthStr = Integer.toString(closestWidth);
		String closestHeightStr = Integer.toString(closestHeight);
		for (int i = 0; i < encodings.length; i++)
		{
			String encoding = encodings[i];
			if ( format == null || encoding.toLowerCase().indexOf(format) != -1) {
				if (encoding.indexOf(closestWidthStr) != -1 && encoding.indexOf(closestHeightStr) != -1) {
					bestEncodings.add(encoding);
				}
			}
		}
		if (bestEncodings.size() == 0) {
			return new String[] { defaultEncoding };			
		}
		encodings = (String[]) bestEncodings.toArray( new String[ bestEncodings.size() ]);
		Arrays.sort(encodings);
		return encodings;
	}
	
	
	
	/**
	 * Tries to determine the available formats from the supported encodings
	 * @return an array of detected formats (in lower case), may be emtpy but not null
	 */
	public static String[] getSnapshotFormats() {
		String[] encodings = getSnapshotEncodings();
		ArrayList formats = new ArrayList();
		boolean pngAdded = false;
		boolean jpegAdded = false;
		boolean bmpAdded = false;
		boolean gifAdded = false;
		for (int i = 0; i < encodings.length; i++)
		{
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
	
		return (String[]) formats.toArray( new String[ formats.size() ] );
	}
}
