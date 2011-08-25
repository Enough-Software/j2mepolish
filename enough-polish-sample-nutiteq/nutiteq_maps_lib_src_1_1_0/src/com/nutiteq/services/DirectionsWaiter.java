package com.nutiteq.services;

import com.nutiteq.components.Route;

/**
 * Interface for classes waiting on directions service
 */
public interface DirectionsWaiter {

  /**
   * Network error has occurred during directions service execution
   */
  void networkError();

  /**
   * Give resut to waiting object
   * 
   * @param route
   *          found route
   */
  void routeFound(final Route route);

  /**
   * Parsing error notification
   * 
   * @param message
   *          detalis
   */
  void routingParsingError(final String message);

  /**
   * Server side routing errors. Codes defined in
   * {@link com.nutiteq.services.OpenLSDirections}
   * 
   * @param errors
   *          possible error codes from server
   */
  void routingErrors(final int errors);
}
