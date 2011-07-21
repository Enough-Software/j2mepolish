//#condition polish.blackberry
package de.enough.polish.blackberry.persistent;

/**
 * Provides constraints for the record store management
 * @author Andre
 *
 */
public class PersistentRecordStoreConstraints {
	public static long RECORDSTOREINDEX_ID = Long.MAX_VALUE;
	
	public static long RECORDSTORE_RANGE = 10000000;
	
	public static int RECORDSTORE_MAX_SIZE = 10000000 - 1;
	
	public static int RECORDSTORE_MAX_NUMBER = 1000; 
	
	public static long RECORDSTORE_OFFSET = 0;
}
