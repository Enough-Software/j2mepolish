package com.nutiteq.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;
import com.nutiteq.utils.RmsUtils;

/**
 * <p>
 * Caching inside record stores. Uses a collection of record stores (buckets)
 * for caching. Record stores are created for every bucket and cache index.
 * </p>
 * <p>
 * Implemented with LRU (least recently used) strategy.
 * </p>
 * <p>
 * Values given in constructor are for maximum bucket size and number of
 * buckets. If phone limits are smaller, then this implementation adapts to
 * it.<br /> For example cache with five buckets of size 64kB is created, but
 * phone has a max limit of three record stores for application with maximum
 * size of 30kB. Then we will have a cache with two buckets (one will be used
 * for index) with total size of 60kB.
 * </p>
 * <p>
 * <strong>Memory usage estimates</strong>
 * </p>
 * <p>
 * Cache holds a index of all files in memory and based on tile size and tile
 * URL length it can differ significantly.
 * </p>
 * <p>
 * With OpenStreetMap tiles index size with 64kB bucket would be around 500-600
 * bytes. Bucket would contain 5-6 10kB images and for every image we would need
 * to have in memory tile URL for cache key, bucket location and there is some
 * object overhead.
 * </p>
 * <p>
 * With CloudMade tiles cache index size for 64kB bucket would be ~10kB.
 * CloudMade images are a lot smaller (usually around 500B) and this also
 * increases number of URLs needed for cache keys.
 * </p>
 */
public class RmsCache implements Cache {
  private static final String CACHE_INDEX_SUFFIX = "_index";

  private final String cachePrefix;
  private final int maxBucketSize;
  private final int numberOfBuckets;
  private final int bucketSize[];
  private final int bucketElements[];
  private final boolean bucketUsable[];

  private final Hashtable index;

  private RmsCacheItem mru;
  private RmsCacheItem lru;

  /**
   * Creates a new record stores cache with given number of cache buckets.
   * 
   * @param cachePrefix
   *          prefix for cache
   * @param maxBucketSize
   *          maximum bucket size in bytes
   * @param numberOfBuckets
   *          number of buckets to be used
   */
  public RmsCache(final String cachePrefix, final int maxBucketSize, final int numberOfBuckets) {
    this.cachePrefix = cachePrefix;
    this.maxBucketSize = maxBucketSize;
    this.numberOfBuckets = numberOfBuckets;
    bucketSize = new int[numberOfBuckets];
    bucketElements = new int[numberOfBuckets];
    index = new Hashtable();
    bucketUsable = new boolean[numberOfBuckets];
    for (int i = 0; i < numberOfBuckets; i++) {
      bucketUsable[i] = true;
    }
  }

  public void initialize() {
    if (!RmsUtils.recordStorePresent(cachePrefix + CACHE_INDEX_SUFFIX) || !readCacheIndex()) {
      RmsUtils.deleteRecordStoresWithPrefix(cachePrefix);
      resetData();
    }
    RmsUtils.deleteRecordStore(cachePrefix + CACHE_INDEX_SUFFIX);
    RmsUtils.setData(cachePrefix + CACHE_INDEX_SUFFIX, new byte[] { 1 });
  }

  private void resetData() {
    for (int i = 0; i < numberOfBuckets; i++) {
      bucketSize[i] = 0;
      bucketElements[i] = 0;
      index.clear();
    }
  }

  public byte[] get(final String cacheId) {
    final RmsCacheItem item = (RmsCacheItem) index.get(cacheId);

    if (item == null) {
      return null;
    }

    final byte[] data = RmsUtils.readDataFromId(cachePrefix + item.bucket, item.recordId);

    makeFirst(item);

    return data;
  }

  private void makeFirst(final RmsCacheItem item) {
    if (mru == null && lru == null) {
      mru = lru = item;
      return;
    }

    //TODO jaanus : copy/paste
    //make it the most recently used entry
    if (mru != item) { //not already the MRU
      if (lru == item) { // I'm the least recently used
        lru = item.previous;
      }

      // Remove myself from the LRU list.
      if (item.next != null) {
        item.next.previous = item.previous;
      }

      if (item.previous != null) {
        item.previous.next = item.next;
      }

      // Add myself back in to the front.
      mru.previous = item;
      item.previous = null;
      item.next = mru;
      mru = item;
    }
  }

