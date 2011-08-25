package com.nutiteq.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CacheIndexDatabaseHelper {
  private static final int DATABASE_VERSION = 1;
  private static final String CACHE_INDEX_TABLE = "cache_index";
  private static final String CREATE_CACHE_INDEX_TABLE = "CREATE TABLE "
      + CACHE_INDEX_TABLE
      + " (id INTEGER PRIMARY KEY AUTOINCREMENT, cache_key TEXT NOT NULL, "
      + "resource_path TEXT_NOT_NULL, resource_size INTEGER NOT NULL, used_timestamp INTEGER NOT NULL)";
  private final Context ctx;
  private DatabaseHelper databaseHelper;
  private final String databaseName;
  private SQLiteDatabase database;

  private static final String KEY_ID = "id";
  private static final String KEY_CACHE_KEY = "cache_key";
  private static final String KEY_RESOURCE_PATH = "resource_path";
  private static final String KEY_RESOURCE_SIZE = "resource_size";
  private static final String KEY_USED_TIMESTAMP = "used_timestamp";

  private static final String LOG_TAG = "CacheIndexDatabaseHelper";

  private static class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(final Context context, final String databaseName) {
      super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
      db.execSQL(CREATE_CACHE_INDEX_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
      onCreate(db);
    }
  }

  public CacheIndexDatabaseHelper(final Context ctx, final String databaseName) {
    this.ctx = ctx;
    this.databaseName = databaseName;
  }

  public void open() {
    databaseHelper = new DatabaseHelper(ctx, databaseName);
    database = databaseHelper.getWritableDatabase();
  }

  public void close() {
    databaseHelper.close();
  }

  public boolean containsKey(final String cacheKey) {
    final long start = System.currentTimeMillis();
    final Cursor c = database.query(CACHE_INDEX_TABLE, new String[] { KEY_ID }, "cache_key = ?",
        new String[] { cacheKey }, null, null, null);
    final boolean hasKey = c.moveToFirst();
    c.close();
    Log.d(LOG_TAG, "execution time " + (System.currentTimeMillis() - start));
    return hasKey;
  }

  public List<String> addToIndex(final String cacheKey, final String resourcePath,
      final int resourceSize, final int cacheSize) {
    final ContentValues values = new ContentValues();
    values.put(KEY_CACHE_KEY, cacheKey);
    values.put(KEY_RESOURCE_PATH, resourcePath);
    values.put(KEY_RESOURCE_SIZE, resourceSize);
    values.put(KEY_USED_TIMESTAMP, System.currentTimeMillis());
    database.insert(CACHE_INDEX_TABLE, null, values);

    final Cursor c = database.rawQuery("SELECT SUM(resource_size) FROM cache_index", null);
    int totalSize = 0;
    if (c.moveToFirst()) {
      totalSize = c.getInt(0);
    }
    c.close();

    Log.d(LOG_TAG, "maxSize = " + cacheSize + " currentSize = " + totalSize);
    if (totalSize < cacheSize) {
      return new ArrayList<String>();
    } else {
      return reduceCacheSize(totalSize - cacheSize);
    }
  }

  private List<String> reduceCacheSize(final int bytesNeededToFree) {
    final Cursor c = database.query(CACHE_INDEX_TABLE, new String[] { KEY_RESOURCE_PATH,
        KEY_RESOURCE_SIZE }, null, null, null, null, "used_timestamp ASC");
    final List<String> removedFiles = new ArrayList<String>();

    int moreBytesNeeded = bytesNeededToFree;
    while (c.moveToNext() && moreBytesNeeded > 0) {
      removedFiles.add(c.getString(0));
      moreBytesNeeded -= c.getInt(1);
    }
    c.close();

    deleteFilesFromIndex(removedFiles);

    return removedFiles;
  }

  private void deleteFilesFromIndex(final List<String> removedFiles) {
    final String[] files = removedFiles.toArray(new String[removedFiles.size()]);
    final StringBuffer whereClause = new StringBuffer();
    for (int i = 0; i < files.length; i++) {
      whereClause.append(KEY_RESOURCE_PATH).append(" = ?");
      if (i != files.length - 1) {
        whereClause.append(" OR ");
      }
    }

    database.beginTransaction();
    final int affectedRows = database.delete(CACHE_INDEX_TABLE, whereClause.toString(), files);
    database.setTransactionSuccessful();
    database.endTransaction();
    Log.d(LOG_TAG, "Needed to delete " + removedFiles.size() + ", deleted " + affectedRows);
  }

  public String getRespourcePathForKey(final String cacheKey) {
    final Cursor c = database.query(CACHE_INDEX_TABLE, new String[] { KEY_RESOURCE_PATH },
        KEY_CACHE_KEY + " = ?", new String[] { cacheKey }, null, null, null);

    String resourcePath = "";
    if (c.moveToFirst()) {
      resourcePath = c.getString(0);
    }
    c.close();

    return resourcePath;
  }
}
