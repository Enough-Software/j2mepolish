package com.nutiteq.landmark;

import java.util.Enumeration;

public class LocationAPILandmarksEnumeration implements Enumeration {
  private final Enumeration landmarks;

  public LocationAPILandmarksEnumeration(final Enumeration landmarks) {
    this.landmarks = landmarks;
  }

  public boolean hasMoreElements() {
    if (landmarks == null) {
      return false;
    }

    return landmarks.hasMoreElements();
  }

  public Object nextElement() {
    final javax.microedition.location.Landmark landmark = (javax.microedition.location.Landmark) landmarks
        .nextElement();
    return LocationAPILandmarkStore.toNutiteqLandmark(landmark);
  }
}
