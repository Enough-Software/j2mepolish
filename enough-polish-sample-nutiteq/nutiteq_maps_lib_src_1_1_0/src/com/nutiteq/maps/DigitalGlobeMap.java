package com.nutiteq.maps;

import com.nutiteq.maps.projections.EPSG3785;
import com.nutiteq.ui.Copyright;
import com.nutiteq.ui.StringCopyright;

/**
 * Areal maps provided by DigitalGlobe.
 */
public class DigitalGlobeMap extends EPSG3785 implements UnstreamedMap {
  private static final String BASEURL = "http://www.globexplorer.com/tiles/img?p=mercator-spheroid&n=1&l=49&t=a&e=img%2FnoTile.jpg";
  private final String licenseKey;

  /**
   * Create new map for 256 tile size and zoom range 0-19.
   * 
   * @param licenseKey
   *          license key issued by DigitalGlobe
   */
  public DigitalGlobeMap(final String licenseKey) {
    this(new StringCopyright("DigitalGlobe"), licenseKey);
  }

  public DigitalGlobeMap(final Copyright copyright, final String licenseKey) {
    super(copyright, 256, 0, 19);
    this.licenseKey = licenseKey;
  }

  public String buildPath(final int mapX, final int mapY, final int zoom) {
    final int tmpX = mapX / 256;
    final int tmpY = mapY / 256;

    final StringBuffer url = new StringBuffer(BASEURL);
    url.append("&xi=");
    url.append(tmpX);
    url.append("&yi=");
    url.append(tmpY);
    url.append("&z=");
    url.append(zoom);
    url.append("&key=");
    url.append(licenseKey);
    return url.toString();
  }
}
