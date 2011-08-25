package com.nutiteq.location.providers;

import com.nutiteq.location.cellid.CellIdService;
import com.nutiteq.location.cellid.SonyEricssonCellIdDataReader;

/**
 * SonyEricsson implementation for location provider based on mobile cell ID.
 * See {@link CellIdLocationProvider} for more info.
 */
public class SonyEricssonCellIdLocationProvider extends CellIdLocationProvider {
  /**
   * Create location provider with default service
   * {@link com.nutiteq.location.cellid.OpenCellIdService}
   */
  public SonyEricssonCellIdLocationProvider() {
    super(new SonyEricssonCellIdDataReader());
  }

  /**
   * Create location provider with custom cellId service
   * 
   * @param service
   *          service used for location finding
   */
  public SonyEricssonCellIdLocationProvider(final CellIdService service) {
    super(new SonyEricssonCellIdDataReader(), service);
  }
}
