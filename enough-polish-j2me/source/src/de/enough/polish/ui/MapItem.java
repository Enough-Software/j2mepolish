//#condition polish.usePolishGui && polish.hasFloatingPoint
package de.enough.polish.ui;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import de.enough.polish.maps.MapProvider;
import de.enough.polish.maps.MapRequest;
import de.enough.polish.maps.MapResponse;
import de.enough.polish.maps.MapTileCache;
import de.enough.polish.maps.MapTiler;
import de.enough.polish.maps.MapTilerEventListener;
import de.enough.polish.maps.MapUtils;
import de.enough.polish.maps.PoiItem;
import de.enough.polish.util.Locale;

/**
 * This item implements a map view that the end-user can interact with. Zoom, scroll and POI support is present. The MapItem also acts as a regular
 * UI Container, useful for creating a GUI overlay.
 * @author Ovidiu Iliescu
 */
public class MapItem 
extends Container 
implements MapTilerEventListener 
{
	
	/**
	 * The MapTiler to use when rendering maps
	 */
	protected MapTiler tiler = null;
	
	/**
	 * The map response to render
	 */
	protected MapResponse responseToRender;
	
	/**
	 * Flag to track what individual tile from the response the item needs to render. If the item needs to render all tiles,
	 * this should be set to -1. This can change at anytime.
	 */
	protected int responseTileToRender = -1;
	
	/**
	 * The map's current latitude
	 */
	protected double lat;
	
	/**
	 * The map's current longitude
	 */
	protected double lon;
	
	/**
	 * The current zoom level for the item
	 */
	protected int zoomLevel = 0;
	
	/**
	 * Flag to track if the item is in map navigation mode or not
	 */
	protected boolean isInMapNavigationMode = false;
	
	/**
	 * The buffer image that the map view is drawn on
	 */
	protected Image bufferImage;
	
	/**
	 * The Vector containing the item's POI items
	 */
	protected Vector poiList = new Vector();
	
	//#if polish.hasPointerEvents
	
	/**
	 * The old x coordinate for the view's center point
	 */
	protected int oldX = -1;
	
	/**
	 * The old y coordinate for the view's center point
	 */
	protected int oldY = -1;
	
	/**
	 * Flag to track if POI Item activation should be ignored or not
	 */
	protected boolean ignorePoiItemActivation = false;
	
	/**
	 * The old latitude of the MapView
	 */
	protected double oldLat;
	
	/**
	 * The old longitude of the MapView
	 */
	protected double oldLon;
	//#endif
	
	/**
	 * The zoom in command
	 */
	public static Command ZOOM_IN_CMD = new Command( Locale.get("polish.command.zoom.in"), Command.ITEM, 0 );
	
	/**
	 * The zoom out command
	 */
	public static Command ZOOM_OUT_CMD = new Command( Locale.get("polish.command.zoom.out"), Command.ITEM, 0 );
	
	/**
	 * Constructs a new MapItem
	 * @param provider the map provider to use
	 */
	public MapItem(MapProvider provider) {
		this(provider, null);
	}	
 	
	/**
	 * Constructs a new MapItem
	 * @param provider the map provider to use
	 * @param cache the map cache to use
	 */
	public MapItem(MapProvider provider, MapTileCache cache) {
		this(provider, cache, null);
	}
	
	/**
	 * Constructs a new MapItem
	 * @param provider the map provider to use
	 * @param cache the map cache to use
	 * @param style the style to use
	 */
	public MapItem(MapProvider provider, MapTileCache cache, Style style) {
		super(style);
		this.tiler = new MapTiler(provider, cache);
		setCurrentMapProvider(provider);
		this.tiler.setListener(this);
		setAppearanceMode(Item.INTERACTIVE);
		updateZoomCommands();
	}
	
	/**
	 * Adds a POI to the item
	 * @param item the POI add
	 */
	public void addPoi(PoiItem item) {
		this.poiList.addElement(item);
	}
	
	/**
	 * Removes a given POI from the item
	 * @param item the POI item
	 */
	public void removePoi(PoiItem item) {
		this.poiList.removeElement(item);
	}
	
	/**
	 * Returns the item's POI vector
	 * @return a Vector containing PoiItems
	 */
	public Vector getRawPoiList() {
		return this.poiList;
	}
	
	/**
	 * Returns if the item is in map navigation mode or not
	 * @return true if the item is in map navigation mode
	 */
	public boolean isInMapNavigationMode() {
		return this.isInMapNavigationMode;
	}	
	
	/**
	 * Update's the Item's zoom item commands
	 */
	protected void updateZoomCommands() {
		try {
			removeCommand(ZOOM_IN_CMD);
			removeCommand(ZOOM_OUT_CMD);
		} catch (Exception ex) { };		

		MapProvider provider = this.tiler.getProvider();
		if ( provider != null ) {
			if ( getZoomLevel() > provider.getMinZoomLevel() ) {
				addCommand(ZOOM_OUT_CMD);
			}
			
			if ( getZoomLevel() < provider.getMaxZoomLevel() ) {
				addCommand(ZOOM_IN_CMD);
			}
		}
		
	}
	
	/**
	 * Sets the current map provider. This also resets the zoom level to the provider's recommended zoom level.
	 * @param provider the provider to use.
	 */
	public void setCurrentMapProvider(MapProvider provider) {
		this.tiler.setProvider(provider);
		if (provider != null ) {
			setZoomLevel(provider.getRecommendedZoomLevel()) ;
		} else {
			this.isInMapNavigationMode = false;			
			this.zoomLevel = -1;
			notifyStateChanged();
		}
	}
	
	/**
	 * Retrieves the MapTileCache to use, if any
	 * @param cache the MapTileCache to use
	 */
	public void setCurrentMapTileCache(MapTileCache cache) {
		this.tiler.setCache(cache);
	}
	
	/**
	 * Retrieves the current MapTileCached used
	 * @return the current MapTileCached used
	 */
	public MapTileCache getCurrentMapTileCache() {
		return this.tiler.getCache();
	}
	
	/**
	 * Sets the center coordinates for the map view 
	 * @param lat the latitude
	 * @param lon the longitude
	 */
	public void setCoords(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;		
		this.tiler.prepareResponse(lat, lon);
	}
	
	/**
	 * Returns the current set of coordinates for the maps' center point 
	 * @return a double [], the first element is the latitude, the second element is the longitude
	 */
	public double[] getCoords() {
		return new double[] {this.lat, this.lon};
	}
	
	/**
	 * Retrieves the current zoom level. If there's no map provider specified for the current MapItem, a zoom level of -1 is returned
	 * @return the current zoom level
	 */
	public int getZoomLevel() {
		return this.zoomLevel;
	}
	
	/**
	 * Returns the maximum allowed zoom level for the currently used map provider. If there's no map provider specified for the current MapItem, a zoom level of -1 is returned.
	 * @return the maximum allowed zoom level for the currently used map provider
	 */
	public int getMaxZoomLevel() {
		if ( this.tiler.getProvider() == null ) {
			return -1;
		} else {
			return this.tiler.getProvider().getMaxZoomLevel();
		}
	}
	
	/**
	 * Returns the minimum allowed zoom level for the currently used map provider. If there's no map provider specified for the current MapItem, a zoom level of -1 is returned.
	 * @return the minimum allowed zoom level for the currently used map provider
	 */
	public int getMinZoomLevel() {
		if ( this.tiler.getProvider() == null ) {
			return -1;
		} else {
			return this.tiler.getProvider().getMinZoomLevel();
		}
	}

	/**
	 * Sets the MapItem's zoom level. If an out of bounds zoom level is specified, it will be clipped to the
	 * permitted zoom level range. If there's no map provider associated with the map item, the zoom level is set to -1.
	 * @param zoomLevel the desired zoom level.
	 */
	public void setZoomLevel(int zoomLevel) {
		
		// If there's no map provider associated, set the zoom level to -1
		if ( this.tiler.getProvider() == null ) {
			this.zoomLevel = -1;
			return;
		}
		
		// If the desired zoom level is the same as the current zoom level, do nothing
		if ( this.zoomLevel == zoomLevel ) {
			return;
		}
		
		// Clip the zoom level
		if ( zoomLevel > this.tiler.getProvider().getMaxZoomLevel() ) {
			zoomLevel = this.tiler.getProvider().getMaxZoomLevel();
		} else if ( zoomLevel < this.tiler.getProvider().getMinZoomLevel() ) {
			zoomLevel = this.tiler.getProvider().getMinZoomLevel();
		}
		
		// Update the zoom level
		this.zoomLevel = zoomLevel;	
		this.tiler.setZoomLevel(this.zoomLevel);
		
		// Update the item's own zoom commands
		updateZoomCommands();
		
		// Prepare the response for the new zoom level
		this.tiler.prepareResponse(this.lat, this.lon);
		
		// Notify that the item's state has changed
		notifyStateChanged();
	}

	/**
	 * Triggers a refresh of the MapItem. Currently, this is done by triggering a full repaint
	 */
	protected void triggerRefresh() {
		repaintFully();
		this.screen.serviceRepaints();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		
		// If there's no buffer image, there's nothing to paint
		if ( this.bufferImage == null ) {
			return;
		}
		
		// The MapRequest for the current view
		MapRequest request = null;
		
		// Get the buffer Graphics object
		Graphics bufferGraphics = this.bufferImage.getGraphics();
		
		// Draw the current response, if applicable
		if ( this.responseToRender != null ) {
			request = this.responseToRender.getAssociatedMapRequest();
			
			if ( this.responseTileToRender < 0 ) {
				// Draw the whole response, if there's no specific tile given
				this.tiler.drawWholeResponse(bufferGraphics, this.responseToRender, 0, 0);
			} else {
				// Draw only the specified response tile, if one is given
				this.tiler.drawIndividualResponseTile(bufferGraphics, this.responseToRender, 0, 0, this.responseTileToRender);
			}
			
			// Draw the map buffer on the Graphics object
			g.drawImage(this.bufferImage, x, y, Graphics.TOP | Graphics.LEFT);
		}
		
		// For drawing the PoiItems, set the clipping rectangle
		g.setClip(x, y, getContentWidth(), getContentHeight());
		
		// If there's a MapRequest rendered, also render the PoiItems
		if ( request != null ) {
			
			// Go through all the PoiItems
			for (int i=0;i<this.poiList.size();i++) {
				
				// Get the current PoiItem
				PoiItem temp = (PoiItem) this.poiList.elementAt(i);
				
				// If the current PoiItem doesn't have an associated Polish item, ignore it
				if ( temp.getInnerItem() == null ) {
					continue;
				}
				
				// Retrieve the inner item and initialize it, if needed
				Item innerItem = temp.getInnerItem();
				if ( ! innerItem.isInitialized() ) {
					innerItem.init(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
				}
				
				// Calculate deltaX & deltaY in pixels, between the view's center point and the PoiItem's center point
				int poiCoords [] = this.tiler.getProvider().coordsToPixels(temp.getLat(), temp.getLon(), request.getZoom());
				int responseCoords [] = this.tiler.getProvider().coordsToPixels(request.getLat(), request.getLon(), request.getZoom());						
				int deltaY = poiCoords[0] - responseCoords[0];
				int deltaX = poiCoords[1] - responseCoords[1];
				
				// Check if the current PoiItem is visible
				if ( poiCoords[1] >= responseCoords[1] - this.responseToRender.getWidth()/2 - innerItem.itemWidth/2 &&
					poiCoords[0] >= responseCoords[0] - this.responseToRender.getHeight()/2 - innerItem.itemHeight/2 &&
					poiCoords[1] <= responseCoords[1] + this.responseToRender.getWidth()/2 + innerItem.itemWidth/2 && 
					poiCoords[0] <= responseCoords[0] + this.responseToRender.getHeight()/2 + innerItem.itemHeight/2 ) {					
					
					// Calculate the PoiItem's x and y coordinates relative to the MapItem's content area
					int targetX = this.responseToRender.getWidth()/2 + deltaX;
					int targetY = this.responseToRender.getHeight()/2 + deltaY;
					
					// Paint the PoiItem's inner item
					innerItem.paint(x+targetX-innerItem.itemWidth/2, y+targetY-innerItem.itemHeight/2, 0, 0, g);
				}
			}
		}		
		
		// Reset the clipping rectangle
		g.setClip(0,0, Integer.MAX_VALUE, Integer.MAX_VALUE);

		// Draw the regular Container children on top of everything else
		super.paintContent(x,y,leftBorder,rightBorder,g);
	} 
	
	/**
	 * Retrieves the top-most visible PoiItem at the given pointer position (relative to the item's top-left corner), if any.
	 * @param x the x pointer position
	 * @param y the y pointer position
	 * @return the top-most visible PoiItem at the given pointer position, or null if no such item exists
	 */
	protected PoiItem getPoiItemAtPointerPosition(int x, int y) {
		
		// Check if there's something actually rendered
		if ( this.responseToRender == null ) {
			return null;
		}	
		
		// Check if response is within bounds
		if ( ! isWithinBounds(x, y) ) {
			return null;
		}
		
		// Get the request based on which the current map view is painted
		MapRequest request = this.responseToRender.getAssociatedMapRequest();
		
		// Go through all PoiItems. Use reverse order, to match display order (last item drawn on top).
		for (int i=this.poiList.size()-1;i>=0;i--) {
			PoiItem temp = (PoiItem) this.poiList.elementAt(i);
			
			// If there's no inner item, there's no need to go further
			if ( temp.getInnerItem() == null ) {
				continue;
			}
			
			// Retrieve the inner item and initialize it, if needed
			Item innerItem = temp.getInnerItem();
			if ( ! innerItem.isInitialized() ) {
				innerItem.init(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
			}
			
			// Calculate deltaX & deltaY in pixels, between the view's center point and the PoiItem's center point
			int poiCoords [] = this.tiler.getProvider().coordsToPixels(temp.getLat(), temp.getLon(), request.getZoom());
			int responseCoords [] = this.tiler.getProvider().coordsToPixels(request.getLat(), request.getLon(), request.getZoom());			
			int deltaY = poiCoords[0] - responseCoords[0];
			int deltaX = poiCoords[1] - responseCoords[1];
			
			// Check if the current PoiItem is visible
			if ( poiCoords[1] >= responseCoords[1] - this.responseToRender.getWidth()/2 - innerItem.itemWidth/2 &&
				poiCoords[0] >= responseCoords[0] - this.responseToRender.getHeight()/2 - innerItem.itemHeight/2 &&
				poiCoords[1] <= responseCoords[1] + this.responseToRender.getWidth()/2 + innerItem.itemWidth/2 && 
				poiCoords[0] <= responseCoords[0] + this.responseToRender.getHeight()/2 + innerItem.itemHeight/2 ) {					
				
				// Calculate the PoiItem's x and y coordinates relative to the MapItem
				int targetX = this.responseToRender.getWidth()/2 + deltaX + this.paddingLeft + this.border.borderWidthLeft;
				int targetY = this.responseToRender.getHeight()/2 + deltaY + this.paddingTop + this.border.borderWidthTop;			
				
				// Calculate the PoiItem's bounds
				int startX = targetX-innerItem.itemWidth/2 ;
				int startY = targetY-innerItem.itemHeight/2 ;
				int endX = startX + innerItem.itemWidth;
				int endY = startY + innerItem.itemHeight;
				
				// Compare the PoiItem's bounds with the given pointer coordinates. If the pointer coordinates are within
				// the PoiItem's bounds, return the PoiItem
				if ( x >= startX && x <= endX && y >= startY && y <= endY ) {
					return temp;
				}
			}
		}
		
		// No PoiItem found at the given pointer position
		return null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#createCssSelector()
	 */
	protected String createCssSelector() {
		return null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {				

		// Adjust the Polish item and initialize it
		availHeight += this.paddingBottom + this.paddingTop + this.border.borderWidthTop + this.border.borderWidthBottom;		
		super.initContent(firstLineWidth, availWidth, availHeight);				
		setContentWidth( availWidth );
		setContentHeight( availHeight);
		
		// Create the map view buffer & request the map view
		this.bufferImage = Image.createImage(availWidth, availHeight);
		this.tiler.setZoomLevel(this.zoomLevel);
		this.tiler.setMapViewWidth(availWidth);
		this.tiler.setMapViewHeight(availHeight);
		this.tiler.prepareResponse(this.lat, this.lon);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapTilerEventListener#preloadedTilesReady(de.enough.polish.maps.MapTiler, de.enough.polish.maps.MapResponse)
	 */
	public void preloadedTilesReady(MapTiler tiler, MapResponse response) {
		this.responseToRender = response;
		this.responseTileToRender = -1;
		triggerRefresh();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapTilerEventListener#allTilesReady(de.enough.polish.maps.MapTiler, de.enough.polish.maps.MapResponse)
	 */
	public void allTilesReady(MapTiler tiler, MapResponse response) {
		this.responseToRender = response;
		this.responseTileToRender = -1;
		triggerRefresh();		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapTilerEventListener#individualTileReady(de.enough.polish.maps.MapTiler, de.enough.polish.maps.MapResponse, int)
	 */
	public void individualTileReady(MapTiler tiler, MapResponse response,
			int tileNo) {
		this.responseToRender = response;
		this.responseTileToRender = tileNo;
		triggerRefresh();		
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed (int keyCode, int gameAction) {
		if ( this.isInMapNavigationMode ) {
			// Do nothing on key press in map navigation mode
			return true;
		} else {
			return super.handleKeyPressed(keyCode, gameAction);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		// If we're in map navigation mode, translate the key to a new set of map coordinates.
		// Otherwise, let the parent implementation handle the key
		if ( this.isInMapNavigationMode ) {
			mapNavigation(keyCode, gameAction);
			return true;
		} else {
			return super.handleKeyRepeated(keyCode, gameAction);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased( int keyCode, int gameAction ) {
		
		// The FIRE key toggles between map navigation and regular Form navigation
		if ( gameAction == Canvas.FIRE && this.tiler.getProvider() != null ) {
			this.isInMapNavigationMode = !this.isInMapNavigationMode;
			notifyStateChanged();
			return true;
		}
		
		// If we're in map navigation mode, translate the key to a new set of map coordinates.
		// Otherwise, let the parent implementation handle the key
		if ( this.isInMapNavigationMode ) {
			mapNavigation(keyCode, gameAction);
			return true;
		} else {
			return super.handleKeyReleased(keyCode, gameAction);
		}
	}
	
	/**
	 * Implements map naigation via keypad
	 * @param keyCode the keycode of the pressed key
	 * @param gameAction the corresponding game action
	 */
	protected void mapNavigation(int keyCode, int gameAction ) {
		
		// Translate the key press to new map coordinates
		int oX = 0;
	    int oY = 0;
	    switch(gameAction)
	    {
	        case Canvas.UP:
	        	oY -= 10;
	        	break;
	        case Canvas.DOWN:
	        	oY += 10;
	            break;
	        case Canvas.LEFT:
	        	oX -= 10;
	            break;
	        case Canvas.RIGHT:
	        	oX += 10;
	            break;
	    }	    
	    double newCoords[] = this.tiler.getProvider().getCoordsByPixelOffset(this.lat, this.lon, oY, oX, this.zoomLevel);
	    
	    // Set the new coordinates
	    setCoords(newCoords[0],newCoords[1]);
	}
	
    /**
     * Checks if a given pixel (relative to the item) is within the bounds
     * of the Processing canvas.
     * @param x x coordinate of the pixel
     * @param y y coordinate of the pixel
     * @return true if the pixel is within bounds, false otherwise
     */
    protected boolean isWithinBounds(int x, int y)
    {
        if ( (x < this.paddingLeft) || (x > this.itemWidth-this.paddingRight) ||
              (y < this.paddingTop) || (y > this.itemHeight-this.paddingBottom) )
        {
            return false;
        }
        return true;
    }
    
	//#if polish.hasPointerEvents	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handlePointerDragged(int, int)
	 */
	public boolean handlePointerDragged(int x, int y) {
		
		// Ignore drag if there's no provider set
		if ( this.tiler.getProvider() == null ) {
			return true;
		}
		
		// Begin drag, if needed
		if ( this.oldX < 0 && isWithinBounds(x, y) ) {
			this.oldX = x;
			this.oldY = y;
			this.oldLat = this.lat;
			this.oldLon = this.lon;
			this.isInMapNavigationMode = true;
			notifyStateChanged();
			return true;
		} else if ( this.oldX < 0 ) {
			return false;
		}
		
		// Calculate the new view center coordinates
		int oX = this.oldX - x, oY = this.oldY - y;
		double newCoords[] = this.tiler.getProvider().getCoordsByPixelOffset(this.oldLat, this.oldLon, oY, oX, this.zoomLevel);
		
		//Move the view to the new coordinates
		setCoords(newCoords[0],newCoords[1]);
		
		// Ignore PoiItem activations if we moved more than 20 pixels from the origin
		if ( MapUtils.distanceBetweenPoints(this.oldX, this.oldY, x, y) > 15 ) {
			this.ignorePoiItemActivation = true;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {		
		if ( ! isWithinBounds(x, y) ) {
			return false;
		}		
		return super.handlePointerPressed(x, y);
	}		
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int x,
            int y) {
				
		// Get the PoiItem at the current pointer position, if any
		PoiItem itemAtPointer = getPoiItemAtPointerPosition(x, y);
		
		if ( itemAtPointer == null ) {
			// Do nothing if there's no PoiItem at the current pointer position
		} else if ( ! this.ignorePoiItemActivation ) {
			// If we're NOT ignoring item activations, interpret the pointer resleased as an "activated" event
			// and try to notify the listener, if one is present
			if ( itemAtPointer.getListener() != null ) {
				itemAtPointer.getListener().onActivated(itemAtPointer, this);
			}
		}	
		
		// Reset all drag/pointer related variables to defaults
		this.oldX = this.oldY = -1;	
		this.ignorePoiItemActivation = false;
		
		// Notify the view to refresh itself
		notifyStateChanged();
		
		return true;
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#defocus(de.enough.polish.ui.Style)
	 */
	public void defocus(Style style) {
		if ( this.isInMapNavigationMode ) {
			this.isInMapNavigationMode = false;
			notifyStateChanged();
		}
		super.defocus(style);		
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#handleCommand(de.enough.polish.ui.Command)
	 */
	protected boolean handleCommand( Command cmd ) {		
		if ( ZOOM_IN_CMD == cmd ) {
			setZoomLevel(getZoomLevel()+1);
			return true;
		} else if ( ZOOM_OUT_CMD == cmd ) {
			setZoomLevel(getZoomLevel()-1);
			return true;
		} else return super.handleCommand(cmd);
	}

}
