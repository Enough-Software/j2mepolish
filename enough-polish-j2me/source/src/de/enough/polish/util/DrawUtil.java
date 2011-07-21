//#condition polish.midp || polish.usePolishGui

/*
 * Created on Nov 23, 2005 at 2:42:24 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 * 
 * The implementation for filling a polygon on devices without Nokia-UI-API and without the BlackBerry API is based
 * upon JMicroPolygon: http://sourceforge.net/projects/jmicropolygon which is licensed under the Apache Software License
 */
package de.enough.polish.util;

import javax.microedition.lcdui.Graphics;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;
//#endif


/**
 * <p>Provides functions for drawing shadows, polygons, gradients, etc.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <p>
 * The implementation for filling a polygon on devices without Nokia-UI-API and without the BlackBerry API is based
 * upon JMicroPolygon: http://sourceforge.net/projects/jmicropolygon, which is licensed under the Apache Software License
 * </p>
 * <pre>
 * history
 *        Nov 23, 2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class DrawUtil {
	
	/**
	 * Draws a (translucent) filled out rectangle.
	 * Please note that this method has to create temporary arrays for pure MIDP 2.0 devices each time it is called, using a TranslucentSimpleBackground
	 * is probably less resource intensive.
	 * 
	 * @param x the horizontal start position
	 * @param y the vertical start position 
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle 
	 * @param color the argb color of the rectangle, when there is no alpha value (color & 0xff000000 == 0), the traditional g.fillRect() method is called
	 * @param g the graphics context
	 * @see de.enough.polish.ui.backgrounds.TranslucentSimpleBackground
	 */
	public static void fillRect( int x, int y, int width, int height, int color, Graphics g ) {
		if ((color & 0xff000000) == 0) {
			g.setColor(color);
			g.fillRect(x, y, width, height);
			return;
		}
		//#if polish.blackberry && polish.usePolishGui
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			int alpha = color >>> 24;
			bbGraphics.setGlobalAlpha( alpha );
			bbGraphics.setColor( color );
			bbGraphics.fillRect(x, y, width, height);
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque
		//#elif tmp.useNokiaUi
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			int[] xCoords = new int[4];
			xCoords[0] = x;
			xCoords[1] = x + width;
			xCoords[2] = x + width;
			xCoords[3] = x;
			int[] yCoords = new int[4];
			yCoords[0] = y;
			yCoords[1] = y;
			yCoords[2] = y + height;
			yCoords[3] = y + height;
			dg.fillPolygon( xCoords, 0, yCoords, 0, 4, color );
		//#elif polish.midp2
			//#ifdef polish.Bugs.drawRgbOrigin
				x += g.getTranslateX();
				y += g.getTranslateY();
			//#endif
				
			// check if the buffer needs to be created:
			int[] buffer = null;
			//#if polish.Bugs.drawRgbNeedsFullBuffer || polish.vendor == Generic
				//#if polish.vendor == Generic
					if (DeviceInfo.requiresFullRgbArrayForDrawRgb()) {
				//#endif
						buffer = new int[ width * height ];
						for (int i = buffer.length - 1; i >= 0 ; i--) {
							buffer[i] = color;
						}
				//#if polish.vendor == Generic
					}
				//#endif
			//#endif
			//#if !polish.Bugs.drawRgbNeedsFullBuffer
				//#if polish.vendor == Generic
					if (buffer == null) {
				//#endif
						buffer = new int[ width ];
						for (int i = buffer.length - 1; i >= 0 ; i--) {
							buffer[i] = color;
						}
				//#if polish.vendor == Generic
					}
				//#endif
			//#endif
			//#if polish.Bugs.drawRgbNeedsFullBuffer
				drawRgb(buffer, x, y, width, height, true, g);
			//#else
				//#if polish.vendor == Generic
					if (DeviceInfo.requiresFullRgbArrayForDrawRgb()) {
						drawRgb(buffer, x, y, width, height, true, g);
						return;
					}
				//#endif
				if (x < 0) {
					width += x;
					x = 0;
				}
				if (width <= 0) {
					return;
				}
				if (y < 0) {
					height += y;
					y = 0;
				}
				if (height <= 0) {
					return;
				}
				g.drawRGB(buffer, 0, 0, x, y, width, height, true);
			//#endif
		//#else
			g.setColor(color);
			g.fillRect(x, y, width, height);
		//#endif
	}


	/**
	 * Draws a polygon.
	 * 
	 * @param xPoints the x coordinates of the polygon
	 * @param yPoints the y coordinates of the polygon
	 * @param color the color of the polygon
	 * @param g the graphics context
	 */
	public static void drawPolygon( int[] xPoints, int[] yPoints, int color, Graphics g )
    {
		//#if polish.blackberry && polish.usePolishGui
			Object o = g; // this cast is needed, otherwise the compiler will complain
			              // that javax.microedition.lcdui.Graphics can never be casted
			              // to de.enough.polish.blackberry.ui.Graphics.
			net.rim.device.api.ui.Graphics graphics = ((de.enough.polish.blackberry.ui.Graphics) o).g;
			if ((color & 0xff000000) == 0) {
				color = 0xff000000 | color;
			}
			graphics.setColor( color );
			graphics.setColor(color);
			graphics.drawPathOutline( xPoints, yPoints, null, null, true);
		//#elif polish.api.nokia-ui
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			if ((color & 0xFF000000) == 0) {
				color |= 0xFF000000;
			}
			dg.drawPolygon(xPoints, 0, yPoints, 0, xPoints.length, color );
        //#else
        	// use default mechanism
	        int length = xPoints.length - 1;
			g.setColor( color );
	        for(int i = 0; i < length; i++) {
	            g.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
	        }
	        g.drawLine(xPoints[length], yPoints[length], xPoints[0], yPoints[0]);
        //#endif
    }

	/**
	 * Draws a filled out polygon.
	 * 
	 * @param xPoints the x coordinates of the polygon
	 * @param yPoints the y coordinates of the polygon
	 * @param color the color of the polygon
	 * @param g the graphics context
	 */
	public static void fillPolygon( int[] xPoints, int[] yPoints, int color, Graphics g ) {
		//#if polish.blackberry && polish.usePolishGui
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			if ((color & 0xff000000) == 0) {
				color = 0xff000000 | color;
			}
			bbGraphics.setColor( color );
			// rest of translation is handled in de.enough.polish.blackberry.ui.Graphics directly, so we have to adjust
			// this here manually
            int translateX = g.getTranslateX();
			int translateY = g.getTranslateY();
			bbGraphics.translate( translateX, translateY );
			bbGraphics.drawFilledPath( xPoints, yPoints, null, null);
			bbGraphics.translate( -translateX, -translateY );
		//#elif polish.api.nokia-ui
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			if ((color & 0xFF000000) == 0) {
				color |= 0xFF000000;
			}
			dg.fillPolygon(xPoints, 0, yPoints, 0, xPoints.length, color );
		//#else
			// ... use default mechanism by simple triangulation of the polygon. Holes within the polygon are not supported.
			// This code is based on JMicroPolygon: http://sourceforge.net/projects/jmicropolygon
			while (xPoints.length > 2) {
				// a, b & c represents a candidate triangle to draw.
				// a is the left-most point of the polygon
				int a = indexOfLeast(xPoints);
				// b is the point after a
				int b = (a + 1) % xPoints.length;
				// c is the point before a
				int c = (a > 0) ? a - 1 : xPoints.length - 1;
				// The value leastInternalIndex holds the index of the left-most
				// polygon point found within the candidate triangle, if any.
				int leastInternalIndex = -1;
				boolean leastInternalSet = false;
				// If only 3 points in polygon, skip the tests
				if (xPoints.length > 3) {
					// Check if any of the other points are within the candidate triangle
					for (int i=0; i<xPoints.length; i++) {
						if (i != a && i != b && i != c) {
							if (withinBounds(xPoints[i], yPoints[i],
											xPoints[a], yPoints[a],
											xPoints[b], yPoints[b],
											xPoints[c], yPoints[c])) 
							{
								// Is this point the left-most point within the candidate triangle?
								if (!leastInternalSet || xPoints[i] < xPoints[leastInternalIndex]) 
								{
									leastInternalIndex = i;
									leastInternalSet = true;
								}
							}
						}
					}
				}
				// No internal points found, fill the triangle, and reservoir-dog the polygon
				if (!leastInternalSet) {
					g.setColor( color );
					//#if polish.midp2
						g.fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c]);
					//#else
						fillTriangle(xPoints[a], yPoints[a], xPoints[b], yPoints[b], xPoints[c], yPoints[c], g );
					//#endif
					int[][] trimmed = trimEar(xPoints, yPoints, a);
					xPoints = trimmed[0];
					yPoints = trimmed[1];
					// Internal points found, split the polygon into two, using the line between
					// "a" (left-most point of the polygon) and leastInternalIndex (left-most
					// polygon-point within the candidate triangle) and recurse with each new polygon
				} else {
					int[][][] split = split(xPoints, yPoints, a, leastInternalIndex);
					int[][] poly1 = split[0];
					int[][] poly2 = split[1];
					fillPolygon( poly1[0], poly1[1], color, g );
					fillPolygon( poly2[0], poly2[1], color, g );
					break;
				}
			}
		//#endif
	}
	
	/**
	 * Fills the specified triangle.
	 * 
	 * @param x1 the x coordinate of the first vertex of the triangle
	 * @param y1 the y coordinate of the first vertex of the triangle
	 * @param x2 the x coordinate of the second vertex of the triangle
	 * @param y2 the y coordinate of the second vertex of the triangle
	 * @param x3 the x coordinate of the third vertex of the triangle
	 * @param y3 the y coordinate of the third vertex of the triangle
	 * @param g the graphics context
	 */
	public static void fillTriangle(int x1,
            int y1,
            int x2,
            int y2,
            int x3,
            int y3, Graphics g) 
	{
		//#if polish.midp2
			g.fillTriangle(x1, y1, x2, y2, x3, y3);
		//#elif polish.hasFloatingPoint
			int tmp;

			if (y1 > y2) {
				tmp = x1; x1 = x2; x2 = tmp;
				tmp = y1; y1 = y2; y2 = tmp;
			}

			if (y1 > y3) {
				tmp = x1; x1 = x3; x3 = tmp;
				tmp = y1; y1 = y3; y3 = tmp;
			}

			if (y2 > y3) {
				tmp = x2; x2 = x3; x3 = tmp;
				tmp = y2; y2 = y3; y3 = tmp;
			}

			double dx1, dx2, dx3;

			if (y2 - y1 > 0)
				dx1 = (double) (x2 - x1) / (double) (y2 - y1);
			else
				dx1 = 0;

			if (y3 - y1 > 0)
				dx2 = (double) (x3 - x1) / (double) (y3 - y1);
			else
				dx2 = 0;

			if (y3 - y2 > 0)
				dx3 = (double) (x3 - x2) / (double) (y3 - y2);
			else
				dx3 = 0;

			int pos_x1 = 0, pos_x2 = 0, pos_y = 0;

			pos_y = y1;
			int index1 = 1, index2 = 1;

			if (dx1 > dx2) {
				for (; pos_y < y2; pos_y++, index1++, index2++) {
					pos_x1 = x1 + (int) Math.floor(dx2 * index1 + 0.5);
					pos_x2 = x1 + (int) Math.floor(dx1 * index2 + 0.5);
					g.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}

				pos_x2 = x2;
				index2 = 0;

				for (; pos_y <= y3; pos_y++, index1++, index2++) {
					pos_x1 = x1 + (int) Math.floor(dx2 * index1 + 0.5);
					pos_x2 = x2 + (int) Math.floor(dx3 * index2 + 0.5);
					g.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}
			}
			else {
				for (; pos_y < y2; pos_y++, index1++, index2++) {
					pos_x1 = x1 + (int) Math.floor(dx1 * index1 + 0.5);
					pos_x2 = x1 + (int) Math.floor(dx2 * index2 + 0.5);
					g.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}

				pos_x1 = x2;
				index1 = 0;

				for (; pos_y <= y3; pos_y++, index1++, index2++) {
					pos_x1 = x2 + (int) Math.floor(dx3 * index1 + 0.5);
					pos_x2 = x1 + (int) Math.floor(dx2 * index2 + 0.5);
					g.drawLine(pos_x1, pos_y, pos_x2, pos_y);
				}
			}
		//#else
			int centerX = getCenter( x1, x2, x3 );
			int centerY = getCenter( y1, y2, y3 );
			boolean isPositionMoved;
			do {
				// drawTriangle( x1, y1, x2, y2, x3, y3, g );
				g.drawLine( x1, y1, x2, y2 );
				g.drawLine( x2, y2, x3, y3 ); 
				g.drawLine( x3, y3, x1, y1 );

				isPositionMoved = false;
				if (x1 < centerX) {
					x1++;
					isPositionMoved = true;
				} else if (x1 > centerX) {
					x1--;
					isPositionMoved = true;
				}
				if (x2 < centerX) {
					x2++;
					isPositionMoved = true;
				} else if (x2 > centerX) {
					x2--;
					isPositionMoved = true;
				}
				if (x3 < centerX) {
					x3++;
					isPositionMoved = true;
				} else if (x3 > centerX) {
					x3--;
					isPositionMoved = true;
				}
				if (y1 < centerY) {
					y1++;
					isPositionMoved = true;
				} else if (y1 > centerY) {
					y1--;
					isPositionMoved = true;
				}
				if (y2 < centerY) {
					y2++;
					isPositionMoved = true;
				} else if (y2 > centerY) {
					y2--;
					isPositionMoved = true;
				}
				if (y3 < centerY) {
					y3++;
					isPositionMoved = true;
				} else if (y3 > centerY) {
					y3--;
					isPositionMoved = true;
				}
			} while (isPositionMoved);
		//#endif
	}

	/**
	 * Retrieves the center position of all numbers
	 * @param n1 first number
	 * @param n2 second number
	 * @param n3 third number
	 * @return the center of all numbers: min( n1, n2, n3 ) +  (max( n1, n2, n3 ) - min( n1, n2, n3 )) / 2
	 */
	public static int getCenter(int n1, int n2, int n3) {
		int max = Math.max( n1, Math.max( n2, n3) );
		int min = Math.min( n1, Math.min( n2, n3 ) );
		return min + ((max - min) / 2);
	}

	/**
	 * Draws the specified triangle.
	 * 
	 * @param x1 the x coordinate of the first vertex of the triangle
	 * @param y1 the y coordinate of the first vertex of the triangle
	 * @param x2 the x coordinate of the second vertex of the triangle
	 * @param y2 the y coordinate of the second vertex of the triangle
	 * @param x3 the x coordinate of the third vertex of the triangle
	 * @param y3 the y coordinate of the third vertex of the triangle
	 * @param g the graphics context
	 */
	public static void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Graphics g) {
		g.drawLine( x1, y1, x2, y2 );
		g.drawLine( x2, y2, x3, y3 ); 
		g.drawLine( x3, y3, x1, y1 ); 		
	}

	/**
	 * Finds the index of the smallest element
	 * 
	 * @param elements the elements
	 * @return the index of the smallest element
	 */
	static int indexOfLeast(int[] elements) {
		int index = 0;
		int least = elements[0];
		for (int i=1; i<elements.length; i++) {
			if (elements[i] < least) {
				index = i;
				least = elements[i];
			}
		}
		return index;
	}
	
	/**
	 * Checks whether the specified point px, py is within the triangle defined by ax, ay, bx, by and cx, cy.
	 * 
	 * @param px The x of the point to test
	 * @param py The y of the point to test
	 * @param ax The x of the 1st point of the triangle
	 * @param ay The y of the 1st point of the triangle
	 * @param bx The x of the 2nd point of the triangle
	 * @param by The y of the 2nd point of the triangle
	 * @param cx The x of the 3rd point of the triangle
	 * @param cy The y of the 3rd point of the triangle
	 * @return true when the point is within the given triangle
	 */
	private static boolean withinBounds(int px, int py,
								int ax, int ay,
								int bx, int by,
								int cx, int cy) 
	{
		if (   px < Math.min(ax, Math.min( bx, cx ) )
				|| px > Math.max(ax, Math.max( bx, cx ) )
				|| py < Math.min(ay, Math.min( by, cy ) )
				|| py > Math.max(ay, Math.max( by, cy ) ) ) 
		{
			return false;
		}
		boolean sameabc = sameSide(px, py, ax, ay, bx, by, cx, cy);
		boolean samebac = sameSide(px, py, bx, by, ax, ay, cx, cy);
		boolean samecab = sameSide(px, py, cx, cy, ax, ay, bx, by);
		return sameabc && samebac && samecab;
	}
	
	private static boolean sameSide (int p1x, int p1y, int p2x, int p2y,
							int l1x, int l1y, int l2x, int l2y) 
	{
		long lhs = ((p1x - l1x) * (l2y - l1y) - (l2x - l1x) * (p1y - l1y));
		long rhs = ((p2x - l1x) * (l2y - l1y) - (l2x - l1x) * (p2y - l1y));
		long product = lhs * rhs;
		boolean result = product >= 0;
		return result;
	}
	
	private static int[][] trimEar(int[] xPoints, int[] yPoints, int earIndex) {
		int[] newXPoints = new int[xPoints.length - 1];
		int[] newYPoints = new int[yPoints.length - 1];
		int[][] newPoly = new int[2][];
		newPoly[0] = newXPoints;
		newPoly[1] = newYPoints;
		int p = 0;
		for (int i=0; i<xPoints.length; i++) {
			if (i != earIndex) {
				newXPoints[p] = xPoints[i];
				newYPoints[p] = yPoints[i];
				p++;
			}
		}
		return newPoly;
	}
	
	private static int[][][] split(int[] xPoints, int[] yPoints, int aIndex, int bIndex) {
		int firstLen, secondLen;
		if (bIndex < aIndex) {
			firstLen = (xPoints.length - aIndex) + bIndex + 1;
		} else {
			firstLen = (bIndex - aIndex) + 1;
		}
		secondLen = (xPoints.length - firstLen) + 2;
		int[][] first = new int[2][firstLen];
		int[][] second = new int[2][secondLen];
		for (int i=0; i<firstLen; i++) {
			int index = (aIndex + i) % xPoints.length;
			first[0][i] = xPoints[index];
			first[1][i] = yPoints[index];
		}
		for (int i=0; i<secondLen; i++) {
			int index = (bIndex + i) % xPoints.length;
			second[0][i] = xPoints[index];
			second[1][i] = yPoints[index];
		}
		int[][][] result = new int[2][][];
		result[0] = first;
		result[1] = second;
		return result;
	}
	
	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multiplication nor devision), but
	 * it will create a new integer array in each call. 
	 * 
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param steps the number of colors in the gradient, 
	 *        when 2 is given, the first one will be the startColor and the second one will the endColor.  
	 * @return an int array with the gradient.
	 * @see #getGradient(int, int, int[])
	 * @see #getGradientColor(int, int, int)
	 */
	public static int[] getGradient( int startColor, int endColor, int steps ) {
		if (steps <= 0) {
			return new int[0];
		}
		int[] gradient = new int[ steps ];
		getGradient(startColor, endColor, gradient);
		return gradient;

	}

	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multiplication nor devision).
	 * 
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param gradient the array in which the gradient colors are stored.  
	 * @see #getGradientColor(int, int, int, int)
	 */
	public static void getGradient(int startColor, int endColor, int[] gradient) {
		int steps = gradient.length;
		if (steps == 0) {
			return;
		} else if (steps == 1) {
			gradient[0] = startColor;
			return; 
		}
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0x00FF;
		int startGreen = (startColor >>> 8) & 0x0000FF;
		int startBlue = startColor  & 0x00000FF;

		int endAlpha = endColor >>> 24;
		int endRed = (endColor >>> 16) & 0x00FF;
		int endGreen = (endColor >>> 8) & 0x0000FF;
		int endBlue = endColor  & 0x00000FF;
		
		int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps-1);
		int stepRed = ((endRed -startRed) << 8) / (steps-1);
		int stepGreen = ((endGreen - startGreen) << 8) / (steps-1);
		int stepBlue = ((endBlue - startBlue) << 8) / (steps-1);
