package de.enough.polish.maps;

/**
 * This class defines a map request, used to define what part of the world map is needed, and at what zoom level 
 * @author Ovidiu Iliescu
 */
public class MapRequest {
	
	/**
	 * The latitude of the map request's center point
	 */
	protected double lat;

	/**
	 * The longitude of the map request's center point
	 */
	protected double lon;
	
	/**
	 * The width of the map request (in pixels)	
	 */
	protected int pixelW = 100;
	
	/**
	 * The height of the map request (in pixels)
	 */
	protected int pixelH = 100;
	
	/**
	 * The desired zoom level
	 */
	protected int zoom = 1;
	
	/**
	 * The desired tile size
	 */
	protected int tileSize;
	
	/**
	 * Retrieves the desired tile size for this request
	 * @return the desired tile size for this request
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * Sets the desired tile size for this request
	 * @param tileSize  the desired tile size for this request
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/**
	 * Returns the latitude of the map request's center point
	 * @return the latitude of the map request's center point
	 */
	public double getLat() {
		return this.lat;
	}
	
	/**
	 * Returns the longitude of the map request's center point
	 * @return the longitude of the map request's center point
	 */
	public double getLon() {
		return this.lon;
	}	
	
	/**
	 * Sets the latitude of the map request's center point
	 * @param lat the latitude of the map request's center point
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	/**
	 * Sets the longitude of the map request's center point
	 * @param lat the longitude of the map request's center point
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}	

	/**
	 * Returns the request's width (in pixels)
	 * @return the request's width (in pixels)
	 */
	public int getWidthInPixels() {
		return this.pixelW;
	}
	
	/** Sets the request's width (in pixels)
	 * @param w the request's width (in pixels)
	 */
	public void setWidthInPixels(int w) {
		this.pixelW = w;
	}
	
	/**
	 * Returns the request's height (in pixels)
	 * @return the request's height (in pixels)
	 */
	public int getHeightInPixels() {
		return this.pixelH;
	}
	
	/**
	 * Sets the request's height (in pixels)
	 * @param h the request's height (in pixels)
	 */
	public void setHeightInPixels(int h) {
		this.pixelH = h;
	}
	
	/**
	 * Returns the request's desired zoom level
	 * @return the request's desired zoom level
	 */
	public int getZoom() {
		return this.zoom;
	}
	
	/**
	 * Sets the request's desired zoom level
	 * @param zoom the request's desired zoom level
	 */
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

}
