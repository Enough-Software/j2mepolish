package com.nutiteq.maps;

public class StreamedCloudMadeMap extends NutiteqStreamedMap {
  private static final String CLOUD_MADE_STREAMING_URL = "http://aws.nutiteq.com/cloudmade_stream.php?";

  public StreamedCloudMadeMap() {
    super(CLOUD_MADE_STREAMING_URL, "CloudMade", 64, 0, 19);
  }
}