  public void cache(final String cacheId, final byte[] data, final int cacheLevel) {
    if ((cacheLevel & CACHE_LEVEL_PERSISTENT) != CACHE_LEVEL_PERSISTENT || data == null || data.length == 0) {
      return;
    }

    final int dataLength = data.length;
    int availableBucket;
    while ((availableBucket = findAvailable(0, dataLength)) == -1) {
      kickLRU();
    }

    int recordId;
    while ((recordId = RmsUtils.insertData(cachePrefix + availableBucket, data)) <= -1) {
      if (recordId == RmsUtils.COULD_NOT_OPEN_RMS) {
        bucketUsable[availableBucket] = false;
      }

      if (bucketElements[availableBucket] == 0) {
        //all elements in bucket kicked out, but still does not fit. giving up
        break;
      }

      kickLRU();
      availableBucket = findAvailable(availableBucket + 1, dataLength);
    }

    if (recordId <= -1) {
      return;
    }

    final RmsCacheItem item = new RmsCacheItem();
    item.bucket = availableBucket;
    item.dataLength = dataLength;
    item.key = cacheId;
    item.recordId = recordId;

    index.put(cacheId, item);
    bucketSize[availableBucket] += dataLength;
    bucketElements[availableBucket] += 1;

    makeFirst(item);
  }

  private void kickLRU() {
    if (lru == null) {
      return;
    }
    // Kick out the least recently used element.
    index.remove(lru.key);
    RmsUtils.removeRecord(cachePrefix + lru.bucket, lru.recordId);

    bucketSize[lru.bucket] -= lru.dataLength;
    bucketElements[lru.bucket] -= 1;

    if (lru.previous != null) {
      lru.previous.next = null;
    }

    lru = lru.previous;

    //TODO jaanus : check this
    if (lru == null) {
      mru = null;
    }
  }

  private int findAvailable(final int startIndex, final int dataLength) {
    //make loop over buckets
    int checkedBucket = startIndex < numberOfBuckets ? startIndex : 0;
    boolean loopDone = false;
    do {
      if (bucketUsable[checkedBucket]
          && (bucketElements[checkedBucket] == 0 || bucketSize[checkedBucket] + dataLength <= maxBucketSize)) {
        return checkedBucket;
      }

      checkedBucket++;
      if (checkedBucket == numberOfBuckets) {
        checkedBucket = 0;
      }

      if (checkedBucket == startIndex) {
        loopDone = true;
      }
    } while (!loopDone);

    return -1;
  }

  private boolean readCacheIndex() {
    final byte[] indexData = RmsUtils.readData(cachePrefix + CACHE_INDEX_SUFFIX);
    if (indexData == null || indexData.length == 0) {
      return false;
    }

    ByteArrayInputStream bais = null;
    DataInputStream dis = null;
    try {
      bais = new ByteArrayInputStream(indexData);
      dis = new DataInputStream(bais);
      for (int i = 0; i < numberOfBuckets; i++) {
        bucketSize[i] = dis.readInt();
        bucketElements[i] = dis.readInt();
      }
      final int elements = dis.readInt();
      for (int i = 0; i < elements; i++) {
        final RmsCacheItem read = new RmsCacheItem();
        read.dataLength = dis.readInt();
        read.bucket = dis.readInt();
        read.recordId = dis.readInt();
        read.key = dis.readUTF();
        makeFirst(read);
        index.put(read.key, read);
      }
      return true;
    } catch (final Exception e) {
      Log.printStackTrace(e);
    } finally {
      IOUtils.closeStream(dis);
      IOUtils.closeStream(bais);
    }

    return false;
  }

  public void deinitialize() {
    writeIndex();
  }

  private boolean writeIndex() {
    ByteArrayOutputStream baos;
    DataOutputStream dos;
    try {
      baos = new ByteArrayOutputStream();
      dos = new DataOutputStream(baos);
      for (int i = 0; i < numberOfBuckets; i++) {
        dos.writeInt(bucketSize[i]);
        dos.writeInt(bucketElements[i]);
      }
      dos.writeInt(index.size());
      RmsCacheItem writing = lru;
      do {
        dos.writeInt(writing.dataLength);
        dos.writeInt(writing.bucket);
        dos.writeInt(writing.recordId);
        dos.writeUTF(writing.key);
      } while ((writing = writing.previous) != null);

      final byte[] data = baos.toByteArray();
      RmsUtils.setData(cachePrefix + CACHE_INDEX_SUFFIX, data);
    } catch (final Exception ignore) {
      return false;
    }

    return true;
  }

  //TEST METHODS
  protected int getCalculatedSize(final int bucket) {
    return bucketSize[bucket];
  }

  protected RmsCacheItem getMRU() {
    return mru;
  }

  protected int getTotalItemsCount() {
    int result = 0;
    for (int i = 0; i < bucketElements.length; i++) {
      result += bucketElements[i];
    }
    return result;
  }

  public boolean contains(final String cacheKey) {
    return index.containsKey(cacheKey);
  }

  public boolean contains(final String cacheKey, final int cacheLevel) {
    if ((cacheLevel & CACHE_LEVEL_PERSISTENT) != CACHE_LEVEL_PERSISTENT) {
      return false;
    }

    return contains(cacheKey);
  }
}
