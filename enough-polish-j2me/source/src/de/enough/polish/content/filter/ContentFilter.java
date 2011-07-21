package de.enough.polish.content.filter;

import de.enough.polish.content.ContentDescriptor;

/**
 * Used to filter content descriptors from a ContentSource. Given the case that
 * a ContentSource has multiple ContentSources attached to it a content filter
 * is needed to determine which ContentSource to use.
 * 
 * @author Andre
 * 
 */
public interface ContentFilter {
	/**
	 * Returns true if the descriptor is valid for the parenting ContentSource
	 * 
	 * @param descriptor
	 *            the ContentDescriptor
	 * @return true if the descriptor is valid for the parenting ContentSource
	 */
	public boolean filter(ContentDescriptor descriptor);
}
