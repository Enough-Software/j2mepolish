package com.nutiteq.config;

public class StoredMapConfig {
  private final int tilesPerFile;
  private final int tpfx;
  private final int tpfy;
  private final int hashSize;

  public StoredMapConfig(final int tilesPerFile, final int tpfx, final int tpfy, final int hashSize) {
    this.tilesPerFile = tilesPerFile;
    this.tpfx = tpfx;
    this.tpfy = tpfy;
    this.hashSize = hashSize;
  }

  public int getTilesPerFile() {
    return tilesPerFile;
  }

  public int getTpfx() {
    return tpfx;
  }

  public int getTpfy() {
    return tpfy;
  }
}
