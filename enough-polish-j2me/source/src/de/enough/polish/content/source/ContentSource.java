package de.enough.polish.content.source;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.ContentException;
import de.enough.polish.content.filter.ContentFilter;
import de.enough.polish.content.storage.StorageIndex;
import de.enough.polish.content.storage.StorageReference;
import de.enough.polish.content.transform.ContentTransform;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.ToStringHelper;

/**
 * <p>
 * ContentSource implementations are used to load, store and destruct contents
 * through a hierachy of hosted ContentSources of a parenting ContentSource,
 * e.g.:
 * </p>
 * <p>
 * HttpContentSource http = new HttpContentSource();<br/>
 * RMSStorage rms = new RMSStorage();<br/>
 * rms.attachSource(http);<br/>
 * ContentLoader loader = new ContentLoader();<br/>
 * loader.attachSource(rms);<br/>
 * </p>
 * <p>
 * The sample code above will create a hierachy to load content from http, store
 * / load content from the RMS and load/store content from the memory.
 * </p>
 * <p>
 * A ContentSource implementation can be implemented in two ways : as a source
 * or a storage. A source only loads content (e.g. a http source) while a
 * storage both loads and stores content (e.g. a file storage). A storage is
 * identified through the setting of a StorageIndex implementation.
 * </p>
 * <p>
 * In both cases transforms and a filter are applied to transform or filter
 * requested content. Transforms are used to transform loaded byte data into
 * another data type. A good example is the transformation from raw byte data to
 * an Image object.
 * </p>
 * <p>
 * The filter of a ContentSource is used to determine if a requested content
 * should be retrieved through this ContentSource. This is useful for storing
 * e.g. images to the file system while audio should be stored in the rms. It is
 * mandatory to use filters when multiple ContentSources are used to avoid
 * redundant storing and loading of content.
 * </p>
 * 
 * @author Andre Schmidt
 * 
 */
public abstract class ContentSource {

	final String id;

	/**
	 * the storage index
	 */
	final StorageIndex storageIndex;

	/**
	 * the listeners
	 */
	final ArrayList sources = new ArrayList();

	/**
	 * the transformers
	 */
	final HashMap transformers = new HashMap();

	/**
	 * the filter
	 */
	ContentFilter filter;

	/**
	 * Creates a new ContentSource instance with no index
	 */
	public ContentSource(String id) {
		this(id, null);
	}

	/**
	 * Creates a new ContentSource with the specified index
	 * 
	 * @param index
	 *            the StorageIndex instances
	 */
	public ContentSource(String id, StorageIndex index) {
		this.id = id;
		this.storageIndex = index;
	}

	/**
	 * Sets the source for this ContentSource and registers this ContentSource
	 * as a listener in the source
	 * 
	 * @param source
	 *            the source
	 */
	public void attachSource(ContentSource source) {
		if (this.sources.size() > 0 && !hasFiltersComplete()) {
			throw new IllegalArgumentException(
					"please add filters to all used source for " + this);
		}

		this.sources.add(source);

		//#debug debug
		info("attached source : " + source);
	}

