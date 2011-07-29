//#condition polish.android
package de.enough.polish.android.rms;

import de.enough.polish.util.Arrays;
import de.enough.polish.util.Comparator;

public class SqlRecordEnumeration implements RecordEnumeration{

	private final RecordStore recordStore;
	private final RecordFilter filter;
	private final RecordComparator comparator;
	private boolean keepUpdated;
	private int lastRecordIndex;
	private boolean destroyed = false;
	private SqlDao sqlDao;
	private int[] recordIds;
	
	public SqlRecordEnumeration(RecordStore recordStore, RecordFilter filter, RecordComparator comparator, boolean keepUpdated) {
		this.recordStore = recordStore;
		this.filter = filter;
		this.comparator = comparator;
		this.keepUpdated = keepUpdated;
		this.sqlDao = SqlDao.getInstance();
		rebuild();
	}
	
	public void destroy() {
		this.destroyed = true;
	}

	public boolean hasNextElement() {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		boolean hasNext = this.lastRecordIndex < this.recordIds.length-1;
		return hasNext;
	}

	public boolean hasPreviousElement() {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		boolean hasPrevious = this.lastRecordIndex > 0;
		return hasPrevious;
	}

	public boolean isKeptUpdated() {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		return this.keepUpdated;
	}

	public void keepUpdated(boolean keepUpdated2) {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		this.keepUpdated = keepUpdated2;
	}
	
	private byte[] load(int recordId) {
		return this.sqlDao.getRecord(this.recordStore.getPk(),recordId);
	}


	public byte[] nextRecord() throws InvalidRecordIDException,RecordStoreNotOpenException, RecordStoreException {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		if(this.recordStore.isClosed()) {
			throw new RecordStoreNotOpenException("The record store which is enumerated is closed.");
		}
		int nextRecordIndex = this.lastRecordIndex + 1;
		if(nextRecordIndex >= this.recordIds.length) {
			throw new InvalidRecordIDException("The end of the enumeration is reached.");
		}
		this.lastRecordIndex = nextRecordIndex;
		int recordId = this.recordIds[nextRecordIndex];
		byte[] result = load(recordId);
		return result;
	}

	public int nextRecordId() throws InvalidRecordIDException {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		int nextRecordIndex = this.lastRecordIndex + 1;
		if(nextRecordIndex >= this.recordIds.length) {
			throw new InvalidRecordIDException("No more records in this enumeration.");
		}
		this.lastRecordIndex = nextRecordIndex;
		int nextRecordId = this.recordIds[nextRecordIndex];
		return nextRecordId;
	}

	public int numRecords() {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		return this.recordIds.length;
	}

	public byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		if(this.recordStore.isClosed()) {
			throw new RecordStoreNotOpenException("The record store which is enumerated is closed.");
		}
		int previousRecordIndex = this.lastRecordIndex - 1;
		if(previousRecordIndex < 0) {
			throw new InvalidRecordIDException("The start of the enumeration is reached.");
		}
		this.lastRecordIndex = previousRecordIndex;
		int recordId = this.recordIds[previousRecordIndex];
		byte[] result = load(recordId);
		return result;
	}

	public int previousRecordId() throws InvalidRecordIDException {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		int previousIndex = this.lastRecordIndex - 1;
		if(this.lastRecordIndex < 0) {
			throw new InvalidRecordIDException("The start of the enumeration is reached.");
		}
		this.lastRecordIndex = previousIndex;
		int nextRecordId = this.recordIds[previousIndex];
		return nextRecordId;
	}

	public void rebuild() {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		reset();
		this.recordIds = this.sqlDao.getRecordIdsForRecordStore(this.recordStore.getPk());
		filter();
		sort();
	}


	public void reset() {
		if(isDestroyed()) {
			throw new IllegalStateException("This RecordEnumeration instance is destroyed.");
		}
		this.lastRecordIndex = -1;
		
	}

	private boolean isDestroyed() {
		return this.destroyed;
	}

	private void filter() {
		if(this.filter == null) {
			return;
		}
		int deleteCount = 0;
		// Remove recordIds which do not match the filter.
		// Do this by invalidating not matching ids instead of harvesting the matching ones in a list. This way we safe a lot of object creations when filling the list with Integer objects.
		int numberOfRecords = this.recordIds.length;
		for (int currentIndex = 0; currentIndex < numberOfRecords; currentIndex++) {
			int currentRecordId = this.recordIds[currentIndex];
			byte[] currentData = this.sqlDao.getRecord(this.recordStore.getPk(),currentRecordId);
			boolean filterMatches = this.filter.matches(currentData);
			if( ! filterMatches) {
				this.recordIds[currentIndex] = -1;
				deleteCount++;
			}
		}
		if(deleteCount > 0) {
			int newNumberOfRecords = numberOfRecords-deleteCount;
			int[] temp = new int[newNumberOfRecords];
			int tempIndex = 0;
			for (int currentIndex = 0; currentIndex < numberOfRecords; currentIndex++) {
				int recordId = this.recordIds[currentIndex];
				if(recordId != -1) {
					temp[tempIndex] = recordId;
					tempIndex++;
				}
			}
			this.recordIds = temp;
		}
	}
	
	private void sort() {
		if (this.comparator == null) {
			return;
		}
		
		int[] ids = this.recordIds;
		Integer[] sortableIds = new Integer[ids.length];
		for (int i = 0; i < sortableIds.length; i++) {
			sortableIds[i] = new Integer( ids[i] );
		}
		
		Arrays.sort(sortableIds, new RecordComparatorWrapper(this.comparator));
		
		for (int i = 0; i < sortableIds.length; i++) {
			ids[i] = sortableIds[i].intValue();
		}
		
		this.recordIds = ids;
	}

	
	
	private class RecordComparatorWrapper implements Comparator {
		
		private RecordComparator recordComparator;
		
		
		public RecordComparatorWrapper(RecordComparator recordComparator) {
			this.recordComparator = recordComparator;
		}

		public int compare(Object o1, Object o2) {
			Integer id1 = (Integer) o1;
			Integer id2 = (Integer) o2;
			byte[] record1 = SqlRecordEnumeration.this.load( id1.intValue() );
			byte[] record2 = SqlRecordEnumeration.this.load( id2.intValue() );
			return this.recordComparator.compare(record1, record2);
		}
		
	}

}
