package com.nutiteq.landmark;

import java.util.Enumeration;

public interface LandmarkStore {
  Enumeration getLandmarks();

  void addlandmark(Landmark landmark);

  void deleteLandmark(Landmark landmark);
}
