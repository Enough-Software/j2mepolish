package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

public class NavteqMapTP extends EPSG3785 implements UnstreamedMap {
    private final String mid;
    private String baseUrl = "http://maptp12.map24.com/map24/webservices1.5?cgi=Map24RenderEngine&mid=";

    public NavteqMapTP(final String baseUrl, final String mid) {
        this(new StringCopyright("Navteq MapTP"), mid);
        this.baseUrl = baseUrl;
    }

    public NavteqMapTP(final Copyright copyright, final String baseUrl,
            final String mid) {
        this(copyright, mid);
        this.baseUrl = baseUrl;
    }

    public NavteqMapTP(final String mid) {
        this(new StringCopyright("Navteq MapTP"), mid);
    }

    public NavteqMapTP(final Copyright copyright, final String mid) {
        super(copyright, 256, 1, 17);
        this.mid = mid;
    }

    public String buildPath(final int mapX, final int mapY, final int zoom) {
        final int tmpX = mapX / 256;
        final int tmpY = mapY / 256;
        final StringBuffer buf = new StringBuffer();
        buf.append(baseUrl);
        buf.append(mid);
        buf.append("&quadkey=");
        for (int i = zoom - 1; i >= 0; i--) {
            buf.append((((tmpY >> i) & 1) << 1) + ((tmpX >> i) & 1));
        }
        return buf.toString();
    }
}
