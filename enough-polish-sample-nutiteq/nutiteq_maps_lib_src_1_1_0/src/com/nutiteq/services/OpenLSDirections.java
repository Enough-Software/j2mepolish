package com.nutiteq.services;

import java.io.IOException;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.Route;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.utils.Utils;

/**
 * OpenLS directions service using Nutiteq's backend.
 */
public class OpenLSDirections implements DirectionsService, ResourceRequestor, ResourceDataWaiter {
  private final String routeUrl;
  private final DirectionsWaiter directionsWaiter;

  /**
   * Default baseurl for the service.
   */
  public static final String NUTITEQ_DEFAULT_SERVICE_URL = "http://lbs.nutiteq.ee/osm/geocode_kml.php";

  /**
   * 
   * @param directionsWaiter
   *          object waiting for directions result
   * @param baseurl
   *          baseurl for the service
   * @param language
   *          used language
   * @param routeStart
   *          star point for route (in WGS84)
   * @param routeEnd
   *          end point for route (in WGS84)
   */
  public OpenLSDirections(final DirectionsWaiter directionsWaiter, final String baseurl,
      final String language, final WgsPoint routeStart, final WgsPoint routeEnd) {
    this.directionsWaiter = directionsWaiter;
    final StringBuffer url = new StringBuffer(Utils.prepareForParameters(baseurl));
    url.append("out=openls&t=d&saddr=").append(Tools.urlEncode(routeStart.getLat() + "N, "));
    url.append(Tools.urlEncode(routeStart.getLon() + "E"));
    url.append("&daddr=").append(Tools.urlEncode(routeEnd.getLat() + "N, "));
    url.append(Tools.urlEncode(routeEnd.getLon() + "E")).append("&lang=");
    url.append(language).append("&gzip=yes");
    routeUrl = url.toString();
  }

  public String resourcePath() {
    return routeUrl;
  }

  //TODO jaanus : read as gzip stream
  public void dataRetrieved(final byte[] data) {
    final OpenLSReader reader = new OpenLSReader(data, directionsWaiter);
    try {
      final Route route = reader.read();
      directionsWaiter.routeFound(route);
    } catch (final IOException e) {
      directionsWaiter.routingParsingError(e.getMessage());
    }
  }

  public void notifyError() {
    directionsWaiter.networkError();
  }

  public void execute() {
    MappingCore.getInstance().getTasksRunner().enqueueDownload(this,
        com.nutiteq.cache.Cache.CACHE_LEVEL_NONE);
  }

  public void cancel() {
    //TODO jaanus
  }

  public int getCachingLevel() {
    return Cache.CACHE_LEVEL_NONE;
  }
}
