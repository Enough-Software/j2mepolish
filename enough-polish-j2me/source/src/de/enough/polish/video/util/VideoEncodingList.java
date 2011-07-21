package de.enough.polish.video.util;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;

/**
 * Retrieves and parses the system property vide.encodings and stores the
 * resulting list of VideoEncoding instances
 * 
 * @author Andre
 * 
 */
public class VideoEncodingList {

	/**
	 * the resulting VideoEncoding instances as an ArrayList
	 */
	private ArrayList encodings;

	/**
	 * Creates a new VideoEncodingList instance
	 */
	public VideoEncodingList() {
		this(null);
	}

	/**
	 * Creates a new VideoEncodingList instance
	 * 
	 * @param filter
	 *            the filter to use
	 */
	public VideoEncodingList(VideoEncoding filter) {
		// get the system property
		String encodingsProperty = System.getProperty("video.encodings");
		// parse all encodings
		ArrayList allEncodings = parseEncodings(encodingsProperty);

		// if no filter is given ...
		if (filter == null) {
			// store all encodings
			this.encodings = allEncodings;
			// otherwise ...
		} else {
			// store filtered encodings
			this.encodings = filterEncodings(allEncodings, filter);
		}
	}

	/**
	 * Parses the encodings property and returns the encodings as an ArrayList
	 * 
	 * @param videoEncodings
	 *            the encodings property
	 * @return the resulting ArrayList
	 */
	private ArrayList parseEncodings(String videoEncodings) {
		String[] splittedEncodings = TextUtil.split(videoEncodings, ' ');
		ArrayList resultEncodings = new ArrayList();
		for (int index = 0; index < splittedEncodings.length; index++) {
			String encoding = splittedEncodings[index];
			resultEncodings.add(new VideoEncoding(encoding));
		}

		return resultEncodings;
	}

	/**
	 * Filters the given encodings by the given filter
	 * 
	 * @param encodings
	 *            the encodings
	 * @param filter
	 *            the filter
	 * @return the filtered encodings
	 */
	private ArrayList filterEncodings(ArrayList encodings, VideoEncoding filter) {
		ArrayList filteredEncodings = new ArrayList();
		for (int index = 0; index < encodings.size(); index++) {
			VideoEncoding encoding = (VideoEncoding) encodings.get(index);

			String mime = filter.getEncoding();
			if (mime != null) {
				if (!mime.equals(encoding.getEncoding())) {
					continue;
				}
			}

			String width = filter.getWidth();
			if (width != null) {
				if (!width.equals(encoding.getWidth())) {
					continue;
				}
			}

			String height = filter.getHeight();
			if (height != null) {
				if (!height.equals(encoding.getHeight())) {
					continue;
				}
			}

			String videoCodec = filter.getVideoCodec();
			if (videoCodec != null) {
				if (!videoCodec.equals(encoding.getVideoCodec())) {
					continue;
				}
			}

			String audioCodec = filter.getAudioCodec();
			if (audioCodec != null) {
				if (!audioCodec.equals(encoding.getAudioCodec())) {
					continue;
				} 
			}

			filteredEncodings.add(encoding);
		}

		return filteredEncodings;
	}

	/**
	 * Returns the size
	 * 
	 * @return the size
	 */
	public int size() {
		return this.encodings.size();
	}

	/**
	 * Returns the encoding at the given index
	 * 
	 * @param index
	 *            the index
	 * @return the encoding at the given index
	 */
	public VideoEncoding get(int index) {
		return (VideoEncoding) this.encodings.get(index);
	}

	/**
	 * Returns the video encodings as an array
	 * 
	 * @return the video encodings as an array
	 */
	public VideoEncoding[] toArray() {
		return (VideoEncoding[])this.encodings.toArray(new VideoEncoding[this.encodings.size()]);
	}
}
