package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

/**
 * General QuadKey-based API connector (baseUrl{quadkey}format style).
 */
public class QKMap extends EPSG3785 implements UnstreamedMap {
    private final String baseUrl;
    private final String format;

    /**
     * Quadtree based map general server API connector
     * 
     * @param copyright
     *            Copyright graphics drawn to the map
     * @param baseUrl
     *            URL beginning for the map tile request. Final URL will be
     *            generated as <b>baseUrl</b><I>QuadKey-number</i><b>format</b>
     * @param tileSize
     *            Size of tile image in pixels, usually 256
     * @param format
     *            Tile image format, usually "png" or "jpg".
     * @param minZoom
     *            Minimum (world) zoom level for service. Could be 0
     * @param maxZoom
     *            Maximum zoom level. E.g. for OSM set it to 19
     */
    public QKMap(final Copyright copyright, final String baseUrl,
            final int tileSize, final int minZoom, final int maxZoom,
            final String format) {
        super(copyright, tileSize, minZoom, maxZoom);
        this.baseUrl = baseUrl;
        this.format = format;
    }

    /**
     * Quadtree based map general server API connector
     * 
     * @param copyright
     *            Copyright as String
     * @param baseUrl
     *            URL beginning for the map tile request. Final URL will be
     *            generated as <b>baseUrl</b><I>QuadKey-number</i><b>format</b>
     * @param tileSize
     *            Size of tile image in pixels, usually 256
     * @param format
     *            Tile image format, usually "png" or "jpg".
     * @param minZoom
     *            Minimum (world) zoom level for service. Could be 0
     * @param maxZoom
     *            Maximum zoom level. E.g. for OSM set it to 19
     */
    public QKMap(final String copyright, final String baseUrl,
            final int tileSize, final int minZoom, final int maxZoom,
            final String format) {
        this(new StringCopyright(copyright), baseUrl, tileSize, minZoom,
                maxZoom, format);
    }

    public final String buildPath(final int mapX, final int mapY, final int zoom) {
        final int tmpX = mapX / getTileSize();
        final int tmpY = mapY / getTileSize();
        final StringBuffer buf = new StringBuffer(baseUrl);
        for (int i = zoom - 1; i >= 0; i--) {
            buf.append((((tmpY >> i) & 1) << 1) + ((tmpX >> i) & 1));
        }
        buf.append(format);
        return buf.toString();
    }
}
