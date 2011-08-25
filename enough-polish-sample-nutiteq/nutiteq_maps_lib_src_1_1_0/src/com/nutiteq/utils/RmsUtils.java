package com.nutiteq.utils;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import com.nutiteq.log.Log;

public class RmsUtils {
  private static final int DEFAULT_ID = 1;

  public static final int COULD_NOT_SAVE_DATA = -1;
  public static final int COULD_NOT_OPEN_RMS = -2;

  private RmsUtils() {
  }

  public static boolean recordStorePresent(final String rsName) {
    final String[] recordStores = RecordStore.listRecordStores();

    if (recordStores == null) {
      return false;
    }

    for (int i = 0; i < recordStores.length; i++) {
      if (recordStores[i].equals(rsName)) {
        return true;
      }
    }

    return false;
  }

  public static void deleteRecordStoresWithPrefix(final String prefix) {
    final String[] recordStores = RecordStore.listRecordStores();

    if (recordStores == null) {
      return;
    }

    for (int i = 0; i < recordStores.length; i++) {
      if (recordStores[i].startsWith(prefix)) {
        try {
          RecordStore.deleteRecordStore(recordStores[i]);
        } catch (RecordStoreException ignore) {
        }
      }
    }
  }

  public static byte[] readData(final String rsName) {
    return readDataFromId(rsName, DEFAULT_ID);
  }

  public static byte[] readDataFromId(final String rsName, final int recordId) {
    byte[] result = new byte[0];
    RecordStore rs = null;
    try {
      rs = RecordStore.openRecordStore(rsName, true);
      result = rs.getRecord(recordId);
    } catch (RecordStoreException ignore) {
      //      Log.printStackTrace(ignore);
    } finally {
      closeRecordStore(rs);
    }

    return result;
  }

  public static void closeRecordStore(final RecordStore rs) {
    if (rs != null) {
      try {
        rs.closeRecordStore();
      } catch (RecordStoreException ignore) {
      }
    }
  }

  public static void setData(final String rsName, final byte[] data) {
    RecordStore rs = null;
    try {
      rs = RecordStore.openRecordStore(rsName, true);
      if (rs.getNumRecords() == 0) {
        rs.addRecord(data, 0, data.length);
      } else {
        rs.setRecord(DEFAULT_ID, data, 0, data.length);
      }
    } catch (RecordStoreException e) {
      Log.printStackTrace(e);
    } finally {
      closeRecordStore(rs);
    }
  }

  public static void deleteRecordStore(final String rsName) {
    try {
      RecordStore.deleteRecordStore(rsName);
    } catch (RecordStoreException ignore) {
      Log.printStackTrace(ignore);
    }
  }

  public static void removeRecord(final String rsName, final int recordId) {
    RecordStore rs = null;
    try {
      rs = RecordStore.openRecordStore(rsName, false);
      rs.deleteRecord(recordId);
    } catch (RecordStoreException e) {
      Log.printStackTrace(e);
    } finally {
      closeRecordStore(rs);
    }
  }

  public static int insertData(final String rsName, final byte[] data) {
    RecordStore rs = null;
    boolean rmsOpened = false;
    try {
      rs = RecordStore.openRecordStore(rsName, true);
      rmsOpened = true;
      return rs.addRecord(data, 0, data.length);
    } catch (RecordStoreException ignore) {
      //      Log.printStackTrace(ignore);
    } finally {
      closeRecordStore(rs);
    }

    return rmsOpened ? COULD_NOT_SAVE_DATA : COULD_NOT_OPEN_RMS;
  }
}
