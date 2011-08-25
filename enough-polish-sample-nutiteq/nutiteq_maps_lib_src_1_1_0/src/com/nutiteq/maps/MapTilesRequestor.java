package com.nutiteq.maps;

import com.nutiteq.components.MapTile;

public interface MapTilesRequestor {
  MapTile getRequiredTile();

  MapTile[] getAllRequiredTiles();

  void tileRetrieved(final MapTile tile);

  boolean requiresMoreTiles();

  void updateTile(MapTile mapTile);
}
