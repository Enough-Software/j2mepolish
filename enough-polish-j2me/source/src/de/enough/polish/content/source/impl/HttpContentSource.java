package de.enough.polish.content.source.impl;

import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.source.ContentSource;
import de.enough.polish.content.storage.StorageReference;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.StreamUtil;

/**
 * A sample HttpContentSource that receives byte[]
 * @author Andre Schmidt
 *
 */
public class HttpContentSource extends ContentSource{
	
	public final static String PREFIX = "http://";

	public HttpContentSource(String id) {
		super(id);
	}

	/* (non-Javadoc)
	 * @see com.zyb.nowplus.business.content.ContentSource#destroy(com.zyb.nowplus.business.content.StorageReference)
	 */
	protected void destroy(StorageReference reference) throws IOException {
		// do nothing here, its just a source
	}
	
	/* (non-Javadoc)
	 * @see com.zyb.nowplus.business.content.ContentSource#load(com.zyb.nowplus.business.content.ContentDescriptor)
	 */
	protected Object load(ContentDescriptor descriptor) throws IOException {
		byte[] data = null;
		
		// open the http connection
		RedirectHttpConnection connection = new RedirectHttpConnection(descriptor.getUrl());
		InputStream stream = connection.openInputStream();

		// read all bytes into data
		data = StreamUtil.readFully(stream);
		
		return data;
	}

	/* (non-Javadoc)
	 * @see com.zyb.nowplus.business.content.ContentSource#load(com.zyb.nowplus.business.content.StorageReference)
	 */
	protected Object load(StorageReference reference) throws IOException {
		// do nothing here, its just a source
		return null;
	}

	/* (non-Javadoc)
	 * @see com.zyb.nowplus.business.content.ContentSource#store(com.zyb.nowplus.business.content.ContentDescriptor, java.lang.Object)
	 */
	protected Object store(ContentDescriptor descriptor, Object data)
			throws IOException {
		// do nothing here, its just a source
		return null;
	}
	
	protected Object[] storeContentAndGetDataSize(ContentDescriptor descriptor,
			Object data) throws IOException {
		// Do nothing, this is a source
		return null;
	}

}