//		System.out.println("step red=" + Integer.toHexString(stepRed));
//		System.out.println("step green=" + Integer.toHexString(stepGreen));
//		System.out.println("step blue=" + Integer.toHexString(stepBlue));
		
		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;
		
		gradient[0] = startColor;
		for (int i = 1; i < steps; i++) {
			startAlpha += stepAlpha;
			startRed += stepRed;
			startGreen += stepGreen;
			startBlue += stepBlue;
			
			gradient[i] = (( startAlpha << 16) & 0xFF000000)
				| (( startRed << 8) & 0x00FF0000)
				| ( startGreen & 0x0000FF00)
				| ( startBlue >>> 8);
				//| (( startBlue >>> 8) & 0x000000FF);
		}	
	}
	
	/**
	 * Retrieves the gradient color between the given start and end colors.
	 * 
	 * @param startColor the start color
	 * @param endColor the end color
	 * @param permille the permille between 0 and 1000 - 0 will return the startColor, 1000 the endColor, 
	 * 			500 a gradient color directly in the middlet between start and endcolor.
	 * @return the gradient color
	 */
	public static int getGradientColor( int startColor, int endColor, int permille ) {
		int alpha = startColor >>> 24;
		int red = (startColor >>> 16) & 0x00FF;
		int green = (startColor >>> 8) & 0x0000FF;
		int blue = startColor  & 0x000000FF;

		int diffAlpha = (endColor >>> 24) - alpha;
		int diffRed   = ( (endColor >>> 16) & 0x00FF ) - red;
		int diffGreen = ( (endColor >>> 8) & 0x0000FF ) - green;
		int diffBlue  = ( endColor  & 0x000000FF ) - blue;
		
		alpha += (diffAlpha * permille) / 1000;
		red   += (diffRed   * permille) / 1000;
		green += (diffGreen * permille) / 1000;
		blue  += (diffBlue  * permille) / 1000;
		
		return ( alpha << 24 ) 
		     | ( red   << 16 ) 
		     | ( green <<  8 ) 
		     | ( blue        );		

		
//		return (( alpha << 24) & 0xFF000000)
//			| (( red << 16) & 0x00FF0000)
//			| ( (green << 8) & 0x0000FF00)
//			| ( blue );		
	}
	
	/**
	 * Retrieves the gradient color between the given start and end colors.
	 * This method returns getGradientColor(startColor, endColor, (step * 1000)/numberOfSteps);
	 * 
	 * @param startColor the start color
	 * @param endColor the end color
	 * @param step the step/position within the gradient
	 * @param numberOfSteps the maxium step (=100%)
	 * @return the gradient color
	 * @see #getGradientColor(int, int, int)
	 */
	public static int getGradientColor( int startColor, int endColor, int step, int numberOfSteps ) {
		int permille = (step * 1000) / numberOfSteps;
		return getGradientColor(startColor, endColor, permille);
	}
	
	/**
	 * Retrieves the complementary color to the specified one.
	 * 
	 * @param color the original argb color
	 * @return the complementary color with the same alpha value
	 */
	public static int getComplementaryColor( int color ) {
		return  ( 0xFF000000 & color )
			| ((255 - (( 0x00FF0000 & color ) >> 16)) << 16)
			| ((255 - (( 0x0000FF00 & color ) >> 8)) << 8)
			| (255 - ( 0x000000FF & color ) );				
	}

	
	/**
	 * <p>Paints a dropshadow behind a given ARGB-Array, whereas you are able to specify
	 *  the shadows inner and outer color.</p>
	 * <p>Note that the dropshadow just works for fully opaque pixels and that it needs 
	 * a transparent margin to draw the shadow.
	 * </p>
	 * <p>Choosing the same inner and outer color and varying the transparency is recommended.
	 *  Dropshadow just works for fully opaque pixels.</p>
	 * 
	 * @param argbData the images ARGB-Array
	 * @param width the width of the ARGB-Array
	 * @param height the width of the ARGB-Array
	 * @param xOffset use this for finetuning the shadow's horizontal position. Negative values move the shadow to the left.
	 * @param yOffset use this for finetuning the shadow's vertical position. Negative values move the shadow to the top.
	 * @param size use this for finetuning the shadows radius.
	 * @param innerColor the inner color of the shadow, which should be less opaque than the text.
	 * @param outerColor the outer color of the shadow, which should be less than opaque the inner color.
	 * 
	 */
	public static void dropShadow(int[] argbData, int width, int height,int xOffset, int yOffset, int size, int innerColor, int outerColor){
		
		// additional Margin for the image because of the shadow
		int iLeft = size-xOffset<0 ? 0 : size-xOffset;
		int iRight = size+xOffset<0 ? 0 : size+xOffset;
		int iTop = size-yOffset<0 ? 0 : size-yOffset;
		int iBottom = size+yOffset<0 ? 0 : size+yOffset;
		
		// set colors
		int[] gradient = DrawUtil.getGradient( innerColor, outerColor, size );
		
		// walk over the text and look for non-transparent Pixels	
		for (int ix=-size+1; ix<size; ix++){
			for (int iy=-size+1; iy<size; iy++){
				//int gColor=gradient[ Math.max(Math.abs(ix),Math.abs(iy))];
				//int gColor=gradient[(Math.abs(ix)+Math.abs(iy))/2];

				// compute the color and draw all shadowPixels with offset (ix, iy)
				//#if polish.cldc1.1 
					int r = (int) Math.sqrt(ix*ix+iy*iy); // TODO: this might be a bit slowly
				//#elif polish.cldc1.0 
					//# int r = (Math.abs(ix)+Math.abs(iy))/2; // TODO: this looks a bit uncool
				//#endif
				if ( r<size) {
					int gColor = gradient[ r ];
					
					for (int col=iLeft,row; col<width/*+iLeft*/-iRight; col++) { 
						for (row=iTop;row<height-iBottom/*+iTop*/-1;row++){
							
							// draw if an opaque pixel is found and the destination is less opaque then the shadow
							if (argbData[row*(width /*+ size*2*/) + col]>>>24==0xFF 
									&& argbData[(row+yOffset+iy)*(width /* size*2*/) + col+xOffset+ix]>>>24 < gColor>>>24)
							{
								argbData[(row+yOffset+iy)*(width /*+ size*2*/) + col+xOffset+ix]=gColor;
							}
						}
					}
				}
			}
		}

	} 
	
	static int COLOR_BIT_MASK	= 0x000000FF;
	public static byte[][] FILTER_GAUSSIAN_2 = // a small and fast gaussian filtermatrix
									 {{1,2,1},
									  {2,4,2},
									  {1,2,1}};
	public static byte[][] FILTER_GAUSSIAN_3 = // a gaussian filtermatrix
	       			        {{0,1,2,1,0},
	       					 {1,3,5,3,1},
	       					 {2,5,9,5,2},
	       					 {1,3,5,3,1},
	       					 {0,1,2,1,0}};
	
	/**
	 * Performs a convolution of an image with a given matrix. 
	 * @param filterMatrix a matrix, which should have odd rows an colums (not neccessarily a square). The matrix is used for a 2-dimensional convolution. Negative values are possible.  
	 * @param brightness you can vary the brightness of the image measured in percent. Note that the algorithm tries to keep the original brightness as far as is possible.
	 * @param argbData the image (RGB+transparency)
	 * @param width of the given Image
	 * @param height of the given Image
	 * Be aware that the computation time depends on the size of the matrix.
	 */
	public static void applyFilter(byte[][] filterMatrix, int brightness, int[] argbData, int width, int height) {
		
		// check whether the matrix is ok
		if (filterMatrix.length % 2 !=1 || filterMatrix[0].length % 2 !=1 ){
			 throw new IllegalArgumentException();
		}
		
		int fhRadius=filterMatrix.length/2+1;
		int fwRadius=filterMatrix[0].length/2+1;
		int currentPixel=0;
		int newTran, newRed, newGreen, newBlue;
		
		// compute the bightness 
		int divisor=0;
		for (int fCol, fRow=0; fRow < filterMatrix.length; fRow++){
			for (fCol=0; fCol < filterMatrix[0].length; fCol++){
				divisor+=filterMatrix[fRow][fCol];
			}
		}
		// TODO: if (divisor==0), because of negativ matrixvalues
		if (divisor==0) {
			return; // no brightness
		}
		
		// copy the neccessary imagedata into a small buffer
		int[] tmpRect=new int[width*(filterMatrix.length)];
		System.arraycopy(argbData,0, tmpRect,0, width*(filterMatrix.length));
		
		for (int fCol, fRow, col, row=fhRadius-1; row+fhRadius<height+1; row++){
			for (col=fwRadius-1; col+fwRadius<width+1; col++){
				
				// perform the convolution
				newTran=0; newRed=0; newGreen=0; newBlue=0;
				
				for (fRow=0; fRow<filterMatrix.length; fRow++){
					
					for (fCol=0; fCol<filterMatrix[0].length;fCol++){

						// take the Data from the little buffer and skale the color 
						currentPixel = tmpRect[fRow*width+col+fCol-fwRadius+1];
						if (((currentPixel >>> 24) & COLOR_BIT_MASK) != 0) {
							newTran	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 24) & COLOR_BIT_MASK);
							newRed	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 16) & COLOR_BIT_MASK);
							newGreen+= filterMatrix[fRow][fCol] * ((currentPixel >>> 8) & COLOR_BIT_MASK);
							newBlue	+= filterMatrix[fRow][fCol] * (currentPixel & COLOR_BIT_MASK);
						}
						
					}
				}
				
				// calculate the color	
				newTran = newTran * brightness/100/divisor;
				newRed  = newRed  * brightness/100/divisor;
				newGreen= newGreen* brightness/100/divisor;
				newBlue = newBlue * brightness/100/divisor;
			
				newTran =Math.max(0,Math.min(255,newTran));
				newRed  =Math.max(0,Math.min(255,newRed));
				newGreen=Math.max(0,Math.min(255,newGreen));
				newBlue =Math.max(0,Math.min(255,newBlue));
				argbData[(row)*width+col]=(newTran<<24 | newRed<<16 | newGreen <<8 | newBlue);
				
			}
			
			// shift the buffer if we are not near the end
			if (row+fhRadius!=height) { 
				System.arraycopy(tmpRect,width, tmpRect,0, width*(filterMatrix.length-1));	// shift it back
				System.arraycopy(argbData,width*(row+fhRadius), tmpRect,width*(filterMatrix.length-1), width);	// add new data
			}
		}
		
	}
	/**
	 * This class is used for fadeEffects (FadeTextEffect and FadinAlienGlowEffect).
	 * The you can set a start and an end color as well as some durations.
	 * 
	 * Note: stepsIn has to be the same as  stepsOut or 0!
	 * 
	 * @author Simon Schmitt
	 */
	public static class FadeUtil{
		public final static int FADE_IN =1;
		public final static int FADE_OUT=2;
		public final static int FADE_LOOP=3;
		public final static int FADE_BREAK=0;
		
		public int[] gradient;
		public boolean changed;
		
		public int startColor	=0xFF0080FF;
		public int endColor	=0xFF80FF00;
		
		public int steps;
		public int delay=0; 				// time till the effect starts
		public int stepsIn=5,stepsOut=5;  	// fading duration
		public int sWaitTimeIn=10; 		// time to stay faded in
		public int sWaitTimeOut=0; 		// time to stay faded out
		public int mode=FADE_LOOP;
		
		public int cColor;
		public int cStep;
		
		private void initialize(){
			//System.out.println(" init");

			this.cStep=0;
			
			switch (this.mode){
			case FADE_OUT:
				this.stepsIn=0;
				this.sWaitTimeIn=0;
				this.cColor=this.endColor;
				break;
			case FADE_IN:
				this.stepsOut=0;
				this.sWaitTimeOut=0;
				this.cColor=this.startColor;
				break;
			default://loop
				this.cColor=this.startColor;
			}

			this.cStep-=this.delay;
			
			this.steps= this.stepsIn+this.stepsOut+this.sWaitTimeIn+this.sWaitTimeOut;
			
			this.gradient = DrawUtil.getGradient(this.startColor,this.endColor,Math.max(this.stepsIn, this.stepsOut));

			
		}
		
		public boolean step(){
			this.cStep++;
			
			// (re)define everything, if something changed 
			if (this.gradient==null | this.changed) {
				initialize();
			} 
			this.changed=false;
			
			// exit, if no animation is neccessary
			if (this.mode == FADE_BREAK){
				return false; 
			}
			// we have to ensure that a new picture is drawn
			if (this.cStep<0){
				return true;
			}
			
			// set counter to zero (in case of a loop) or stop the engine, when we reached the end
			if (this.cStep==this.steps){
				this.cStep=0;
				
				if (this.mode != FADE_LOOP) {
					this.mode = FADE_BREAK;
					return true;
				}
			}
			
			if (this.cStep<this.stepsIn){	
				// fade in
				this.cColor=this.gradient[this.cStep];	
				//System.out.println("  [in] color:"+this.cStep);
				return true;
				
			} else if (this.cStep<this.stepsIn+this.sWaitTimeIn){
				// have a break
				if (this.cColor!=this.endColor){
					this.cColor=this.endColor;
					return true;
				}
				
				//System.out.println("  color:end color");
				
			} else if( this.cStep<this.stepsIn+this.sWaitTimeIn+this.stepsOut){ 
				// fade out 
				this.cColor=this.gradient[this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1];
				//System.out.println("  [out] color:"+(this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1));
				return true;
				
			} else { 
				// have another break
				if (this.cColor!=this.startColor){
					this.cColor=this.startColor;
					return true;
				}
				//System.out.println("  color:start color");
			} 
			
			// it sees as if we had no change...
			return false;
		}
	}

	/**
	 * Draws a translucent line on MIDP 2.0+ and Nokia-UI-API devices.
	 * Note that on pure MIDP 1.0 devices without support for the Nokia-UI-API the translucency is ignored.
	 * 
	 * @param color the ARGB color
	 * @param x1 horizontal start position 
	 * @param y1 vertical start position
	 * @param x2 horizontal end position
	 * @param y2 vertical end position
	 * @param g the graphics context
	 */
	public static void drawLine( int color, int x1, int y1, int x2, int y2, Graphics g) {
		//#if polish.blackberry && polish.usePolishGui
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			int alpha = color >>> 24;
			bbGraphics.setGlobalAlpha( alpha );
			bbGraphics.setColor( color );
			bbGraphics.drawLine(x1, y1, x2, y2);
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque
		//#elif polish.api.nokia-ui && !polish.Bugs.TransparencyNotWorkingInNokiaUiApi && !polish.Bugs.TransparencyNotWorkingInDrawPolygon
			int[] xPoints = new int[] { x1, x2 };
			int[] yPoints = new int[] { y1, y2 };
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			dg.drawPolygon(xPoints, 0, yPoints, 0, 2, color );
		//#elifdef polish.midp2
			if (y2 < y1 ) {
				int top = y2;
				y2 = y1;
				y1 = top; 
			}
			if (x2 < x1) {
				int left = x2;
				x1 = x2;
				x2 = x1;
				x1 = left;
			}
//			int[] rgb = new int[]{ color };
//			if (y1 == y2) {
//				int start = Math.max( x1, 0);
//				for (int i = start; i < x2; i++ ) {
//					g.drawRGB(rgb, 0, 0, start + i, y1, 1, 1, true ); 
//				}
//			} else if (x1 == x2) {
//				int start = Math.max( y1, 0);
//				for (int i = start; i < y2; i++ ) {
//					g.drawRGB(rgb, 0, 0, x1, start + i, 1, 1, true ); 
//				}				
//			}
			
			if (x1 == x2 || y1 == y2) {
//				int[] rgb = new int[]{ color };
//				g.drawRGB( rgb, 0, 0, x1, y1, x2 - x1, y2 - y1, true );
				int width = x2 - x1;
				if (width == 0) {
					width = 1;
				}
				int height = y2 - y1;
				if (height == 0) {
					height = 1;
				}
				int[] rgb = new int[ Math.max( width, height )];
				for (int i = 0; i < rgb.length; i++) {
					rgb[i] = color;
				}
				// the scanlength should really be 0, but we use width so that 
				// this works on Nokia Series 40 devices as well:
				// drawRGB(		  int[] rgbData,
				//                int offset,
				//                int scanlength, <<< this _should_ allow any value, even 0 or negative ones
				//                int x,
				//                int y,
				//                int width,
				//                int height,
				//                boolean processAlpha)
				g.drawRGB( rgb, 0, width, x1, y1, width, height, true ); 
			} else {
				// TODO use alpha channel
				g.setColor( color );
				g.drawLine(x1, y1, x2, y2);
			}
		//#else
			g.setColor( color );
			g.drawLine(x1, y1, x2, y2);
		//#endif
	}
	
	/**
	 * Draws an (A)RGB array and fits it into the clipping area.
	 * 
	 * @param rgb the (A)RGB array
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param width the width of the RGB array
	 * @param height the heigt of the RGB array
	 * @param processAlpha true when the alpha values should be used so that pixels are blended with the background
	 * @param g the graphics context
	 */
	public static void drawRgb( int[] rgb, int x, int y, int width, int height, boolean processAlpha, Graphics g) {
		drawRgb( rgb, x, y, width, height, processAlpha, g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight(), g );
	}


	/**
	 * Draws an (A)RGB array and fits it into the clipping area.
	 * 
	 * @param rgb the (A)RGB array
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 * @param width the width of the RGB array
	 * @param height the heigt of the RGB array
	 * @param processAlpha true when the alpha values should be used so that pixels are blended with the background
	 * @param clipX the horizontal start of the clipping area
	 * @param clipY the vertical start of the clipping area
	 * @param clipWidth the width of the clipping area
	 * @param clipHeight the height of the clipping area
	 * @param g the graphics context
	 */
	public static  void drawRgb(int[] rgb, int x, int y, int width, int height,
			boolean processAlpha, int clipX, int clipY, int clipWidth,
			int clipHeight, Graphics g)
	{
		if (x + width < clipX || x > clipX + clipWidth || y + height < clipY || y > clipY + clipHeight) {
			// this is not within the visible bounds:
			return;
		}
		// adjust x / y / width / height to draw RGB within visible bounds:
		int offset = 0;
		if (x < clipX) {
			offset = clipX - x;
			x = clipX;
		}
		int scanlength = width;
		width -= offset;
		if (x + width > clipX + clipWidth) {
			width = (clipX + clipWidth) - x;
		}
		if (width <= 0) {
			return;
		}
		if (y < clipY) {
			offset += (clipY - y) * scanlength;
			height -= (clipY - y);
			y = clipY;
		}
		if (y + height > clipY + clipHeight) {
			height = (clipY + clipHeight) - y;
		}
		if (height <= 0) {
			return;
		}
		
		//#if polish.midp2
			g.drawRGB(rgb, offset, scanlength, x, y, width, height,  processAlpha);
		//#endif
	}


	/**
	 * Draws an RGB Image
	 * @param image the image
	 * @param x the horizontal position
	 * @param y the vertical position
	 * @param g the graphics context
	 */
	public static void drawRgb(RgbImage image, int x, int y, Graphics g)
	{
		drawRgb( image.getRgbData(), x, y, image.getWidth(), image.getHeight(), image.isProcessTransparency(), g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight(), g );
	}
	
	/**
	 * Draws the outline of the specified rectangle using the current color and stroke style. 
	 * The resulting rectangle will cover an area (width + 1) pixels wide by (height + 1) pixels tall. 
	 * If either width or height is less than zero, nothing is drawn.
	 * 
	 * Includes transparent drawing for BlackBerry devices.
	 * 
	 * @param x the x coordinate of the rectangle to be drawn
	 * @param y the y coordinate of the rectangle to be drawn
	 * @param width the width of the rectangle to be drawn
	 * @param height the height of the rectangle to be drawn
	 * @param g the graphics context
	 */
	public static void drawRect(int color, int x, int y, int width, int height, Graphics g) {
		//#if polish.blackberry
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			int alpha = color >>> 24;
			bbGraphics.setGlobalAlpha( alpha );
			bbGraphics.setColor(color);
			bbGraphics.drawRect(x, y, width, height);
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque		
		//#else 
			g.setColor(color);
			g.drawRect(x, y, width, height);
		//#endif
	}
}
