package de.enough.polish.content;

import java.io.IOException;

import de.enough.polish.content.source.ContentSource;
import de.enough.polish.content.storage.StorageIndex;
import de.enough.polish.content.storage.StorageReference;
import de.enough.polish.util.IntHashMap;

/**
 * An implementation of ContentSource that uses the memory to store and load
 * content. This should be used as the main entry point for retrieving contents.
 * 
 * Contents can be retrieved through synchronous and asynchronous means.
 * loadContent() blocks and returns content while requestContent() retrieves the
 * content asynchronously and notifies listening instances through a registered
 * listener.
 * 
 * @see ContentSource
 * @author Andre Schmidt
 */
public class ContentLoader extends ContentSource implements Runnable {

	/**
	 * A queue implementation to store and retrieve descriptions for requested
	 * content
	 * 
	 * @author Andre
	 * 
	 */
	private class ContentQueue {
		private QueueElement root;

		/**
		 * Constructs an empty queue.
		 */
		public ContentQueue() {
			this.root = new QueueElement();
			this.root.content = null;
			this.root.prev = this.root;
			this.root.next = this.root;
		}

		/**
		 * Checks if the queue is empty.
		 * @return true when the loader queue is empty
		 */
		public synchronized boolean isEmpty() {
			return (this.root.content == null);
		}

		/**
		 * Adds an object to the queue.
		 * @param o the object to be added
		 */
		public synchronized void push(Object o) {
			if (this.root.content == null) {
				this.root.content = o;
			} else {
				QueueElement e = new QueueElement();
				e.content = o;
				this.root.prev.next = e;
				e.next = this.root;
				e.prev = this.root.prev;
				this.root.prev = e;
			}
		}

		/**
		 * Takes the next object from the queue.
		 * @return the next object
		 */
		public synchronized Object pop() {
			if (this.root.content == null) {
				return null;
			}

			Object o = this.root.content;
			remove(this.root);
			this.root = this.root.next;
			return o;
		}

