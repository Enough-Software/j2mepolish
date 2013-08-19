//#condition polish.hasFloatingPoint
package de.enough.polish.maps;

/**
 * Defines a map tiler even listener, that can respond to various events fired by a {@link MapTiler}
 * @author Ovidiu Iliescu
 */
public interface MapTilerEventListener {

	/**
	 * This even is fired when preparing a response, when all pre-loaded tiles of a response (tiles that are already in the map tiler's current cache) are ready.
	 * In order to ensure smooth drawing, it is recommended that you refresh the entire map ({@see {@link MapTiler#drawWholeResponse(javax.microedition.lcdui.Graphics, MapResponse, int, int)}}) once this event is received.
	 * @param tiler the tiler that fired up the response
	 * @param response the response
	 */
	public void  preloadedTilesReady(MapTiler tiler, MapResponse response);
	
	/**
	 * This even is fired when preparing a response, when all the response tiles are ready.
	 * In order to ensure smooth drawing, it is recommended that you refresh the entire map ({@see {@link MapTiler#drawWholeResponse(javax.microedition.lcdui.Graphics, MapResponse, int, int)}}) once this event is received.
	 * @param tiler the tiler that fired up the response
	 * @param response the response
	 */
	public void  allTilesReady(MapTiler tiler, MapResponse response);
	
	/**
	 * This event is fired up when preparing a response, when an individual response tile is ready
	 * In order to ensure smooth drawing, it is recommended that you refresh the tile ({@see {@link MapTiler#drawIndividualResponseTile(javax.microedition.lcdui.Graphics, MapResponse, int, int, int)}}) once this event is received.
	 * @param tiler the tiler that fired up the response
	 * @param response the response
	 * @param tileNo the tile that is ready, {@see MapResponse#getTiles()}
	 */
	public void  individualTileReady(MapTiler tiler, MapResponse response, int tileNo);
}
