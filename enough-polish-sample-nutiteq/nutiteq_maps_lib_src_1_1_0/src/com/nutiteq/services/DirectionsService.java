package com.nutiteq.services;

public interface DirectionsService extends Service {
  int IMAGE_ROUTE_START = 0;
  int IMAGE_ROUTE_RIGHT = 1;
  int IMAGE_ROUTE_LEFT = 2;
  int IMAGE_ROUTE_STRAIGHT = 3;
  int IMAGE_ROUTE_END = 4;

  /**
   * Error code for from and destination address are the same
   */
  int ERROR_FROM_AND_DESTINATION_ADDRESS_SAME = 4;
  /**
   * Error code for destination not found
   */
  int ERROR_DESTINATION_ADDRESS_NOT_FOUND = 2;
  /**
   * Error code for from address not found
   */
  int ERROR_FROM_ADDRESS_NOT_FOUND = 1;
  /**
   * Error code for route not found
   */
  int ERROR_ROUTE_NOT_FOUND = 8;
}
