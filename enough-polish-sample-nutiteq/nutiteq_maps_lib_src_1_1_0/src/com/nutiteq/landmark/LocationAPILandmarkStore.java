package com.nutiteq.landmark;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.location.QualifiedCoordinates;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.log.Log;

public class LocationAPILandmarkStore implements LandmarkStore {
  private final String storeName;
  private javax.microedition.location.LandmarkStore landmarkStore;

  public LocationAPILandmarkStore(final String storeName) {
    this.storeName = storeName;
  }

  public Enumeration getLandmarks() {
    try {
      return new LocationAPILandmarksEnumeration(getLocationApiLandmarksEnumeration());
    } catch (final IOException e) {
      return new EmptyLandmarksEnumeration();
    }
  }

  public void addlandmark(final Landmark landmark) {
    if (landmarkStore == null) {
      initialize();
    }

    final javax.microedition.location.Landmark added = toLocationApiLandmark(landmark);
    try {
      landmarkStore.addLandmark(added, null);
    } catch (final IOException e) {
      Log.error("Landmark add fail: " + e.getMessage());
      Log.printStackTrace(e);
    }
  }

  public void deleteLandmark(final Landmark deleted) {
    if (landmarkStore == null) {
      initialize();
    }

    try {
      final Enumeration enu = getLocationApiLandmarksEnumeration();
      while (enu.hasMoreElements()) {
        final javax.microedition.location.Landmark landmark = (javax.microedition.location.Landmark) enu
            .nextElement();
        final Landmark converted = toNutiteqLandmark(landmark);

        if (deleted.equals(converted)) {
          landmarkStore.deleteLandmark(landmark);
        }
      }
    } catch (final Exception e) {
      Log.error("Delete landmark: " + e.getMessage());
      Log.printStackTrace(e);
    }
  }

  private javax.microedition.location.Landmark toLocationApiLandmark(final Landmark landmark) {
    final WgsPoint wgsCoordinates = landmark.getCoordinates();
    //TODO jaanus : check this
    final QualifiedCoordinates coordinates = null;//new QualifiedCoordinates(wgsCoordinates.getLat(),
    //wgsCoordinates.getLon(), 0, 0, 0);

    return new javax.microedition.location.Landmark(landmark.getName(), landmark.getDescription(),
        coordinates, null);
  }

  public static Landmark toNutiteqLandmark(final javax.microedition.location.Landmark landmark) {
    final String name = landmark.getName();
    final String description = landmark.getDescription();
    final QualifiedCoordinates coordinates = landmark.getQualifiedCoordinates();

    return new Landmark(name, description, new WgsPoint(coordinates.getLongitude(), coordinates
        .getLatitude()));
  }

  private Enumeration getLocationApiLandmarksEnumeration() throws IOException {
    if (landmarkStore == null) {
      initialize();
    }

    return landmarkStore.getLandmarks();
  }

  private void initialize() {
    landmarkStore = javax.microedition.location.LandmarkStore.getInstance(storeName);
  }
}
