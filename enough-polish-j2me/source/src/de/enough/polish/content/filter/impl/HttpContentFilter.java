package de.enough.polish.content.filter.impl;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.filter.ContentFilter;

public class HttpContentFilter implements ContentFilter {

	public boolean filter(ContentDescriptor descriptor) {
		return descriptor.getUrl().startsWith("http://");
	}

}
