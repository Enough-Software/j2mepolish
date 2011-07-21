package de.enough.polish.content.transform;

import java.io.IOException;

/**
 * Used to transform content data before storing and passing it to
 * the parenting source.
 * @author Andre
 *
 */
public interface ContentTransform {
	/**
	 * returned by calculateDataSize() to indicate that the
	 * size of the data is unknown 
	 */
	public static int DATASIZE_UNKNOWN = Integer.MIN_VALUE;
	
	/**
	 * Returns the transformation id
	 * @return the transformation id
	 */
	public String getTransformId();
	
	/**
	 * Transforms content data from one data type to another
	 * @param rawData the raw data to transform
	 * @return the transformed data
	 * @throws IOException if an error occurs
	 */
	public Object transformContent(Object rawData) throws IOException;
	
	/**
	 * Calculates the data size of the transformed data in bytes
	 * @param transformedData the transformed data
	 * @return the calculated size
	 */
	public int calculateDataSize(Object transformedData);
}
