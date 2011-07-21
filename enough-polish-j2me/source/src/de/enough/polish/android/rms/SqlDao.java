//#condition polish.android
package de.enough.polish.android.rms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import de.enough.polish.android.lcdui.AndroidDisplay;
import de.enough.polish.android.midlet.MIDlet;

/**
 * This DAO manages one database. It is a singleton and is only valid for one application.
 * TODO: How to close the database? Is there a close hook?
 * @author rickyn
 *
 */
public class SqlDao {

	private final class RecordStoreSqliteOpenHelper extends SQLiteOpenHelper {

		RecordStoreSqliteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase database2) {
			// Version 1.
			// TODO: Add upgrade mechanism to add a size field in the Record table.
			// TODO: Add an index to the tables. Check if it is needed for the PK.
			String a = "CREATE TABLE "+TABLENAME_RECORDSTORE+" (" +
					COLUMNNAME_RECORDSTORE_RECORDSTORE_PK + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
					COLUMNNAME_RECORDSTORE_NAME + " VARCHAR(30) NOT NULL," +
					COLUMNNAME_RECORDSTORE_SIZE+" INT DEFAULT 0," +
					COLUMNNAME_RECORDSTORE_NEXTID+" INT DEFAULT 1," +
					"auth_mode INT DEFAULT 0," +
					"writeable TINYINT(1) DEFAULT 0," +
					COLUMNNAME_RECORDSTORE_VERSION + " INT DEFAULT 0," +
					COLUMNNAME_RECORDSTORE_NUMBER_OF_RECORDS + " INT DEFAULT 0," +
					"timestamp INT DEFAULT 0);";
			String b = "CREATE TABLE " + TABLENAME_RECORD + " (" +
					COLUMNNAME_RECORD_RECORD_PK + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
					COLUMNNAME_RECORD_RECORDSTORE_FK + " INT NOT NULL," +
					COLUMNNAME_RECORD_DATA + " BLOB," +
					COLUMNNAME_RECORD_RECORDNUMBER + " INT NOT NULL);";
			database2.beginTransaction();
			database2.execSQL(a);
			database2.execSQL(b);
			database2.setTransactionSuccessful();
			database2.endTransaction();
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			//#debug
			System.out.println("ERROR:Call to RecordStoreSqliteOpenHelper.onUpgrade not implemented. arg1:"+arg1+". arg2:"+arg2);
		}
	}

	public static final String TABLENAME_RECORDSTORE = "recordstore";
	public static final String COLUMNNAME_RECORDSTORE_RECORDSTORE_PK = "recordstore_pk";
	public static final String COLUMNNAME_RECORDSTORE_NAME = "name";
	public static final String COLUMNNAME_RECORDSTORE_VERSION = "version";
	public static final String COLUMNNAME_RECORDSTORE_NEXTID = "nextId";
	public static final String COLUMNNAME_RECORDSTORE_NUMBER_OF_RECORDS = "number_of_records";
	public static final String COLUMNNAME_RECORDSTORE_SIZE = "current_size";
	
	public static final String TABLENAME_RECORD = "record";
	public static final String COLUMNNAME_RECORD_RECORD_PK = "record_pk";
	public static final String COLUMNNAME_RECORD_RECORDSTORE_FK = "recordstore_fk";
	// The number of the record. It is used by the API
	public static final String COLUMNNAME_RECORD_RECORDNUMBER = "record_number";
	public static final String COLUMNNAME_RECORD_DATA = "bytes";
	
	private static SQLiteDatabase database;
	private static SqlDao instance;
	
	private SqlDao() {
		init();
	}
	
	public static SqlDao getInstance() {
		// Do not make a static initialization as we need a context. We have to test at which point in the startup process the context is available.
		if(instance == null) {
			instance = new SqlDao();
		}
		return instance;
	}
	
	/**
	 * Get the record store with the given name from the database.
	 * @param recordStoreName The name of the record store. Must not be null.
	 * @return An RecordStore object or null if the record store could not be found.
	 */
	public synchronized RecordStore getRecordStore(String recordStoreName) {
		if(recordStoreName == null) {
			throw new IllegalArgumentException("The parameter 'recordStoreName' must not be null.");
		}
		Cursor resultCursor = database.query(SqlDao.TABLENAME_RECORDSTORE, null, "name = ?", new String[] {recordStoreName}, null, null, null);
		RecordStore recordStore;
		try {
			if(resultCursor.getCount() == 0) {
				return null;
			}
			recordStore = extractRecordStore(resultCursor);
		} finally {
			resultCursor.close();
		}
		return recordStore;
	}
	
	public synchronized RecordStore getRecordStore(long pk) {
		if(pk < 0) {
			throw new IllegalArgumentException("The parameter 'recordStoreName' must not have a negative value.");
		}
		Cursor result;
		RecordStore recordStore;
		result = database.query(SqlDao.TABLENAME_RECORDSTORE, null, COLUMNNAME_RECORDSTORE_RECORDSTORE_PK+" = ?", new String[] {pk+""}, null, null, null);
		try {
			if(result.getCount() == 0) {
				return null;
			}
			recordStore = extractRecordStore(result);
		} finally {
			result.close();
		}
		return recordStore;
	}

	/**
	 * This methods extracts a RecordStore object from the database cursor. This method will not close this cursor, the caller is responsible for it.
	 * @param result A Cursor object which is not closed and will not be closed by this method.
	 * @return
	 */
	private synchronized RecordStore extractRecordStore(Cursor result) {
		result.moveToFirst();
		int indexOfColumn;
		indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_RECORDSTORE_PK);
		int id = result.getInt(indexOfColumn);
		
		indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_VERSION);
		int version = result.getInt(indexOfColumn);
		
		indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_NEXTID);
		int nextId = result.getInt(indexOfColumn);
		
		indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_NAME);
		String recordStoreName = result.getString(indexOfColumn);
		
		indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_NUMBER_OF_RECORDS);
		int numberOfRecords = result.getInt(indexOfColumn);
		
		indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_SIZE);
		int size = result.getInt(indexOfColumn);
		
		RecordStore recordStore = new RecordStore(recordStoreName,id);
		recordStore.setVersion(version);
		recordStore.setNextId(nextId);
		recordStore.setNumberOfRecords(numberOfRecords);
		recordStore.setSize(size);
		
		return recordStore;
	}

	/**
	 * Creates a record store entry in the database.
	 * @param recordStoreName The name of the record store to be created. The name must be unique. The value must not be null.
	 * @return A RecordStore object.
	 * @throws RecordStoreException 
	 * @throws DaoException In case anything goes wrong. This situation is fatal.
	 */
	public synchronized RecordStore createRecordStore(String recordStoreName) throws RecordStoreException  {
		if(recordStoreName == null) {
			throw new IllegalArgumentException("The parameter 'recordStoreName' must not be null.");
		}
		ContentValues values = new ContentValues();
		values.put(COLUMNNAME_RECORDSTORE_NAME, recordStoreName);
		long id;
		try {
			database.beginTransaction();
			id = database.insertOrThrow(SqlDao.TABLENAME_RECORDSTORE, null, values);
			database.setTransactionSuccessful();
			database.endTransaction();
		} catch (SQLException e) {
			throw new RecordStoreException("Could not insert record store row with name '"+recordStoreName+"'. Reason: "+e);
		}
		if(id == -1) {
			throw new RecordStoreException("Could not insert record store row with name '"+recordStoreName+"'. Reason: The method 'SQLiteDatabase.insertOrThrow' returned '-1' instead of throwing an exception.");
		}
		RecordStore recordStore = new RecordStore(recordStoreName,id);
		return recordStore;
	}
	
	private void init() {
		Context context = AndroidDisplay.getDisplay(MIDlet.midletInstance).getContext();
		SQLiteOpenHelper recordStoreSqliteOpenHelper = new RecordStoreSqliteOpenHelper(context, "recordstoredb", null, 3);
		database = recordStoreSqliteOpenHelper.getWritableDatabase();
	}
	
	public synchronized void destroy() {
		database.close();
	}

	public synchronized String[] listRecordStores() {
		Cursor resultCursor = database.query(TABLENAME_RECORDSTORE, new String[] {COLUMNNAME_RECORDSTORE_NAME}, null, null, null, null,null);
		String[] recordStores;
		try {
			int numberOfRecordStores = resultCursor.getCount();
			recordStores = new String[numberOfRecordStores];
			for (int i = 0; i < numberOfRecordStores; i++) {
				resultCursor.move(1);
				recordStores[i] = resultCursor.getString(0);
			}
		} finally {
			resultCursor.close();
		}
		return recordStores;
	}

	public synchronized void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException {
		RecordStore recordStore = getRecordStore(recordStoreName);
		if(recordStore == null) {
			throw new RecordStoreNotFoundException("Could not delete row in table '"+TABLENAME_RECORDSTORE+"' with value '"+recordStoreName+"'");
		}
		database.beginTransaction();
		database.delete(TABLENAME_RECORDSTORE, "name = ?", new String[]{recordStoreName});
		database.delete(TABLENAME_RECORD, COLUMNNAME_RECORD_RECORDSTORE_FK+" = ?", new String[]{Long.toString(recordStore.recordStorePk)});
		database.setTransactionSuccessful();
		database.endTransaction();
	}

	/**
	 * 
	 * @param recordStoreFk the primary key of the recordstore this record is created in. This value must be valid as it is not cheched.
	 * @param data Must not be null.
	 * @return the id of the added record
	 * @throws RecordStoreException 
	 */
	public synchronized int addRecord(long recordStoreFk, byte[] data) throws RecordStoreException {
		Cursor result = database.query(SqlDao.TABLENAME_RECORDSTORE, null, COLUMNNAME_RECORDSTORE_RECORDSTORE_PK+" = ?", new String[] {recordStoreFk+""}, null, null, null);
		int version;
		int nextRecordId;
		int numberOfRecords;
		int size;
		try {
			result.moveToFirst();
			int indexOfColumn;
			indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_VERSION);
			version = result.getInt(indexOfColumn);
			
			indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_NEXTID);
			nextRecordId = result.getInt(indexOfColumn);
			
			indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_NUMBER_OF_RECORDS);
			numberOfRecords = result.getInt(indexOfColumn);
			
			indexOfColumn = result.getColumnIndex(SqlDao.COLUMNNAME_RECORDSTORE_SIZE);
			size = result.getInt(indexOfColumn);
		} finally {
			result.close();
		}
		
		// Insert record.
		ContentValues values;
		values = new ContentValues();
		values.put(COLUMNNAME_RECORD_DATA, data);
		values.put(COLUMNNAME_RECORD_RECORDNUMBER, new Integer(nextRecordId));
		values.put(COLUMNNAME_RECORD_RECORDSTORE_FK, new Long(recordStoreFk));
		try {
			database.beginTransaction();
			database.insertOrThrow(TABLENAME_RECORD, null, values);
			database.setTransactionSuccessful();
			database.endTransaction();
		} catch (SQLException e) {
			throw new RecordStoreException(e.toString());
		}
		
		// Update record store stats.
		values.clear();
		int newVersion = version + 1;
		int newNextId = nextRecordId + 1;
		int newNumberOfRecords = numberOfRecords + 1;
		int recordStoreSize = size + data.length;
		values.put(COLUMNNAME_RECORDSTORE_VERSION, new Integer(newVersion));
		values.put(COLUMNNAME_RECORDSTORE_NEXTID, new Integer(newNextId));
		values.put(COLUMNNAME_RECORDSTORE_NUMBER_OF_RECORDS, new Integer(newNumberOfRecords));
		values.put(COLUMNNAME_RECORDSTORE_SIZE, new Integer(recordStoreSize));
		try {
			database.beginTransaction();
			database.update(TABLENAME_RECORDSTORE, values, COLUMNNAME_RECORDSTORE_RECORDSTORE_PK + "= ?" , new String[] {Long.toString(recordStoreFk)});
			database.setTransactionSuccessful();
			database.endTransaction();
		} catch (Exception e) {
			throw new RecordStoreException(e.toString());
		}
		return nextRecordId;
		
	}

	/**
	 * @param recordStorePk
	 * @param recordId
	 * @return the data, or null
	 */
	public synchronized byte[] getRecord(long recordStorePk, int recordId) {
		Cursor resultCursor = database.query(TABLENAME_RECORD,new String[] {COLUMNNAME_RECORD_DATA},COLUMNNAME_RECORD_RECORDNUMBER+"=? AND "+COLUMNNAME_RECORD_RECORDSTORE_FK+"=?",new String[] {Long.toString(recordId),Long.toString(recordStorePk)},null,null,null);
		byte[] data;
		try {
			if(resultCursor.getCount() == 0) {
				return null;
			}
			resultCursor.moveToFirst();
			data = resultCursor.getBlob(0);
		} finally {
			resultCursor.close();
		}
		return data;
	}
	
	/**
	 * 
	 * @param recordStorePk
	 * @param recordId
	 * @param data Must not be null.
	 * @throws RecordStoreException
	 */
	public synchronized void setRecord(long recordStorePk, int recordId, byte[] data) throws RecordStoreException {
		ContentValues values = new ContentValues();
		values.put(COLUMNNAME_RECORD_DATA, data);
		try {
			database.beginTransaction();
			database.update(TABLENAME_RECORD, values, COLUMNNAME_RECORD_RECORDSTORE_FK + "= ? AND "+COLUMNNAME_RECORD_RECORDNUMBER+"=?" , new String[] {Long.toString(recordStorePk),Integer.toString(recordId)});
			database.setTransactionSuccessful();
			database.endTransaction();
		} catch (Exception e) {
			throw new RecordStoreException(e.toString());
		}
		RecordStore recordStore = getRecordStore(recordStorePk);
		byte[] oldData = getRecord(recordStorePk,recordId);
		values.clear();
		int size = recordStore.getSize() + - oldData.length + data.length;
		int version = recordStore.getVersion() + 1;
		values.put(COLUMNNAME_RECORDSTORE_SIZE, new Integer(size));
		values.put(COLUMNNAME_RECORDSTORE_VERSION, new Integer(version));
		database.update(TABLENAME_RECORDSTORE, values, COLUMNNAME_RECORDSTORE_RECORDSTORE_PK + "= ?" , new String[] {Long.toString(recordStorePk)});
	}

	public synchronized void removeRecord(long recordStorePk, int recordId) throws RecordStoreException {
		//TODO: Update the size in the recordstore table.
		//TODO: Cache the size in the record table.
		RecordStore recordStore = getRecordStore(recordStorePk);
		byte[] oldData = getRecord(recordStorePk,recordId);
		ContentValues values = new ContentValues();
		int size = recordStore.getSize() - oldData.length;
		int version = recordStore.getVersion() + 1;
		values.put(COLUMNNAME_RECORDSTORE_SIZE, new Integer(size));
		values.put(COLUMNNAME_RECORDSTORE_VERSION, new Integer(version));
		database.update(TABLENAME_RECORDSTORE, values, COLUMNNAME_RECORDSTORE_RECORDSTORE_PK + "= ?" , new String[] {Long.toString(recordStorePk)});
		database.delete(TABLENAME_RECORD,COLUMNNAME_RECORD_RECORDNUMBER+"=? AND "+COLUMNNAME_RECORD_RECORDSTORE_FK+"=?",new String[] {Integer.toString(recordId),Long.toString(recordStorePk)});
	}

	public synchronized int[] getRecordIdsForRecordStore(long recordStorePk) {
		Cursor resultCursor = database.query(TABLENAME_RECORD,new String[] {COLUMNNAME_RECORD_RECORDNUMBER},COLUMNNAME_RECORD_RECORDSTORE_FK+"=?",new String[] {Long.toString(recordStorePk)},null,null,COLUMNNAME_RECORD_RECORDNUMBER+" ASC");
		int[] result;
		try {
			int numberOfRecords = resultCursor.getCount();
			if(numberOfRecords == 0) {
				return new int[0];
			}
			resultCursor.moveToFirst();
			result = new int[numberOfRecords];
			for (int i = 0; i < numberOfRecords; i++) {
				result[i] = resultCursor.getInt(0);
				resultCursor.moveToNext();
			}
		} finally {
			resultCursor.close();
		}
		return result;
	}
	
	// TODO: Implement this methods so we save expensive getRecord calls when updating the size in the methods setRecord and removeRecord.
	public synchronized int getRecordSize() {
		return 0;
	}
}