		/**
		 * Returns true if the specified object is in the queue, otherwise false
		 * 
		 * @param obj
		 *            the object
		 * @return true if the specified object is in the queue, otherwise false
		 */
		public synchronized boolean contains(Object obj) {
			for (QueueElement e = this.root.next; (e != this.root); e = e.next) {
				if (e.content.equals(obj)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Removes a given object from the queue.
		 * @param o the object
		 * @throws InterruptedException 
		 */
		public synchronized void cancel(Object o) throws InterruptedException {
			if (this.root.content != null) {
				if (this.root.content.equals(o)) {
					pop();
				} else {
					for (QueueElement e = this.root.next; (e != this.root); e = e.next) {
						if (e.content.equals(o)) {
							remove(e);
							break;
						}
					}
				}
			}
		}

		/**
		 * Returns the object that was added most recently. 
		 * Doesn't wait if the queue is empty.
		 * @return the Object that was added most recently
		 */
		protected Object latest() {
			return (this.root.content == null) ? null : this.root.prev.content;
		}

		/**
		 * Removes an element from the queue.
		 * 
		 * @param e
		 *            the element
		 */
		private void remove(QueueElement e) {
			e.content = null;
			e.prev.next = e.next;
			e.next.prev = e.prev;
		}
	}

	private class QueueElement {
		Object content;
		QueueElement prev;
		QueueElement next;
	}
	
	/**
	 * The main id
	 */
	public final static String ID = "ContentLoader";
	
	/**
	 * default size of the cache (100,000)
	 */
	public final static int LOADER_CACHE_SIZE = 100000;

	/**
	 * The memory cache
	 */
	final IntHashMap cache = new IntHashMap();

	/**
	 * The listeners that requested content
	 */
	final IntHashMap listeners = new IntHashMap();

	/**
	 * The lock for the asynchronous content retrieval
	 */
	Object lock = new Object();

	/**
	 * The queue for the asynchronous content retrieval
	 */
	ContentQueue queue;

	/**
	 * The thread asynchronous content retrieval
	 */
	Thread thread;

	/**
	 * Flag to indicate a requested shutdown
	 */
	boolean shutdown = false;

	/**
	 * Creates a new ContentLoader instance with a default StorageIndex
	 */
	public ContentLoader() {
		super(ID, new StorageIndex(LOADER_CACHE_SIZE));
	}

	/**
	 * Creates a new ContentLoader instance with a default Storage index and a
	 * ContentSource
	 * 
	 * @param source
	 *            the ContentSource
	 */
	public ContentLoader(ContentSource source) {
		this(new StorageIndex(LOADER_CACHE_SIZE), source);
	}

	/**
	 * Creates a new ContentLoader instance with the given StorageIndex and a given 
	 * ContentSource instance
	 * 
	 * @param index
	 *            the StorageIndex instance 
	 * @param source
	 *            the ContentSource instance 
	 */
	public ContentLoader(StorageIndex index, ContentSource source) {
		super(ID, index);

		attachSource(source);
	}

	/**
	 * Request the content described throught the given ContentDescriptor
	 * 
	 * @param descriptor
	 *            the ContentDescriptor
	 * @param listener
	 *            the ContentListener
	 */
	public synchronized void requestContent(ContentDescriptor descriptor,
			ContentListener listener) {
		if (this.thread == null) {
			// create thread if not already ...
			this.queue = new ContentQueue();
			this.thread = new Thread(this);
			// ... and start it
			this.thread.start();
		}

		// if content isn't already requested
		if (this.listeners.get(descriptor.getHash()) == null) {
			//#debug debug
			System.out.println("requesting content for " + descriptor);

			// store the listener
			this.listeners.put(descriptor.getHash(), listener);
			// push the decriptor to the queue ...
			this.queue.push(descriptor);

			synchronized (this.lock) {
				// ... and notify the thread
				this.lock.notify();
			}
		} else {
			//#debug debug
			System.out.println("content is already requested");
		}
	}
	
	/**
	 * Cancels the request of the content described throught the given ContentDescriptor
	 * 
	 * @param descriptor
	 *            the ContentDescriptor
	 */
	public synchronized void cancelContent(ContentDescriptor descriptor) {
		try {
			synchronized (this.lock) {
				this.queue.cancel(descriptor);
				this.listeners.remove(descriptor.getHash());
			}
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		do {
			synchronized (this.lock) {
				try {
					if (this.queue.isEmpty()) {
						// wait until notified through requestContent()
						this.lock.wait();
					}
				} catch (InterruptedException e) {
					// ignored
				}
			}

			if (!this.shutdown && !this.queue.isEmpty()) {
				// pop the first descriptor from the queue
				ContentDescriptor descriptor = (ContentDescriptor) this.queue
						.pop();

				// retrieve the listener
				ContentListener listener = (ContentListener) this.listeners
						.get(descriptor.getHash());
				
				if(listener != null) {
					try {
						// load the content
						Object data = loadContent(descriptor);
						// notify the listener that content is loaded
						listener.onContentLoaded(descriptor, data);
					} catch (Exception e) {
						// notify the listener that an error occured
						listener.onContentError(descriptor, e);
					} finally {
						this.listeners.remove(descriptor.getHash());
					}
				}
			}

		// repeat until a shutdown is requested
		} while (!this.shutdown);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zyb.nowplus.business.content.ContentSource#destroy(com.zyb.nowplus
	 * .business.content.StorageReference)
	 */
	protected void destroy(StorageReference reference) {
		this.cache.remove(reference.getHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zyb.nowplus.business.content.ContentSource#load(com.zyb.nowplus.business
	 * .content.ContentDescriptor,
	 * com.zyb.nowplus.business.content.StorageReference)
	 */
	protected Object load(StorageReference reference) {
		return this.cache.get(reference.getHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zyb.nowplus.business.content.ContentSource#load(com.zyb.nowplus.business
	 * .content.ContentDescriptor,
	 * com.zyb.nowplus.business.content.StorageReference)
	 */
	protected Object load(ContentDescriptor descriptor) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zyb.nowplus.business.content.ContentSource#store(com.zyb.nowplus.
	 * business.content.ContentDescriptor, java.lang.Object)
	 */
	protected Object store(ContentDescriptor descriptor, Object data) {
		this.cache.put(descriptor.getHash(), data);
		return null;
	}
	
	protected Object[] storeContentAndGetDataSize(ContentDescriptor descriptor,
			Object data) throws IOException {
		// Do nothing, this is a source
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zyb.nowplus.business.content.ContentSource#shutdown()
	 */
	public void shutdown() {
		this.shutdown = true;

		synchronized (this.lock) {
			this.lock.notify();
		}

		// shutdown attached sources
		super.shutdown();
	}
}
