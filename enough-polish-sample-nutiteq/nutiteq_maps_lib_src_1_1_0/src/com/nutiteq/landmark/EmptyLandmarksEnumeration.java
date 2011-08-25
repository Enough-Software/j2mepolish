package com.nutiteq.landmark;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyLandmarksEnumeration implements Enumeration {
  public boolean hasMoreElements() {
    return false;
  }

  public Object nextElement() {
    throw new NoSuchElementException();
  }
}
