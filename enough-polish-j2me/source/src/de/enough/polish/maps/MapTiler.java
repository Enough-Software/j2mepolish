//#condition polish.hasFloatingPoint
package de.enough.polish.maps;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

/**
 * The MapTiler is an utility class designed to assist with map drawing. It covers drawing map sections based on geo-coordinates, cache management,
 * notifying listeners and more.
 * @author Ovidiu Iliescu
 */
public class MapTiler {

	/**
	 * The provider used to generate map tiles
	 */
	protected MapProvider provider;
	
	/**
	 * The map cache
	 */
	protected MapTileCache cache;
	
	/**
	 * The event listener
	 */
	protected MapTilerEventListener listener;
	
	/**
	 * The tile width
	 */
	protected int mapTileWidth = 100;
	
	/**
	 * The tile height
	 */
	protected int mapTileHeight = 100;
	
	/**
	 * The width of the map view (viewport), in pixels
	 */
	protected int mapViewWidth = 200;
	
	/**
	 * The height of the map view (viewport), in pixels
	 */
	protected int mapViewHeight = 200;
	
	/**
	 * The zoom level at which to paint the map
	 */
	protected int zoomLevel = -1;
	
	/**
	 * The desired view center-point latitude
	 */
	protected double lat = 0;
	
	/**
	 * The desired view center-point longitude
	 */
	protected double lon = 0;
	
	/**
	 * The thread on which the actual painting occurs
	 */
	protected Thread currentRunningThread = null;
	
	/**
	 * The map view's root latitude
	 */
	protected double rootLat = - Integer.MAX_VALUE;
	
	/**
	 * The map view's root longitude
	 */
	protected double rootLon = - Integer.MAX_VALUE;
	
	/**
	 * Creates a new map tiler
	 * @param provider the provider to use
	 * @param cache the map cache to use
	 */
	public MapTiler(MapProvider provider, MapTileCache cache) {
		this.provider = provider;
		this.cache = cache;
	}	

	/**
	 * Returns the currently registered {@link MapTilerEventListener}
	 * @return the currently registered {@link MapTilerEventListener}
	 */
	public MapTilerEventListener getListener() {
		return listener;
	}

	/**
	 * Sets the the currently registered {@link MapTilerEventListener} to notify when needed
	 * @param listener the currently registered {@link MapTilerEventListener} to notify
	 */
	public void setListener(MapTilerEventListener listener) {
		this.listener = listener;
	}

	/**
	 * Returns the {@link MapProvider} in use
	 * @return the {@link MapProvider} in use
	 */
	public MapProvider getProvider() {
		return provider;
	}

	/**
	 * Sets the {@link MapProvider} in use
	 * @param provider the {@link MapProvider} in use
	 */
	public void setProvider(MapProvider provider) {
		if ( this.cache != null ) {
			this.cache.clear();
		}
		this.provider = provider;
	}
	
	/**
	 * Returns the {@link MapTileCache} currently in use 
	 * @return the {@link MapTileCache} currently in use
	 */
	public MapTileCache getCache() {
		return cache;
	}

	/**
	 * Sets the {@link MapTileCache} to use
	 * @param cache the {@link MapTileCache} to use
	 */
	public void setCache(MapTileCache cache) {
		this.cache = cache;
	}

	/**
	 * Returns the map view width in pixels (viewport width)
	 * @return the map view width in pixels (viewport width)
	 */
	public int getMapViewWidth() {
		return mapViewWidth;
	}

	/**
	 * Sets the map view width in pixels (viewport width)
	 * @param mapViewWidth the map view width in pixels (viewport width)
	 */
	public void setMapViewWidth(int mapViewWidth) {
		this.mapViewWidth = mapViewWidth;
	}

	/**
	 * Returns the map view height in pixels (viewport height)
	 * @return the map view height in pixels (viewport height) 
	 */
	public int getMapViewHeight() {
		return mapViewHeight;
	}

	/**
	 * Sets the map view height in pixels (viewport height)
	 * @param mapViewHeight the map view height in pixels (viewport height)
	 */
	public void setMapViewHeight(int mapViewHeight) {
		this.mapViewHeight = mapViewHeight;
	}	
		
	/**
	 * Returns the current zoom level
	 * @return the current zoom level
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * Sets the zoom level. It will be clippend to fit witin the current provider's {minimum,maximum} zoom level interval
	 * @param zoomLevel the zoom level
	 */
	public void setZoomLevel(int zoomLevel) {
		if ( this.provider == null ) {
			return;
		}
		if ( this.zoomLevel == zoomLevel ) {
			return;
		}
		
		if ( zoomLevel > this.provider.getMaxZoomLevel() ) {
			zoomLevel = this.provider.getMaxZoomLevel();
		} else if ( zoomLevel < this.provider.getMinZoomLevel() ) {
			zoomLevel = this.provider.getMinZoomLevel();
		}
		
		if ( this.cache != null ) {
			this.cache.clear();
		}
		
		this.zoomLevel = zoomLevel;
	}
	
