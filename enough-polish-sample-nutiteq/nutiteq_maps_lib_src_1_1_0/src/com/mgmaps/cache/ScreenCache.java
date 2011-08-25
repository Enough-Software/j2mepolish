/*
 * Created on Oct 24, 2006
 */
package com.mgmaps.cache;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.components.MapPos;
import com.nutiteq.components.MapTile;
import com.nutiteq.log.Log;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.ui.ImageProcessor;

/**
 * Caches tiles uncompressed.
 */
public class ScreenCache {
  private MapTile[] tiles;
  private int size;
  private Image[] images;
  private boolean[] valid;
  //BattleTac code starts
  //Added by Krisztian Schaffer, 2010.02.26
  private ImageProcessor imageProcessor;
  private static ScreenCache instance;

  /**
   * Creates a new ScreenCache and returns it. The created instance is installed
   * as the current screen cache, the previous instance will be removed if any.
   * 
   * @param n
   *          maximum number of tiles stored in the created cache
   */
  public static ScreenCache createScreenCache(final int n) {
    ScreenCache newInstance = new ScreenCache(n);
    if (instance != null) {
      newInstance.imageProcessor = instance.imageProcessor;
    }
    return instance = newInstance;
  }

  /**
   * Returns the current ScreenCache. (null if no cache is created yet)
   */
  public static ScreenCache getInstance() {
    return instance;
  }

  //BattleTac code ends

  /**
   * Constructor for ScreenCache.
   * 
   * @param n
   *          maximum number of tiles stored
   */
  private ScreenCache(final int n) {//BattleTac code: Modified to private by Krisztian Schaffer, 2010.03.01
    resize(n);
  }

  /**
   * Resize the screen cache when switching full screen.
   * 
   * @param n
   *          new size (number of tiles)
   */
  public void resize(final int n) {
    final int minSize = Math.min(size, n);
    size = n;
    final boolean cond = minSize > 0;

    final boolean[] oldValid = valid;
    valid = new boolean[size];
    if (cond) {
      System.arraycopy(oldValid, 0, valid, 0, minSize);
    }

    final Image[] oldImages = images;
    images = new Image[size];
    if (cond) {
      System.arraycopy(oldImages, 0, images, 0, minSize);
    }

    final MapTile[] oldTiles = tiles;
    tiles = new MapTile[size];
    if (cond) {
      System.arraycopy(oldTiles, 0, tiles, 0, minSize);
    }
  }

  /**
   * Paint a tile
   * 
   * @param g
   *          graphics object
   * @param i
   *          tile number
   * @param centerCopy
   *          copy of the map center, used for synchronization
   */
  public void paint(final Graphics g, final int i, final MapPos centerCopy, final int screenCenterX, final int screenCenterY) {
    if (images[i] == null) {
      //TODO jaanus : why does this sometimes happen with streamed tiles?
      Log.debug(">>>>>>>>>>>>>>>>>>>>>>> SC: null image!");
      images[i] = tiles[i].getMap().getMissingTileImage();
    }
    final int left = tiles[i].getX() - centerCopy.getX() + screenCenterX;
    final int top = tiles[i].getY() - centerCopy.getY() + screenCenterY;
    g.drawImage(images[i], left, top, Graphics.TOP | Graphics.LEFT);
  }

  /**
   * Find the position for a map tile.
   * 
   * @param t
   *          tile to search
   * @return -1 if not found
   */
  public int find(final MapTile t) {
    for (int i = 0; i < size; i++) {
      if (valid[i] && tiles[i].equals(t)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Add a tile to this cache.
   * 
   * @param t
   *          the tile to add
   * @param update
   */
  public int add(final MapTile t, final MapPos mp, final GeoMap displayedMap, final int screenCenterX, final int screenCenterY, final boolean update) {
    // if the tile is not (no longer) visible, return -1
    if (!t.isVisible(mp, displayedMap, screenCenterX, screenCenterY)) {
      return -1;
    }

    // sweep at every add
    final int pos = sweepFind(t, mp, displayedMap, screenCenterX, screenCenterY);
    if (pos >= 0 && !update) { // found? return
      return pos;
    }

    // LOW rewrite if it slows down the app too much (it's O(n))
    // find a place to add
    for (int i = 0; i < size; i++) {
      if (!valid[i] || t.equals(tiles[i])) {
        valid[i] = true;
        tiles[i] = t;
        //BattleTac code starts
        //Modified by Krisztian Schaffer, 2010.02.26
        Image image = t.getImage();
        if (imageProcessor != null) {
          image = imageProcessor.processImage(image);
        }
        images[i] = image;
        //BattleTac code ends
        return i;
      }
    }

    return -1;
  }

  /**
   * Remove unneeded tiles (invalidate them). Also search for a map tile.
   * 
   * @param t
   *          map tile to search for
   * @return the position of the map tile, or -1 if not found
   */
  private int sweepFind(final MapTile t, final MapPos mp, final GeoMap displayedMap, final int screenCenterX, final int screenCenterY) {
    int found = -1;
    for (int i = 0; i < size; i++) {
      if (!valid[i]) {
        continue;
      }
      if (tiles[i].equals(t)) {
        found = i;
      } else if (!tiles[i].isVisible(mp, displayedMap, screenCenterX, screenCenterY)) {
        // if found, do not remove it
        valid[i] = false;
        tiles[i] = null;
        images[i] = null;
      }
    }
    return found;
  }

  //BattleTac code starts
  //Added by Krisztian Schaffer, 2010.02.26
  /**
   * Sets the tile image processor. The given ImageProcessor will process every
   * tile which is added after this call. This method also clears the cache.
   * 
   * @param processor
   *          the new ImageProcessor to use, can be null to delete the current
   *          processor.
   */
  public void setImageProcessor(final ImageProcessor processor) {
    imageProcessor = processor;
    for (int i = 0; i < size; i++) {
      valid[i] = false;
      tiles[i] = null;
      images[i] = null;
    }
  }
  //BattleTac code ends
}
