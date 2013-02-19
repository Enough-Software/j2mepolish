//#condition polish.hasFloatingPoint
package de.enough.polish.maps.nokia;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;

import de.enough.polish.maps.MapProvider;
import de.enough.polish.maps.MapRequest;
import de.enough.polish.maps.MapResponse;
import de.enough.polish.maps.MapTile;
import de.enough.polish.util.MathUtil;

/**
 * This class implements a {@link MapProvider} based on Nokia Maps
 * @author Ovidiu Iliescu
 */
public class NokiaMapProvider implements MapProvider {

	/**
	 * The root URL for tiles
	 */
	public static String URL_ROOT = "http://i.maptile.maps.svc.ovi.com/maptiler/v2/maptile/newest/normal.day/";
	
	/**
	 * The app ID to use for auth
	 */
	protected String appID;
	
	/**
	 * The token to use for auth
	 */
	protected String token;
	
	/**
	 * The string buffer to use for creating request strings
	 */
	protected StringBuffer requestStr;
	
	/**
	 * Creates a new {@link NokiaMapProvider}
	 * @param appId the app ID to use for auth
	 * @param token the token to use for auth
	 */
	public NokiaMapProvider(String appId, String token) {
		this.appID = appId;
		this.token = token;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#resolveTile(de.enough.polish.maps.MapTile)
	 */
	public void resolveTile(MapTile tile) throws Exception {
		if ( tile.getTileImage() == null ) {
			tile.setTileImage( loadImage(tile.getUrl()));
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#getRequestForCoords(double, double, int, int, int)
	 */
	public MapRequest getRequestForCoords(double lat, double lon, int width, int height, int zoom) {
		MapRequest request = new MapRequest();
		request.setLat(lat);
		request.setLon(lon);
		request.setWidthInPixels(width);
		request.setHeightInPixels(height);
	    request.setZoom(zoom);
	    request.setTileSize(128);
	    return request;
	}	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#prepareResponse(de.enough.polish.maps.MapRequest)
	 */
	public MapResponse prepareResponse(MapRequest mapRequest) {
		
		MapResponse resp = new MapResponse();
		resp.setTiles(new Vector());
		resp.setAssociatedMapRequest(mapRequest);

		// Get the extreme tile coordinates for the given request position and size
		int pixelCoords[] = coordsToPixels(mapRequest.getLat(), mapRequest.getLon(), mapRequest.getZoom());
		int leftTile = (int) Math.floor( ((double) (pixelCoords[1] - mapRequest.getWidthInPixels()/2)) / mapRequest.getTileSize() );
		int rightTile = (int) Math.floor( ((double) (pixelCoords[1] + mapRequest.getWidthInPixels()/2)) / mapRequest.getTileSize() );
		int topTile = (int) Math.floor( ((double) (pixelCoords[0] - mapRequest.getHeightInPixels()/2)) / mapRequest.getTileSize() );
		int bottomTile = (int) Math.floor( ((double) (pixelCoords[0] + mapRequest.getHeightInPixels()/2)) / mapRequest.getTileSize() );
				
		// Figure out how many tiles we need per row	
		int tilesNeededPerRow = rightTile - leftTile + 1;		
		resp.setTilesPerRow(tilesNeededPerRow);
		
		// Drawing offsets
		int xOffset, yOffset;
		xOffset = - (  ( (pixelCoords[1] - mapRequest.getWidthInPixels()/2) % mapRequest.getTileSize() ) );
		yOffset = - (  ( (pixelCoords[0] - mapRequest.getHeightInPixels()/2) % mapRequest.getTileSize() ) );
		
		// Handle cases where the world size is smaller than map size, if needed.
		int desiredZoom = mapRequest.getZoom();
		int worldSizeInTiles = (int) MathUtil.pow(2, desiredZoom);
		if (rightTile-leftTile+1 >= worldSizeInTiles ) {
			leftTile = 0;
			rightTile = worldSizeInTiles-1;
			tilesNeededPerRow = worldSizeInTiles;
			resp.setTilesPerRow(tilesNeededPerRow);
			xOffset = mapRequest.getWidthInPixels()/2 - (  tilesNeededPerRow * mapRequest.getTileSize() )/2 ;
		}
		if (bottomTile-topTile+1 >= worldSizeInTiles ) {
			topTile = 0;
			bottomTile = worldSizeInTiles-1;
			yOffset = mapRequest.getHeightInPixels()/2 - (  (bottomTile-topTile+1) * mapRequest.getTileSize() )/2 ;
		}
		
		
		// Set the response size and offsets
		resp.setWidth(mapRequest.getWidthInPixels());
		resp.setHeight(mapRequest.getHeightInPixels());
        resp.setXOffset(xOffset);
        resp.setYOffset(yOffset);             
        
        // Generate each individual tile
		StringBuffer requestStr = new StringBuffer();		
        for (int y=topTile;y<=bottomTile;y++) {
        	for (int x=leftTile;x<=rightTile;x++) {
        		int thisTileX = x;
        		int thisTileY = y;
        		
        		// Reset string buffer
        		requestStr.setLength(0);
        		
        		// Append root url
        		 requestStr.append(URL_ROOT);
        		
        		// Append tile data
                requestStr.append(mapRequest.getZoom());
                requestStr.append('/');
                requestStr.append(thisTileX);
                requestStr.append('/');
                requestStr.append(thisTileY);
                requestStr.append('/');
                requestStr.append(mapRequest.getTileSize());
                requestStr.append("/png8");
                
        		// Append Auth data
        		requestStr.append("?app_id=");
        		requestStr.append(appID);
        		requestStr.append("&token=");
        		requestStr.append(token);	
        		
        		// Create the tile and add it to the vector
        		MapTile tile = new MapTile(null, thisTileY, thisTileX, mapRequest.getZoom(), requestStr.toString(), mapRequest.getTileSize(), mapRequest.getTileSize() );
        		resp.getTiles().addElement(tile);       		        		
        	}
        }
        
        return resp;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#getCoordsByPixelOffset(double, double, int, int, int)
	 */
	public double [] getCoordsByPixelOffset(double lat, double lng, int deltaY, int deltaX, int z) {
    	return adjust(lat, lng, deltaY, deltaX, z);
    }
    
    /* (non-Javadoc)
     * @see de.enough.polish.maps.MapProvider#coordsToPixels(double, double, int)
     */
    public int[] coordsToPixels(double lat, double lon, int zoomLevel) {
    	double PixelTileSize = 128d;
        double RadiansToDegreesRatio = Math.PI / 180d;
        double[] PixelGlobeCenter;
        double XPixelsToDegreesRatio;
        double YPixelsToRadiansRatio;
        
        double pixelGlobeSize = PixelTileSize * MathUtil.pow(2d, zoomLevel);
        XPixelsToDegreesRatio = pixelGlobeSize / 360d;
        YPixelsToRadiansRatio = pixelGlobeSize / (2d * Math.PI);
        double halfPixelGlobeSize = (pixelGlobeSize / 2d);
        PixelGlobeCenter = new double[]{halfPixelGlobeSize, halfPixelGlobeSize};
        
        double x = MathUtil.round(PixelGlobeCenter[0]
                + (lon * XPixelsToDegreesRatio));
            double f = Math.min(
                Math.max(
                     Math.sin(lat * RadiansToDegreesRatio),
                    -0.9999d),
                0.9999d);
            double y = PixelGlobeCenter[1] + .5d * 
                MathUtil.log((1d + f) / (1d - f)) * -YPixelsToRadiansRatio;           
            return new int[]{(int) y, (int) x};        
    }    

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#getMinZoomLevel()
	 */
	public int getMinZoomLevel() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#getMaxZoomLevel()
	 */
	public int getMaxZoomLevel() {
		return 20;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.maps.MapProvider#getRecommendedZoomLevel()
	 */
	public int getRecommendedZoomLevel() {
		return 17;
	}

    // Utility methods	
    // ---------------
    
	/**
	 * Globe offset
	 */
	static final int offset = 268435456; 
	
	/**
	 * Globe radius in radians
	 */
    static final double radius = offset / Math.PI; 
	
    /**
     * Adjust geo-coordinates by a pixel offset
     * @param lat latitude
     * @param lng longitude
     * @param deltaY y offset
     * @param deltaX x offset
     * @param z zoom level
     * @return the new coordinates, in {lat, lon} format
     */
    public double[] adjust(double lat, double lng, int deltaY,  int deltaX, int z)
    {
    	double resLat, resLon;
    	
    	if ( deltaY == 0 ) {
    		resLat = lat;
    	} else {
    		resLat = YToL(LToY(lat) + (deltaY<<(22-z))); 
    	}
    	
    	if ( deltaX == 0 ) {
    		resLon = lng;
    	} else {
    		resLon = XToL(LToX(lng) + (deltaX<<(22-z))); 
    	}    	
    	
    	return new double[]{ resLat, resLon	};
    }
    
    /**
     * Converts longitude to world-pixels
     * @param x longitude
     * @return longitude in world-pixels
     */
    double LToX(double x)
    {
    	return round(offset + radius * x * Math.PI / 180);
    }
     
    /**
     * Converts latitude to world-pixels
     * @param y latitude
     * @return latitude in world-pixels
     */
    double LToY(double y)
    {    	
    	double res = round(
    			offset - radius *
    			MathUtil.log(
    				(1 + Math.sin(y * Math.PI / 180))
    				/
    				(1 - Math.sin(y * Math.PI / 180))) / 2);
    	
    	return res;
    }
     
    /**
     * Converts world-pixel coordinates to longitude
     * @param x world-pixel x coordinates 
     * @return the resulting longitude
     */
    double XToL(double x)
    {
    	return ((round(x) - offset) / radius) * 180 / Math.PI;
    }
     
    
    /**
     * Converts world-pixel coordinates to latitude
     * @param x world-pixel x coordinates 
     * @return the resulting latitude
     */
    double YToL(double y)
    {
		double f = MathUtil.exp(((round(y)-offset)/radius));
		double dd = (Math.PI / 2 - 2 * MathUtil.atan(
				f
		)
	) * 180 / Math.PI;
		return dd;
    }
    
    /**
     * Rounds a number
     * @param num the number
     * @return the rounded number
     */
    double round(double num)
    {
    	double floor = Math.floor(num);
     
    	if(num - floor >= 0.5)
    		return Math.ceil(num);
    	else
    		return floor;
    }
	
	/**
	 * Loads an image from the given URL
	 * @param url the image URL
	 * @return the resultiung Image object
	 * @throws IOException
	 */
	static Image loadImage(String url) throws IOException {
		    HttpConnection hpc = null;
		    DataInputStream dis = null;
		    try {
		      hpc = (HttpConnection) Connector.open(url);
		      int length = (int) hpc.getLength();
		      byte[] data = new byte[length];
		      dis = new DataInputStream(hpc.openInputStream());
		      dis.readFully(data);
		      return Image.createImage(data, 0, data.length);
		    } finally {
		      if (hpc != null)
		        hpc.close();
		      if (dis != null)
		        dis.close();
		    }
		  }

}
