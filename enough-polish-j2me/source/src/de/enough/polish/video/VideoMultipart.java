//#condition polish.usePolishGui && polish.api.mmapi
package de.enough.polish.video;

import de.enough.polish.util.ArrayList;

/**
 * Provides a storage for multiple video parts
 * that are played as a whole in the VideoContainer
 * @author Andre Schmidt
 */
public class VideoMultipart {
	/**
	 * the storage
	 */
	ArrayList sources;
	
	/**
	 * the current part
	 */
	int current;
	
	/**
	 * Constructs a new VideoMultipart instance
	 */
	public VideoMultipart()
	{
		this.sources = new ArrayList();
		this.current = -1;
	}
	
	/**
	 * Adds a VideoSource instance to the storage
	 * @param source the VideoSource instance
	 */
	public void addSource(VideoSource source)
	{
		this.sources.add(source);
	}
	
	/**
	 * Removes a VideoSource instance from the storage
	 * @param source the VideoSource instance
	 */
	public void removeSource(VideoSource source)
	{
		this.sources.remove(source);
	}
	
	/**
	 * Returns the VideoSource instance at the given index
	 * @param index the index
	 * @return the VideoSource instance
	 */
	public VideoSource source(int index)
	{
		return (VideoSource)this.sources.get(index);
	}
	
	/**
	 * Returns true if there are more VideoSource instances in the storage
	 * @return true if there are more VideoSource instances in the storage, otherwise false
	 */
	public boolean hasNext()
	{
		return this.current < (this.sources.size() - 1);
	}
	
	/**
	 * Returns the next VideoSource instance in the storage
	 * @return the next VideoSource instance
	 */
	public VideoSource next()
	{
		this.current++;
		return source(this.current);
	}
	
	/**
	 * Reset the current index to 0
	 */
	public void reset()
	{
		this.current = -1;
	}
	
	/**
	 * Closes all VideoSource instances in the storage
	 */
	public void close()
	{
		for(int i=0; i<this.sources.size();i++)
		{
			source(i).close();
		}
	}
}
