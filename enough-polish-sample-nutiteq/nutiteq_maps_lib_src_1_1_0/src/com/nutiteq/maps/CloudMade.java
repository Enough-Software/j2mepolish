package com.nutiteq.maps;

import javax.microedition.io.HttpConnection;

import com.nutiteq.cache.Cache;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.log.Log;
import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.net.DataPostingDownloadable;
import com.nutiteq.task.RetrieveNetworkResourceTask;
import com.nutiteq.task.Task;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

/**
 * Map for using CloudMade maps (with tile size 64 and 256).
 */
public class CloudMade extends EPSG3785 implements GeoMap, UnstreamedMap {
  private static final String BASEURL = "http://tile.cloudmade.com/";
  private static final String AUTHURL = "http://auth.cloudmade.com/";
  private final String licenseKey;
  private final String userid;
  private String token; // not static. otherwise user cannot change with new instance
  private final int mapLayout;

  public static final int TILE_SIZE_64 = 64;
  public static final int TILE_SIZE_256 = 256;

  private static final int MIN_ZOOM = 0;
  private static final int MAX_ZOOM_256 = 18;
  private static final int MAX_ZOOM_64 = 20;

  /**
   * Constructor for CloudMade map.
   * 
   * @param licenseKey
   *          license key issued by CloudMade
   * @param tileSize
   *          used tile size (64 or 256)
   * @param mapLayout
   *          used map layout (currently only 1 is supported by CloudMade)
   */
  public CloudMade(final String licenseKey, final String userid, final int tileSize, final int mapLayout) {
    this(new StringCopyright("CloudMade"), licenseKey, userid, tileSize, mapLayout);
  }

  public CloudMade(final Copyright copyright, final String licenseKey, final String userid, final int tileSize,
      final int mapLayout) {
    super(copyright, tileSize, MIN_ZOOM, tileSize == TILE_SIZE_64 ? MAX_ZOOM_64 : MAX_ZOOM_256);
    this.licenseKey = licenseKey;
    this.userid = userid;
    this.mapLayout = mapLayout;
    this.token = null;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final StringBuffer result = new StringBuffer(BASEURL);

    result.append(licenseKey);
    result.append("/");
    result.append(mapLayout);
    result.append("/");
    result.append(getTileSize());
    result.append("/");

    result.append(zoom);
    result.append('/');
    result.append(mapX / getTileSize()& ((1 << zoom) - 1));
    result.append('/');
    result.append(mapY / getTileSize());
    result.append(".png?token=").append(token);
    return result.toString();
  }
  
  /**
   * @return CloudMade token, usable for other services
   */
  public String getCloudMadeToken(){
    return token;
  }
  
  public Task getInitializationTask() {
    if (token == null) {
      CloudMadeTokenRequest req = new CloudMadeTokenRequest();
      return new RetrieveNetworkResourceTask(req, null, req.getCachingLevel());
    }
    return null;
    
  }
  
  private class CloudMadeTokenRequest implements DataPostingDownloadable, ResourceRequestor, ResourceDataWaiter {

    public String getContentType() {
      return "application/x-www-form-urlencoded";
    }

    public String getPostContent() {
      return "apikey=" + licenseKey + "&userid=" + userid;
    }

    public String getUrl() {
      return AUTHURL + "token/" + licenseKey;
    }

    public void notifyError() {
      Log.error("CloudMade token request failed!");
    }

    public int getCachingLevel() {
      return Cache.CACHE_LEVEL_NONE;
    }

    public String resourcePath() {
      return getUrl(); // not used for DataPostingDownloadable
    }

    public void dataRetrieved(byte[] data) {
       token = new String(data);
       Log.debug("CloudMade token = "+token);
    }

    public String getRequestMethod() {
      return HttpConnection.POST;
    }
  }
}
