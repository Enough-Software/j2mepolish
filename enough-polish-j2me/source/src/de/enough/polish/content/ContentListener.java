package de.enough.polish.content;

/**
 * An interface to notify a listening instance that
 * a content is loaded or an error has occured
 * while fetching the content 
 * @author Andre Schmidt
 *
 */
public interface ContentListener {
	/**
	 * Notifies a ContentListener that content data is
	 * available
	 * @param descriptor the content descriptor
	 * @param data the content data
	 */
	public void onContentLoaded(ContentDescriptor descriptor, Object data);
	
	/**
	 * Notifies a ContentListener that an error
	 * occured while requesting content
	 * @param descriptor the content descriptor
	 * @param exception the error
	 */
	public void onContentError(ContentDescriptor descriptor,Exception exception);
	
}
