package com.nutiteq.location.providers;

import com.nutiteq.location.cellid.CellIdService;
import com.nutiteq.location.cellid.MotorolaCellIdDataReader;

/**
 * Cell id positioning provider for Motorola handsets. See
 * {@link MotorolaCellIdDataReader} for more information.
 */
public class MotorolaCellIdLocationProvider extends CellIdLocationProvider {
  /**
   * Create location provider with default service
   * {@link com.nutiteq.location.cellid.OpenCellIdService}
   */
  public MotorolaCellIdLocationProvider() {
    super(new MotorolaCellIdDataReader());
  }

  /**
   * Create location provider with custom cellId service.
   * 
   * @param service
   *          cellId service used for location finding
   */
  public MotorolaCellIdLocationProvider(final CellIdService service) {
    super(new MotorolaCellIdDataReader(), service);
  }
}
