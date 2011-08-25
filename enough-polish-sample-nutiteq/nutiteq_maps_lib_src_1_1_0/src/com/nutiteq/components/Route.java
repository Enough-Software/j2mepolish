package com.nutiteq.components;

import javax.microedition.lcdui.Image;

/**
 * Data object containing route information from directions service.
 */
public class Route {
  private final RouteSummary summary;
  private final Line routeLine;
  private final RouteInstruction[] instructions;

  /**
   * Route from directions service
   * 
   * @param summary
   *          summary of the route
   * @param routeLine
   *          line containing route points
   * @param instructions
   *          route instructions
   */
  public Route(final RouteSummary summary, final Line routeLine,
      final RouteInstruction[] instructions) {
    this.summary = summary;
    this.routeLine = routeLine;
    this.instructions = instructions;
  }

  /**
   * Get route summary
   * 
   * @return summary of route
   */
  public RouteSummary getRouteSummary() {
    return summary;
  }

  /**
   * Get line for this route
   * 
   * @return route lines
   */
  public Line getRouteLine() {
    return routeLine;
  }

  /**
   * Get instructios for this route
   * 
   * @return instruction points
   */
  public RouteInstruction[] getInstructions() {
    return instructions;
  }

  /**
   * Get route markers, that can be shown on map. Images order is defined in
   * {@link com.nutiteq.services.DirectionsService}
   * 
   * @param routeImages
   *          images to be used in direction instructions
   * @return instructions that can be shown on map
   */
  public Place[] getRoutePointMarkers(final Image[] routeImages) {
    if (instructions == null || instructions.length == 0) {
      return new Place[0];
    }

    final Place[] routePointMarkers = new Place[instructions.length];
    for (int i = 0; i < instructions.length; i++) {
      final RouteInstruction current = instructions[i];
      routePointMarkers[i] = new Place(current.getInstructionNumber(), new PlaceLabel(current
          .getInstruction()), routeImages[current.getInstructionType()], current.getPoint());
    }

    return routePointMarkers;
  }
}
