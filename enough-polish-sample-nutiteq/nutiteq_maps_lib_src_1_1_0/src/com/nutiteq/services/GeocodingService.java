package com.nutiteq.services;

import com.mgmaps.utils.Tools;
import com.nutiteq.cache.Cache;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.core.MappingCore;
import com.nutiteq.io.ResourceDataWaiter;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.kml.KmlElementsWaiter;
import com.nutiteq.kml.KmlReader;
import com.nutiteq.kml.KmlService;
import com.nutiteq.kml.KmlStylesCache;
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
public class GeocodingService implements Service, KmlElementsWaiter,
        KmlService, ResourceRequestor, ResourceDataWaiter {
    private static final String ERROR_MESSAGE_START = "Error:";
    public static final int NETWORK_ERROR = 0;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;

    private static final int ERROR_RESPONSE_LENGTH = 9;
    /**
     * Search type for geocoding (place search)
     */
    public static final String SEARCH_TYPE_GEOCODING = "q";
    /**
     * Search type for POI search
     */
    public static final String SEARCH_TYPE_POI = "l";

    private final String searchUrl;
    private final int numberOfElements;
    private final KmlStylesCache stylesCache;

    /**
     * URL for default backend.
     */
    public static final String DEFAULT_URL = "http://lbs.nutiteq.ee/cloudmade/geocode_kml.php";
    private final GeocodingResultWaiter resultWaiter;
    private final boolean gzipResponse;
    private boolean stopped;

    /**
     * 
     * @param resultWaiter
     *            object waiting for search results
     * @param baseurl
     *            baseurl for service
     * @param lang
     *            search language
     * @param searchNear
     *            location for searching (in WGS84)
     * @param query
     *            query
     * @param searchType
     *            search type (place search or POI search)
     * @param categories
     *            possible categories for search
     * @param numberOfElements
     *            number of elements to ask from server
     * @param gzipResponse
     *            should response be compressed in server. Adds gzip=(yes|no) to
     *            end of url
     */
    public GeocodingService(final GeocodingResultWaiter resultWaiter,
            final String baseurl, final String lang, final WgsPoint searchNear,
            final String query, final String searchType,
            final int[] categories, final int numberOfElements,
            final boolean gzipResponse) {
        this.resultWaiter = resultWaiter;
        this.numberOfElements = numberOfElements;
        this.gzipResponse = gzipResponse;
        final StringBuffer url = new StringBuffer(Utils
                .prepareForParameters(baseurl));

        url.append("out=kml");

        if (searchNear != null) {
            url.append("&near=");
            url.append(Tools.urlEncode(searchNear.getLat() + "N, "));
            url.append(Tools.urlEncode(searchNear.getLon() + "E"));
        }
        url.append("&t=").append(Tools.urlEncode(searchType));
        url.append("&q=").append(Tools.urlEncode(query));
        url.append("&lang=").append(Tools.urlEncode(lang));

        if (categories != null) {
            url.append("&cat=");
            for (int i = 0; i < categories.length; i++) {
                url.append(categories[i]);
                if (i < categories.length - 1) {
                    url.append(",");
                }
            }
        }

        url.append("&max=").append(numberOfElements);
        url.append("&gzip=").append(gzipResponse ? "yes" : "no");

        searchUrl = url.toString();

        stylesCache = new KmlStylesCache();
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

        if (data.length == 9) {
            final String response = new String(data);
            if (response.indexOf(ERROR_MESSAGE_START) >= 0) {
                resultWaiter.errors(Integer.parseInt(response
                        .substring(ERROR_MESSAGE_START.length())));
                return;
            }
        }

        final KmlReader reader = new KmlReader(this, this, searchUrl,
                stylesCache, null, gzipResponse);
        reader.dataRetrieved(data);
    }

    public void addKmlPlaces(final KmlService service, final KmlPlace[] read) {
        for (int i = 0; i < read.length; i++) {
            final String url = stylesCache.resolveImageUrl(read[i]
                    .getStyleUrl());
            if (url != null) {
                read[i].setStyleUrl(url);
            }
        }
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
