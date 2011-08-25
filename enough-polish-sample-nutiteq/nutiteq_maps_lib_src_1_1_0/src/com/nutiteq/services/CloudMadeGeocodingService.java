package com.nutiteq.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.kml.JsonKmlReader;
import com.nutiteq.kml.KmlElementsWaiter;
import com.nutiteq.kml.KmlService;
import com.nutiteq.log.Log;
import com.nutiteq.task.TasksRunner;
import com.nutiteq.utils.Utils;

/**
 * <p>
 * Default implementation for geocoding services. Uses default Nutiteq backend
 * for searching.
 * </p>
 * 
 * <p>
 * Additional search parameters can be added to search URL by appending these to
 * baseurl used in service creation.
 * </p>
 */
public class CloudMadeGeocodingService implements Service, KmlElementsWaiter,
        KmlService, ResourceRequestor, ResourceDataWaiter {
    public static final int NETWORK_ERROR = 0;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;

    private final String searchUrl;
    private final int numberOfElements;

    private static final String SERVICE_URL_BASE = "http://geocoding.cloudmade.com/";
    private static final String SERVICE_URL_PART2 = "/geocoding/v2/find.js";

    private final GeocodingResultWaiter resultWaiter;
    private boolean stopped;

    /**
     * CloudMade HTTP Geocoding and Geosearch API service. See <a href="http://developers.cloudmade.com/wiki/geocoding-http-api/Documentation">CloudMade guide</a>
     * 
     * @param resultWaiter
     *            listener waiting for search results
     * @param cloudMadeKey
     *            CloudMade service key, get from cloudmade.com
     * @param endUserId
     *            Unique end-user ID, e.g. end-user login. Used for CloudMade token
     * @param around
     *            Center point of the search area. Used together with distance. around must be an EPSG:4326 coordinate ("latitude,longitude"). Cannot be used together with bbox. If used together with a non-empty search_query, only one may specify an address
     * @param aroundAddress
     *            Center point of the search area. Used together with distance. Either around or aroundAddress can be defined. Cannot be used together with bbox. If used together with a non-empty search_query, only one may specify an address
     * @param query
     *            placename to be searched, optional for POI search
     * @param objectType
     *            Limits search results to a specific object type. Full list of object types can be found from <a href="http://developers.cloudmade.com/wiki/geocoding-http-api/Object_Types">CloudMade web</a>
     * @param numberOfElements
     *            max number of results to return 
     * @param skip
     *            Number of results to skip from beginning
     * @param bBox
     *            Bounding box of the search area
     * @param bBoxOnly
     *            Used only if bbox is specified. If set to false, the geocoder will return results from the whole planet, but still ranking results from within the specified bbox higher, otherwise only results from within the specified bbox will be returned. 
     * @param distance
     *            Radius of the search area, in meters. Special value closest limits search results to one, closest to the center point of the search area
     *            
     */
    public CloudMadeGeocodingService(
            final GeocodingResultWaiter resultWaiter,
            final String cloudMadeKey, 
            final String endUserId,
            final WgsPoint around,
            final String aroundAddress,
            final String query, 
            final String objectType, 
            final int numberOfElements,
            final int skip,
            final WgsBoundingBox bBox,
            final boolean bBoxOnly,
            final String distance
            ) {
        this(resultWaiter, CloudMadeToken.getCloudMadeToken(cloudMadeKey,endUserId), around, aroundAddress,
                query,objectType,numberOfElements,skip,bBox,bBoxOnly,distance,cloudMadeKey);

    }    
    /**
     * CloudMade HTTP Geocoding and Geosearch API service. See <a href="http://developers.cloudmade.com/wiki/geocoding-http-api/Documentation">CloudMade guide</a>
     * 
     * @param resultWaiter
     *            listener waiting for search results
     * @param cloudMadeToken
     *            CloudMade service key, get from cloudmade.com API
     * @param around
     *            Center point of the search area. Used together with distance. around must be an EPSG:4326 coordinate ("latitude,longitude"). Cannot be used together with bbox. If used together with a non-empty search_query, only one may specify an address
     * @param aroundAddress
     *            Center point of the search area. Used together with distance. Either around or aroundAddress can be defined. Cannot be used together with bbox. If used together with a non-empty search_query, only one may specify an address
     * @param query
     *            placename to be searched, optional for POI search
     * @param objectType
     *            Limits search results to a specific object type. Full list of object types can be found from <a href="http://developers.cloudmade.com/wiki/geocoding-http-api/Object_Types">CloudMade web</a>
     * @param numberOfElements
     *            max number of results to return 
     * @param skip
     *            Number of results to skip from beginning
     * @param bBox
     *            Bounding box of the search area
     * @param bBoxOnly
     *            Used only if bbox is specified. If set to false, the geocoder will return results from the whole planet, but still ranking results from within the specified bbox higher, otherwise only results from within the specified bbox will be returned. 
     * @param distance
     *            Radius of the search area, in meters. Special value closest limits search results to one, closest to the center point of the search area
     * @param cloudMadeKey
     *            CloudMade service key, get from cloudmade.com
     */
    public CloudMadeGeocodingService(
            final GeocodingResultWaiter resultWaiter,
            final String cloudMadeToken, 
            final WgsPoint around,
            final String aroundAddress,
            final String query, 
            final String objectType, 
            final int numberOfElements,
            final int skip,
            final WgsBoundingBox bBox,
            final boolean bBoxOnly,
            final String distance,
            final String cloudMadeKey
            ) {
        this.resultWaiter = resultWaiter;
        this.numberOfElements = numberOfElements;
        final StringBuffer url = new StringBuffer(SERVICE_URL_BASE);
        url.append(cloudMadeKey);
        url.append(Utils.prepareForParameters(SERVICE_URL_PART2));
        if(query != null){
         url.append("&query=").append(Tools.urlEncode(query));
        }
        if(around != null){
            url.append("&around=").append(around.getLat()+","+around.getLon());
           }else if(aroundAddress != null){
               url.append("&around=").append(aroundAddress);
           }

        if(objectType != null){
            url.append("&object_type=").append(Tools.urlEncode(objectType));
           }
        if(numberOfElements > 0){
            url.append("&results=").append(numberOfElements);
           }
        if(skip > 0){
            url.append("&skip=").append(skip);
           }
        if(bBox != null){
            url.append("&bbox=")
                .append(bBox.getWgsMin().getLat())
                .append(",")
                .append(bBox.getWgsMin().getLon())
                .append(",")
                .append(bBox.getWgsMax().getLat())
                .append(",")
                .append(bBox.getWgsMax().getLon())
               ;
           }
        if(bBoxOnly){
            url.append("&bbox_only=true");
           }

        if(distance != null){
            url.append("&distance=").append(Tools.urlEncode(distance));
           }

        url.append("&return_location=true");
        url.append("&return_geometry=true");
        url.append("&token="+cloudMadeToken);

        searchUrl = url.toString();
        Log.debug("CloudMade url="+searchUrl);
        
    }

    public void execute() {
        enqueueDownload(MappingCore.getInstance().getTasksRunner());
    }

    protected void enqueueDownload(final TasksRunner d) {
        d.enqueueDownload(this, Cache.CACHE_LEVEL_NONE);
    }

    public String resourcePath() {
        return searchUrl;
    }

    public void notifyError() {
        resultWaiter.errors(NETWORK_ERROR);
    }

    public void dataRetrieved(final byte[] data) {
        if (stopped) {
            return;
        }

        if (data.length < 4) {
                resultWaiter.errors(CloudMadeGeocodingService.NOT_FOUND);
                return;
            }
        
        final JsonKmlReader reader = new JsonKmlReader(this, this, searchUrl);
        
        reader.dataRetrieved(data);
    }

    public void addKmlPlaces(final KmlService service, final KmlPlace[] read) {

        resultWaiter.searchResults(read);
    }

    public String getServiceUrl(final WgsBoundingBox boundingBox, final int zoom) {
        return searchUrl;
    }

    public int maxResults() {
        return numberOfElements;
    }

    public boolean needsUpdate(final WgsBoundingBox boundingBox, final int zoom) {
        return false;
    }

    public void cancel() {
        stopped = true;
    }

    public int getCachingLevel() {
        return Cache.CACHE_LEVEL_NONE;
    }

    public String getDefaultIcon() {
        // TODO Auto-generated method stub
        return null;
    }
    

}
