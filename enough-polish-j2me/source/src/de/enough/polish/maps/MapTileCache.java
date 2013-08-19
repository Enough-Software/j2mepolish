//#condition polish.hasFloatingPoint
package de.enough.polish.maps;

import de.enough.polish.util.ArrayList;

/**
 * This class implements a cache for map tiles. The implementation also allows defining a safe area. All tiles whose latitude and longitude IDs are
 * STRICTLY WITHIN this safe area will not be removed. Furthermore, when there is a need to remove a tile from the cache, the cache will attempt to
 * remove the tile that is furtherst away from the safe area's center coordinates.
 * NOTE: If a tile's lat/long IDs specify the tile's center geo coordinates (@see {@link MapTile}}, there are cases in which parts of the tile are within the safe area,
 * but the tile's center coordinates ARE NOT. In this case, the tile is considered to be outside the safe area.
 * NOTE: Altough possible, it is not advisable to use the same cache to store tiles generated from different providers, or at different zoom levels
 * @author Ovidiu Iliescu
 */
public class MapTileCache {
	
	/**
	 * The maximum cache size (in tiles)
	 */
	protected int maxSize = 0;
	
	/**
	 * The actual map tiles
	 */
	protected ArrayList cache = new ArrayList();
	
	/**
	 * The safe area's start latitude ID
	 */
	protected double safeAreaStartLatId = Integer.MAX_VALUE;
	
	/**
	 * The safe area's start longitude ID
	 */
	protected double safeAreaStartLonId = Integer.MAX_VALUE;
	
	/**
	 * The safe area's end latitude ID
	 */
	protected double safeAreaEndLatId = Integer.MAX_VALUE;
	
	/**
	 * The safe area's end longitude ID
	 */
	protected double safeAreaEndLonId = Integer.MAX_VALUE;
	
	/**
	 * Creates a new map tile cache
	 * @param size the maximum cache size (in tiles)
	 */
	public MapTileCache(int size) {
		this.maxSize = Math.max(1, size);
	}	
	
	/**
	 * Defines the cache's safe area
	 * @param startLatId start latitude ID
	 * @param startLonId start longitude ID
	 * @param endLatId end longitude ID
	 * @param endLonId end latitude ID
	 */
	protected void setSafeArea(double startLatId, double startLonId, double endLatId, double endLonId) {
		this.safeAreaStartLatId = startLatId;
		this.safeAreaStartLonId = startLonId;
		this.safeAreaEndLatId = endLatId;
		this.safeAreaEndLonId = endLonId;
	}
	
	/**
	 * Checks if a tile falls within the currently set safe area
	 * @param tile the tile the check
	 * @return true if the tile is within the safe area, false otherwise
	 */
	public boolean isWithinSafeArea(MapTile tile) {
		if ( tile.getLatId() >= this.safeAreaStartLatId &&
			 tile.getLatId() <= this.safeAreaEndLatId &&
			 tile.getLonId() >= this.safeAreaStartLonId &&
			 tile.getLonId() <= this.safeAreaEndLonId ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the safe area
	 */
	public void removeSafeArea() {
		this.safeAreaEndLatId = this.safeAreaEndLonId = this.safeAreaStartLatId = this.safeAreaStartLonId = Integer.MAX_VALUE;
	}
	
	/**
	 * Returns the number of tiles in the cache
	 * @return the number of tiles in the cache
	 */
	public int getCount() {
		return this.cache.size();
	}
	
	/**
	 * Returns the map tile with the specified cache index
	 * @param i the tile index
	 * @return the corresponding tile
	 */
	public MapTile getElementAt(int i) {
		return (MapTile) cache.get(i);
	}
	
	/**
	 * Removes all tiles from the cache
	 */
	public void clear() {
		this.cache.clear();
	}
	
	/**
	 * Attempts to push a tile into the cache.
	 * @param tile the tile to push
	 * @return true if the tile could be pushed, false otherwise
	 */
	public boolean pushMapTile(MapTile tile) {
		
		// Check if the map tile is already in the cache 
		for (int i=0;i<this.cache.size();i++) {
			MapTile t = (MapTile) this.cache.get(i);
			if ( MapUtils.distanceBetweenPoints(t.getLatId(), t.getLonId(), tile.getLatId(), tile.getLonId()) < 0.0001 ) {
				return true;
			}
		}
		
		// If there's no protected area, use simple cache management
		if ( this.safeAreaStartLatId == Integer.MAX_VALUE ) 
		{
			if ( this.cache.size() == maxSize ) {
				this.cache.remove(0);
			}
			this.cache.add(tile);
			return true;
		}
		
		// There is a protected area set
		// -----------------------------
		
		// If the cache isn't full, simply add to it
		if ( this.cache.size() < maxSize ) {
			this.cache.add(tile);
			return true;
		}
		
		// If the cache is full, attempt to find the tile furthest away from the safe area
		int bestCandidateIndex = -1;
		double safeAreaMiddleLat = (safeAreaEndLatId+safeAreaStartLatId)/2;
		double safeAreaMiddleLon = (safeAreaEndLonId+safeAreaStartLonId)/2;
		double bestDistanceSoFar = 0;
		for (int i=0;i<this.cache.size();i++) {
			MapTile t = (MapTile) this.cache.get(i);
			if ( ! isWithinSafeArea((MapTile) this.cache.get(i)) ) {
				double currentDistance = MapUtils.distanceBetweenPoints(safeAreaMiddleLat, safeAreaMiddleLon, t.getLatId(), t.getLonId());
				if ( currentDistance > bestDistanceSoFar ) {
					bestCandidateIndex = i;
					bestDistanceSoFar = currentDistance ;
				}
			} 
		}
		
		// If we could locate a suitable far away tile, replace it with the current tile
		if ( bestCandidateIndex >= 0 ) {
			this.cache.set(bestCandidateIndex, tile);				
			return true;
		}
		
		// All cached tiles are within the safe area, the new tile couldn't be added
		return false;		
	}

}
