package de.enough.polish.content.transform.impl;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import de.enough.polish.content.transform.ContentTransform;


/**
 * A ContentTransform to handle images. ONLY USE THIS FOR THE CONTENTLOADER.
 * @author Andre
 *
 */
public class ImageContentTransform implements ContentTransform{
	public final static String ID = "image";
	
	/* (non-Javadoc)
	 * @see de.enough.polish.content.transform.ContentTransform#getTransformId()
	 */
	public String getTransformId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.content.transform.ContentTransform#transformContent(java.lang.Object)
	 */
	public Object transformContent(Object rawData) throws IOException {
		byte[] imageData = (byte[])rawData;
		return Image.createImage(imageData, 0, imageData.length);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.content.transform.ContentTransform#calculateDataSize(java.lang.Object)
	 */
	public int calculateDataSize(Object transformedData) {
		if (transformedData instanceof Image) {
			Image image = (Image) transformedData;
			return image.getHeight() * image.getWidth() * 4;
		} else {
			return DATASIZE_UNKNOWN;
		}
	}	
}
