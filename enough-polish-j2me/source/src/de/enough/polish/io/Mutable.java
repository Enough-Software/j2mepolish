package de.enough.polish.io;

public interface Mutable 
extends Externalizable 
{
	/**
	 * Determines if this element has been changed after it has been read or written the last time.
	 * @return true when it has changed and requires storage
	 */
	boolean isDirty();
}
