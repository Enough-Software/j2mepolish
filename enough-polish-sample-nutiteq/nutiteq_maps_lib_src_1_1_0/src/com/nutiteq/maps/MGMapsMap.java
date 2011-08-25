package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

/**
 * General MGMaps URL style Server API connector (baseUrl?x=x&y=y&zoom=zoom style, based on "old google tile" URL)
 */
public class MGMapsMap extends EPSG3785 implements UnstreamedMap {

    private String baseUrl;
    private String format;

    /**
     * Final URL will be generated as
     * <b>baseUrl</b>&x=<i>x</i>&y=<i>y</i>&zoom=<i>zoom</i></b>
     * 
     * @param copyright
     *            Copyright graphics drawn to the map
     * @param baseUrl
     *            URL beginning for the map tile request.
     * @param tileSize
     *            Size of tile image in pixels, usually 256
     * @param format
     *            Tile image format, usually "png" or "jpg".
     * @param minZoom
     *            Minimum (world) zoom level for service. Could be 0
     * @param maxZoom
     *            Maximum zoom level. E.g. 21
     */
    public MGMapsMap(final String baseUrl, final int tileSize, final int minZoom,
            final int maxZoom, final String format, final Copyright copyright) {

        super(copyright, tileSize, minZoom, maxZoom);
        this.format = format;
        this.baseUrl = baseUrl;
    }

    /**
     * MGMaps URL style map server API. 
     * Final URL will be generated as
     * <b>baseUrl</b>&x=<i>x</i>&y=<i>y</i>&zoom=<i>zoom</i></b>
     * 
     * @param copyright
     *            Copyright as string
     * @param baseUrl
     *            URL beginning for the map tile request.
     * @param tileSize
     *            Size of tile image in pixels, usually 256
     * @param format
     *            Tile image format, usually "png" or "jpg".
     * @param minZoom
     *            Minimum (world) zoom level for service. Could be 0
     * @param maxZoom
     *            Maximum zoom level.
     */

    public MGMapsMap(final String baseUrl, final int tileSize, final int minZoom,
            final int maxZoom, final String format, final String copyright) {

        this(baseUrl, tileSize, minZoom, maxZoom, format, new StringCopyright(
                copyright));

    }

    public String buildPath(final int mapX, final int mapY, final int zoom) {

        final StringBuffer url = new StringBuffer(baseUrl);
        url.append("&x=");
        url.append(mapX / getTileSize());
        url.append("&y=");
        url.append(mapY / getTileSize());
        url.append("&zoom=");
        url.append(zoom);

        return url.toString();
    }

}
