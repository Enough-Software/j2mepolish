package com.nutiteq.location;

import com.nutiteq.components.WgsPoint;

/**
 * Interface for objects needing location data
 */
public interface LocationListener {
  void setLocation(final WgsPoint location);
}
