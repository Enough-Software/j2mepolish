package de.enough.polish.maps;

import javax.microedition.lcdui.Image;

/**
 * This class defines a singular map tile. Typically, map tiles are obtained by splitting the world-map into fixed-size sections starting at
 * world offset (0,0). In effect, the world map is split into rectangular pieces like a puzzle. Each map tile has a latitude and longitude ID.
 * Depending on the provider which generated the tile, these ID's may be actual geo-coordinates (eg. 24.1234) or simply the tile's row/column
 * position in the world matrix.
 * @author Ovidiu Iliescu
 */
public class MapTile {
	
	/**
	 * The actual tile image
	 */
	protected Image tileImage;
	
	/**
	 * The tile's latitude ID. Depending on the map provider, this may be the tile's actual geo-latitude, or just its row index in the world map
	 */
	protected double latId;
	
	/**
	 * The tile's longitude ID. Depending on the map provider, this may be the tile's actual geo-longitude, or just its column index in the world map
	 */
	protected double lonId;
	
	/**
	 * The zoom level at which this tile was obtained and for which it is valid
	 */
	protected double zoomLevel;
	
	/**
	 * The tile's unique URL, used to retrive the tile's image
	 */
	protected String url;
	
	/**
	 * The tile's width in pixels
	 */
	protected int width;
	
	/**
	 * The tile's height in pixels
	 */
	protected int height;	
	
	/**
	 * Returns the tile's width in pixels
	 * @return tile's width in pixels
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets tile's width in pixels
	 * @param width tile's width in pixels
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Returns tile's height in pixels
	 * @return tile's height in pixels
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Sets tile's height in pixels
	 * @param height tile's height in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Returns the map tile's image URL
	 * @return the map tile's image URL
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the map tile's image URL
	 * @param url the map tile's image URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Returns the tile's actual image if it has been retrieved, or null if the image has not been retrieved yet
	 * @return the tile's actual image
	 */
	public Image getTileImage() {
		return tileImage;
	}
	
	/**
	 * Sets the tile's actual image
	 * @param tileImage the tile's actual image
	 */
	public void setTileImage(Image tileImage) {
		this.tileImage = tileImage;
	}
	
	/**
	 * Returns the tile's latitude ID. Depending on the map provider, this may be the tile's actual geo-latitude, or just its row index in the world map.
	 * @return the tile's latitude ID.
	 */
	public double getLatId() {
		return latId;
	}
	
	/**
	 * Sets the tile's latitude ID. Depending on the map provider, this may be the tile's actual geo-latitude, or just its row index in the world map.
	 * @param latId the tile's latitude ID
	 */
	public void setLatId(double latId) {
		this.latId = latId;
	}
	
	/**
	 * Returns the tile's longitude ID. Depending on the map provider, this may be the tile's actual geo-longitude, or just its column index in the world map.
	 * @return the tile's longitude ID
	 */
	public double getLonId() {
		return lonId;
	}
	
	/**
	 * Sets the tile's longitude ID. Depending on the map provider, this may be the tile's actual geo-longitude, or just its column index in the world map.
	 * @param lonId the tile's longitude ID
	 */
	public void setLonId(double lonId) {
		this.lonId = lonId;
	}
	
	/**
	 * Returns the tile's zoom level.
	 * @return the tile's zoom level.
	 */
	public double getZoomLevel() {
		return zoomLevel;
	}
	
	/**
	 * Sets the tile's zoom level.
	 * @param zoomLevel the tile's zoom level.
	 */
	public void setZoomLevel(double zoomLevel) {
		this.zoomLevel = zoomLevel;
	}
	
	/**
	 * Creates a new map tile
	 * @param tileImage the tile's actual image
	 * @param latId the tile's latitude ID
	 * @param lonId the tile's longitude ID
	 * @param zoomLevel the tile's zoom level
	 * @param url the tile's image URL
	 * @param width the tile's width in pixels
	 * @param height the tile's height in pixels
	 */
	public MapTile(Image tileImage, double latId, double lonId,
			double zoomLevel, String url, int width, int height) {
		this.tileImage = tileImage;
		this.latId = latId;
		this.lonId = lonId;
		this.zoomLevel = zoomLevel;
		this.url = url;
		this.width = width;
		this.height = height;
	}

}