	/**
	 * Returns true if all sources of this source have filters
	 * 
	 * @return true if all sources of this source have filters otherwise false
	 */
	boolean hasFiltersComplete() {
		for (int i = 0; i < this.sources.size(); i++) {
			ContentSource source = (ContentSource) this.sources.get(i);
			if (source.getFilter() == null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Removes this ContentSource from the listeners of the current source and
	 * sets the source to null
	 */
	public void detachSource(ContentSource source) {
		this.sources.remove(source);

		//#debug debug
		info("detached source : " + source);
	}

	public void setContentFilter(ContentFilter filter) {
		this.filter = filter;

		//#debug debug
		info("set filter : " + filter);
	}

	public ContentFilter getFilter() {
		return this.filter;
	}

	public void addContentTransform(ContentTransform transformer) {
		String id = transformer.getTransformId();
		if (!id.equals(ContentDescriptor.TRANSFORM_NONE)
				&& this.transformers.containsKey(id)) {
			//#debug info
			info("overwriting transformer with id \"" + id + "\"");
		}

		this.transformers.put(id, transformer);

		//#debug debug
		info("added transform : " + transformer);
	}

	public void removeContentTransform(ContentTransform transformer) {
		String id = transformer.getTransformId();
		this.transformers.remove(id);

		//#debug debug
		info("removed transform : " + transformers);
	}

	public Object loadContent(ContentDescriptor descriptor)
			throws ContentException {
		Object data = loadContentData(descriptor);

		if (data == null) {
			if (this.sources.size() > 0) {
				for (int index = 0; index < this.sources.size(); index++) {
					ContentSource source = (ContentSource) this.sources
							.get(index);

					ContentFilter filter = source.getFilter();
					if (filter != null && !filter.filter(descriptor)) {
						//#debug debug
						info("filtered source : " + source);
						continue;
					}

					//#debug debug
					info("load content : " + descriptor + " : with source : "
							+ source);

					data = source.loadContent(descriptor);
					if (data != null) {
						data = transformContent(descriptor, data);
						//#debug debug
						info("transformed content : " + data);

						if(descriptor.getCachingPolicy() == ContentDescriptor.CACHING_READ_WRITE) { 
							storeContent(descriptor, data);
						} else {
							//#debug debug
							info("no storage due to caching policy");
						}
						return data;
					}
				}
			} else {
				String message = this.getClass().getName()
						+ " : no source found, please add a source";
				throw new ContentException(message);
			}

			String message = this.getClass().getName()
					+ " : no source is applicable due to their filtering";
			throw new ContentException(message);
		}

		return data;
	}

	protected Object transformContent(ContentDescriptor descriptor, Object data) {
		if (this.transformers.size() > 0
				&& descriptor.getTransformID() != ContentDescriptor.TRANSFORM_NONE
				&& !(data instanceof ContentException)) {
			ContentTransform transformer = (ContentTransform) this.transformers
					.get(descriptor.getTransformID());
			if (transformer != null) {
				try {
					//#debug debug
					info("using transform : " + transformer
							+ " : for descriptor : " + descriptor);
					data = transformer.transformContent(data);
				} catch (IOException e) {
					//#debug error
					System.out.println("error transforming " + descriptor + ":"
							+ e);
				}

				return data;
			}
		}

		return data;
	}

	protected Object loadContentData(ContentDescriptor descriptor)
			throws ContentException {
		// if this source has a storage ...
		if (hasStorage()) {
			//#debug debug
			info("has storage");

			// prepare the storage if it isn't
			if (!this.storageIndex.isPrepared()) {
				this.storageIndex.prepare();

				//#debug debug
				info("storage index prepared");
			}

			// get the StorageReference to a potentially stored content
			StorageReference reference = this.storageIndex
					.getReference(descriptor);

			// if the content is stored ...
			if (reference != null) {
				//#debug debug
				info("found reference : " + reference);

				// update its activity
				reference.updateActivity();

				// load the content
				return loadContent(descriptor, reference);
			}
		}

		// just load the content
		return loadContent(descriptor, null);
	}

	/**
	 * Returns true, if this ContentSource has a StorageIndex, otherwise false
	 * 
	 * @return true, if this ContentSource has a StorageIndex, otherwise false
	 */
	boolean hasStorage() {
		return this.storageIndex != null;
	}

	/**
	 * Checks for update, checks the version and finally calls load() to load
	 * the content
	 * 
	 * @param descriptor
	 *            the content descriptor
	 * @param reference
	 *            the reference to the stored content
	 * @return the reference retrieved in load()
	 */
	protected Object loadContent(ContentDescriptor descriptor,
			StorageReference reference) throws ContentException {

		if (reference != null) {
			if (reference.getVersion() != descriptor.getVersion()) {
				//#debug info
				System.out
						.println(this.getClass().getName()
								+ " : version does not match stored content, destroying ...");

				// destroy the content if the version doesn't match
				destroyContent(descriptor);
				return null;
			}
		}

		try {
			if (reference != null) {
				//#debug debug
				info("loading content from storage : " + reference);

				return load(reference);
			} else {
				//#debug debug
				info("loading content from source : " + descriptor);
				return load(descriptor);
			}
		} catch (IOException e) {
			String message = this.getClass().getName() + " : " + e;
			throw new ContentException(message);
		}
	}
	
	/**
	 * Stores an object and returns a reference to it along with its stored size in one single step. Storages that support this should
	 * overwrite this method. Storages that do not support this should leave this method untouched.
	 * @param descriptor the content descriptor
	 * @param data the data that needs to be stored
	 * @return an Object [], the first element is an Integer object containing the stored object's data size, the second element is a reference to the stored object
	 */
	protected abstract Object[] storeContentAndGetDataSize(ContentDescriptor descriptor, Object data) throws IOException ;

	protected void storeContent(ContentDescriptor descriptor, Object data)
			throws ContentException {
		// when content is available ...
		try {
			// if this ContentSource has a storage ...
			if (hasStorage()) {
				//#debug debug
				info("storing content : " + data + " : for :" + descriptor);

				int size;
				Object reference;
				
				Object [] result = storeContentAndGetDataSize(descriptor, data);
				if ( result != null ) {
					size = ( (Integer) result[0] ).intValue();
					reference = result[1];
				} else {				
					// get the size of the content
					size = getSize(descriptor, data);
	
					//#debug debug
					info("data size for : " + data + " : " + size);
	
					// store the content
					reference = store(descriptor, data);
				}

				// add the reference to the StorageIndex
				this.storageIndex.addReference(new StorageReference(descriptor,
						size, reference));

				//#debug debug
				info("content stored with reference : " + reference);
			}
		} catch (IOException e) {
			String message = "error storing content : " + e;
			throw new ContentException(message);
		}

		// clean the storage if needed
		clean();
	}

	/**
	 * Fetches the StorageReference from the StorageIndex, removes it from the
	 * storageIndex and calls destroy() on the content
	 * 
	 * @param descriptor
	 *            the content descriptor
	 */
	protected void destroyContent(ContentDescriptor descriptor)
			throws ContentException {
		// if this ContentSource has a storage ...
		if (hasStorage()) {
			//#debug debug
			info("destroying content : " + descriptor);

			// get the reference to the content
			StorageReference reference = this.storageIndex
					.getReference(descriptor);

			if (reference != null) {
				// remove the reference from the StorageIndex
				this.storageIndex.removeReference(reference);

				try {
					// destroy the stored content
					destroy(reference);
				} catch (IOException e) {
					String message = "error destroying content : " + e;
					throw new ContentException(message);
				}
			}

			//#debug debug
			info("content destroyed : " + descriptor);
		}
	}

	/**
	 * If a clean is needed on the storage, the clean order is applied to the
	 * StorageIndex and contents are deleted until the cache size is below the
	 * thresholds
	 */
	public void clean() throws ContentException {
		if (hasStorage()) {
			if (!this.storageIndex.isCleanNeeded()) {
				return;
			}
			
			if ( this.storageIndex.size() == 0 ) {
				return;
			}

			//#debug debug
			info("clean : " + this.storageIndex);

			// apply the clean order
			this.storageIndex.applyOrder();

			do {
				// get the index of the first disposable content
				int index = this.storageIndex.getDisposableIndex();
				// get the reference
				StorageReference reference = this.storageIndex
						.getReference(index);
				// destroy the content
				destroyContent(reference);
			} while ((this.storageIndex.isCleanNeeded()));
		}
	}

	/**
	 * Returns the StorageIndex
	 * 
	 * @return the StorageIndex
	 */
	public StorageIndex getStorageIndex() {
		return this.storageIndex;
	}

	/**
	 * Returns the size for a data object.
	 * 
	 * @param data
	 *            the data
	 * @return the size of the data
	 */
	protected int getSize(ContentDescriptor descriptor, Object data)
			throws ContentException {
		int dataSize = ContentTransform.DATASIZE_UNKNOWN;

		String transformId = descriptor.getTransformID();
		ContentTransform transform = (ContentTransform) this.transformers
				.get(transformId);

		if (transform != null) {
			dataSize = transform.calculateDataSize(data);
			return dataSize;
		} else {
			if (dataSize == ContentTransform.DATASIZE_UNKNOWN) {
				if (data instanceof byte[]) {
					return ((byte[]) data).length;
				}
			}
		}

		throw new ContentException("unable to determine size of " + data);
	}

	/**
	 * Load the specified content by the use of the content descriptor.
	 * 
	 * @param descriptor
	 *            the content descriptor
	 * @return the loaded data
	 * @throws IOException
	 *             if an error occurs (obviously)
	 */
	protected abstract Object load(ContentDescriptor descriptor)
			throws IOException;

	/**
	 * Load the specified content by the use of the storage reference.
	 * 
	 * @param reference
	 *            the storage reference
	 * @return the loaded data
	 * @throws IOException
	 *             if an error occurs (obviously)
	 */
	protected abstract Object load(StorageReference reference)
			throws IOException;

	/**
	 * Stores the specified content
	 * 
	 * @param descriptor
	 *            the content descriptor
	 * @param data
	 *            the data
	 * @return the reference to the stored data
	 * @throws IOException
	 *             if an error occurs (obviously)
	 */
	protected abstract Object store(ContentDescriptor descriptor, Object data)
			throws IOException;

	/**
	 * Destroys the content specified by the storage reference
	 * 
	 * @param reference
	 *            the storage reference
	 * @throws IOException
	 *             if an error occurs (obviously)
	 */
	protected abstract void destroy(StorageReference reference)
			throws IOException;

	/**
	 * Sweeps the storage
	 * 
	 * @throws ContentException
	 *             if an error occurs
	 */
	public void sweep() throws ContentException {
		if (hasStorage()) {
			for (int index = 0; index < this.storageIndex.size(); index++) {
				StorageReference reference = this.storageIndex
						.getReference(index);

				// destroy the content
				destroyContent(reference);
			}
		}
		
		for (int index = 0; index < this.sources.size(); index++) {
			ContentSource source = (ContentSource) this.sources.get(index);
			source.sweep();
		}
	}

	/**
	 * shuts this ContentSource down. It is encouraged to overwrite this method.
	 */
	protected void shutdown() {
		if (hasStorage()) {
			this.storageIndex.shutdown();
		}

		for (int index = 0; index < this.sources.size(); index++) {
			ContentSource source = (ContentSource) this.sources.get(index);
			source.shutdown();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringHelper.createInstance("ContentSource").set("id", this.id).set(
				"sources", this.sources).set("transformers", this.transformers)
				.set("filter", this.filter).toString();
	}

	/**
	 * Prints an info
	 * 
	 * @param info
	 */
	public void info(String info) {
		System.out.println(this.id + " : " + info);
	}
}
