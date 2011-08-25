package javax.microedition.rms;

public class RecordStore {
  public int addRecord(final byte[] data, final int offset, final int numBytes)
      throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
    return -1;
  }

  public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {

  }

  public void deleteRecord(final int recordId) throws RecordStoreNotOpenException,
      InvalidRecordIDException, RecordStoreException {

  }

  public static void deleteRecordStore(final String recordStoreName) throws RecordStoreException,
      RecordStoreNotFoundException {

  }

  public int getNumRecords() throws RecordStoreNotOpenException {
    return 0;
  }

  public byte[] getRecord(final int recordId) throws RecordStoreNotOpenException,
      InvalidRecordIDException, RecordStoreException {
    return null;
  }

  public static String[] listRecordStores() {
    return null;
  }

  public static RecordStore openRecordStore(final String recordStoreName,
      final boolean createIfNecessary) throws RecordStoreException, RecordStoreFullException,
      RecordStoreNotFoundException {
    return null;
  }

  public void setRecord(final int recordId, final byte[] newData, final int offset,
      final int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException,
      RecordStoreException, RecordStoreFullException {

  }

  public RecordEnumeration enumerateRecords(final RecordFilter filter,
      final RecordComparator comparator, final boolean keepUpdated)
      throws RecordStoreNotOpenException {
    return null;
  }
}
