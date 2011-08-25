package com.nutiteq.maps;

/**
 * OpenAerial map streamed via Nutiteq tile streaming server. Uses 64x64 pixel
 * tiles.
 * @deprecated The OpenAerialMap.org is down 

 */
public class StreamedOpenAerialMap extends NutiteqStreamedMap {
  private static final String OPEN_AERIAL_STREAMING_URL = "http://lbs.nutiteq.ee/mts_oam.php?";

  public StreamedOpenAerialMap(final int minZoom, final int maxZoom) {
    super(OPEN_AERIAL_STREAMING_URL, "OpenAerialMap", 64, minZoom, maxZoom);
  }
}
