package de.enough.polish.content;

/**
 * A exception if an error occurs during the retrieval of content
 * 
 * @author Andre
 * 
 */
public class ContentException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new ContentException instance
	 * @param message the message
	 */
	public ContentException(String message) {
		super(message);
	}
}
