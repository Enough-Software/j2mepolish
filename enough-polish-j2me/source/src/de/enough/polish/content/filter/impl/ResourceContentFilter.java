package de.enough.polish.content.filter.impl;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.filter.ContentFilter;

public class ResourceContentFilter implements ContentFilter {

	public boolean filter(ContentDescriptor descriptor) {
		return descriptor.getUrl().startsWith("resources://");
	}

}
