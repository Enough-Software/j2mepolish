package de.enough.polish.maps;

import java.util.Vector;

/**
 * This class defines a map response received from a {@link MapProvider} as a result of processing a {@link MapRequest}. A map response contains a list
 * of {@link MapTile}s required to pain the response, as well as all the information needed to properly paint it.
 * @author Ovidiu Iliescu
 */
public class MapResponse {
	
	/**
	 * The {@link MapRequest} associated with this map response
	 */
	protected MapRequest associatedMapRequest;
	
	/**
	 * The x offset needed to properly paint the map response.
	 */
	protected int xOffset;
	
	/**
	 * The y offset needed to properly paint the map response.
	 */
	protected int yOffset;
	
	/**
	 * As the map response is a rectangular map section made out of a tile matrix, this represents the number of tiles per matrix row.
	 */
	protected int tilesPerRow;
	
	/**
	 * The map response width (in pixels)
	 */
	protected int width;
	
	/**
	 * The map response height (in pixels)
	 */
	protected int height;
	
	/**
	 * The actual map tiles needed to paint the response.
	 */
	protected Vector tiles = new Vector() ;
		
	
	/**
	 * Returns the map response width (in pixels)
	 * @return the map response width (in pixels)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the map response width (in pixels)
	 * @param width the map response width (in pixels)
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the map response height (in pixels)
	 * @return the map response height (in pixels)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the map response height (in pixels)
	 * @param height the map response height (in pixels)
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns the associated map request, based on which this map response has been created
	 * @return the associated map request
	 */
	public MapRequest getAssociatedMapRequest() {
		return associatedMapRequest;
	}

	/**
	 * Sets the associated map request
	 * @param associatedMapRequest the associated map request
	 */
	public void setAssociatedMapRequest(MapRequest associatedMapRequest) {
		this.associatedMapRequest = associatedMapRequest;
	}

	/**
	 * Returns the number of map tiles per row (the map response width in tiles)
	 * @return the number of map tiles per row (the map response width in tiles)
	 */
	public int getTilesPerRow() {
		return tilesPerRow;
	}

	/**
	 * Sets the number of map tiles per row (the map response width in tiles)
	 * @param tilesPerRow the number of map tiles per row (the map response width in tiles)
	 */
	public void setTilesPerRow(int tilesPerRow) {
		this.tilesPerRow = tilesPerRow;
	}

	/**
	 * The map response is made up of a matrix of map tiles, which are typically obtained by splitting the world-map into fixed-size pieces (tiles) starting
	 * at world offset (0,0). However, depending on the map coordinates given and on the map provider used, there is usually a need to paint
	 * the top-left tile of the response matrix with an offset, for example if the map response's top-left PIXEL is actually located in the center of the map response's top-left TILE.
	 * This method returns the horizontal top-left tile offset needed to properly paint the map response starting with the right pixels.
	 * @return the horizontal top-left tile offset needed to properly paint the map response starting with the right pixels.
	 */
	public int getXOffset() {
		return xOffset;
	}
	
	/**
	 * Sets the horizontal top-left tile offset needed to properly paint the map response starting with the right pixels.
	 * @param xOffset the horizontal top-left tile offset needed to properly paint the map response starting with the right pixels.
	 */
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	
	/**
	 * The map response is made up of a matrix of map tiles, which are typically obtained by splitting the world-map into fixed-size pieces (tiles) starting
	 * at world offset (0,0). However, depending on the map coordinates given and on the map provider used, there is usually a need to paint
	 * the top-left tile of the response matrix with an offset, for example if the map response's top-left PIXEL is actually located in the center of the map response's top-left TILE.
	 * This method returns the vertical top-left tile offset needed to properly paint the map response starting with the right pixels.
	 * @return the vertical top-left tile offset needed to properly paint the map response starting with the right pixels.
	 */
	public int getYOffset() {
		return yOffset;
	}
	
	/**
	 * Sets the vertical top-left tile offset needed to properly paint the map response starting with the right pixels.
	 * @param yOffset the vertical top-left tile offset needed to properly paint the map response starting with the right pixels.
	 */
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	/**
	 * Returns the set of map tiles needed to paint the map response
	 * @return the set of map tiles needed to paint the map response
	 */
	public Vector getTiles() {
		return tiles;
	}
	
	/**
	 * Sets the set of map tiles needed to paint the map response
	 * @param tiles the set of map tiles needed to paint the map response
	 */
	public void setTiles(Vector tiles) {
		this.tiles = tiles;
	}
}