	/**
	 * Prepares a response for the given geo coordinates. If a {@link MapTilerEventListener} is set, it gets notified
	 * at various stages of the process.
	 * @param lat latitude
	 * @param lon longitude
	 */
	public void prepareResponse(final double lat, final double lon) {
		if ( this.provider == null ) {
			throw new IllegalArgumentException("Map Provider cannot be null");
		}
		Thread t = new Thread() {
			public void run() {
				
				// Get the cache object & listener
				MapTileCache cache = MapTiler.this.cache;
				MapTilerEventListener listener = MapTiler.this.listener;
				
				// Get a list of all the needed tiles
				MapRequest request = MapTiler.this.provider.getRequestForCoords(lat, lon, getMapViewWidth(), getMapViewHeight(), getZoomLevel());				
				MapResponse response = MapTiler.this.provider.prepareResponse(request);								
				
				// Set the cache safe area
				if ( cache != null ) {
					MapTile firstTile = (MapTile) response.getTiles().firstElement();
					MapTile lastTile = (MapTile) response.getTiles().lastElement();
					cache.setSafeArea(firstTile.getLatId(), firstTile.getLonId(), lastTile.getLatId(), lastTile.getLonId());
				}
				
				// Resolve each tile
				// -----------------
				Vector tiles = response.getTiles();
				
				// First, resolve existing tiles
				for (int i=0;i<tiles.size();i++) {
					
					// Check if we still need to run this thread
					if ( this != MapTiler.this.currentRunningThread) {
						return;
					}
					
					// Retrieve the tile
					MapTile tile = (MapTile) tiles.elementAt(i);
					
					// Check if it has an image already set
					if ( tile.getTileImage() != null ) {
						continue;
					}
					
					// If not, try to get the tile from the cache, with the image already set
					if ( cache != null ) {
						for (int j=0;j<cache.getCount();j++) {
							MapTile tempCacheTile = cache.getElementAt(j);	
							if ( tempCacheTile == null ) {
								continue;
							}
							if ( MapUtils.distanceBetweenPoints(tile.getLatId(), tile.getLonId(), tempCacheTile.getLatId(), tempCacheTile.getLonId()) < 0.0001 ) {
								tiles.setElementAt(tempCacheTile, i);
								break;
							}
						}									
					}
				}
				
				// Check if we still need to run this thread
				if ( this != MapTiler.this.currentRunningThread) {
					return;
				}
				
				// Notify that the existing tiles are ready
				if ( listener != null ) {
					listener.preloadedTilesReady(MapTiler.this, response);
				}
				
				// Next, go through and load+render all remaining tiles
				for (int i=0;i<tiles.size();i++) {
					
					// Check if we still need to run this thread
					if ( this != MapTiler.this.currentRunningThread) {
						return;
					}
					
					// Retrieve the tile
					MapTile tile = (MapTile) tiles.elementAt(i);										
					
					// If we still don't have an image, request the tile from the server
					if ( tile.getTileImage() == null ) {
						try {
							MapTiler.this.provider.resolveTile(tile);
							if ( cache != null ) {
								cache.pushMapTile(tile);
							}
							
							// Check if we still need to run this thread
							if ( this != MapTiler.this.currentRunningThread) {
								return;
							}
							
							if ( listener != null ) {
								listener.individualTileReady(MapTiler.this, response, i);
							}
							
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}			
				
				// Check if we still need to run this thread
				if ( this != MapTiler.this.currentRunningThread) {
					return;
				}
				
				// Lastly, repaint the whole map screen
				if ( listener != null ) {
					listener.allTilesReady(MapTiler.this, response);
				}

				// Clear the current running thread block
				MapTiler.this.currentRunningThread = null;
			}
		};
		this.currentRunningThread = t;
		t.start();
	}
	
	/**
	 * Draws an individual response tile
	 * @param g the graphics to draw on
	 * @param response the response to draw
	 * @param posX the target X position of the response (NOT the tile)
	 * @param posY the target Y position of the response (NOT the tile)
	 * @param tileNo the tile index to redraw ({@see {@link MapResponse#getTiles()}})
	 */
	public void drawIndividualResponseTile(Graphics g, MapResponse response, int posX, int posY, int tileNo ) {
		int startTop = posY + response.getYOffset();
		int startLeft = posX + response.getXOffset();
		
		int tileRow = tileNo % response.getTilesPerRow();
		int tileColumn = (int) Math.floor(( (double) tileNo) / response.getTilesPerRow() );
		
		MapTile tile = (MapTile) response.getTiles().elementAt(tileNo);
		if ( tile.getTileImage() != null ) {
			g.setClip(posX, posY, response.getWidth(), response.getHeight());		
			g.drawImage(tile.getTileImage(),startLeft + tile.getWidth() * tileRow, startTop + tile.getHeight() * tileColumn, Graphics.TOP | Graphics.LEFT );
		}
	}

	/**
	 * Draws an entire response
 	 * @param g the graphics to draw on
	 * @param response the response to draw
	 * @param posX the target X position 
	 * @param posY the target Y position 
	 */
	public void drawWholeResponse(Graphics g, MapResponse response, int posX, int posY) {
		int startTop = posY + response.getYOffset();
		int startLeft = posX + response.getXOffset();
		
		int itemsOnRow = 0;
		int currentItem = 0;
		int maxItem = response.getTiles().size();
		int top = startTop;
		int left = startLeft;
		

		g.setColor(0x00000000);
		g.setClip(posX, posY, response.getWidth(), response.getHeight());
		g.fillRect(0, 0, response.getWidth(), response.getHeight());
		
		while ( currentItem < maxItem ) {			
			MapTile tile = (MapTile) response.getTiles().elementAt(currentItem);			

			if ( itemsOnRow >= response.getTilesPerRow() ) {
				itemsOnRow=0;
				left = startLeft;
				top += tile.getHeight();
			}		

			g.setColor(0x0000FF00);
			g.drawRect(left, top, tile.getWidth(), tile.getHeight());
			
			if ( tile.getTileImage() != null ) {
				g.drawImage(tile.getTileImage(), left, top, Graphics.TOP | Graphics.LEFT );
			}
			left += tile.getWidth();
			
			
			itemsOnRow++;
			currentItem++;
		}
		
	}		
}
