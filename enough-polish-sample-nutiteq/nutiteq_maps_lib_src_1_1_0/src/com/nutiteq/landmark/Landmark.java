package com.nutiteq.landmark;

import com.nutiteq.components.Sortable;
import com.nutiteq.components.WgsPoint;

public class Landmark implements Sortable {

  private final String name;
  private final String description;
  private final WgsPoint coordinates;

  public Landmark(final String name, final String description, final WgsPoint coordinates) {
    this.name = name;
    this.description = description;
    this.coordinates = coordinates;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public WgsPoint getCoordinates() {
    return coordinates;
  }

  public int compareTo(final Object other) {
    if (other == this) {
      return 0;
    }
    final Landmark l = (Landmark) other;
    return name.compareTo(l.name);
  }

  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Landmark)) {
      return false;
    }

    final Landmark other = (Landmark) obj;

    return name.equals(other.name) && description.equals(other.description)
        && coordinatesEqual(coordinates, other.coordinates);
  }

  private boolean coordinatesEqual(final WgsPoint coordinatesOne, final WgsPoint coordinatesTwo) {
    //TODO jaanus : check this
    final int lonOne = (int) (coordinatesOne.getLon() * 1000);
    final int latOne = (int) (coordinatesOne.getLat() * 1000);
    final int lonTwo = (int) (coordinatesTwo.getLon() * 1000);
    final int latTwo = (int) (coordinatesTwo.getLat() * 1000);
    return lonOne == lonTwo && latOne == latTwo;
  }

  public int hashCode() {
    throw new RuntimeException("hashCode not implemented");
  }
}
