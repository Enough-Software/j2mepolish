//#condition polish.usePolishGui
package de.enough.polish.ui.borders;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Border;
import de.enough.polish.ui.Color;

/**
 * <p>Paints a border in several colors at the top, bottom, left, right or a combination of them.</p>
 * <p>
 * Following attributes can be used in the CSS definition:
 * </p>
 * <ul>
 *   <li><b>top-colors</b>: the colors of the top border, each for one line</li>
 *   <li><b>bottom-colors</b>: the colors of the bottom border, each for one line</li>
 *   <li><b>left-colors</b>: the colors of the left border, each for one line</li>
 *   <li><b>right-colors</b>: the colors of the right border, each for one line</li>
 * </ul>
 * <p>CSS Example:
 * <pre>
 * border {
 *  type: nested-colors;
 *  left-colors: #404245 #55595b;
 *  right-colors: #55595b #404245;
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, robert@enough.de
 */
public class NestedColorsBorder extends Border
{

	private final Color[]	leftColors;
	private final Color[]	rightColors;
	private final Color[]	topColors;
	private final Color[]	bottomColors;

	public NestedColorsBorder( Color[] leftColors, Color[] rightColors, Color[] topColors, Color[] bottomColors) 
	{
		super(getWidth(leftColors), getWidth(rightColors), getWidth(topColors), getWidth(bottomColors));
		this.leftColors = leftColors;
		this.rightColors = rightColors;
		this.topColors = topColors;
		this.bottomColors = bottomColors;
	}
	
	private static int getWidth(Color[] colors)
	{
		if (colors == null)
		{
			return 0;
		}
		return colors.length;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		int rightX = x + width - 1;
		int bottomY = y + height - 1;
		// paint top border:
		int border = this.borderWidthTop - 1;
		Color[] colors = this.topColors;
		while ( border >= 0) {
			int color = colors[border].getColor();
			g.setColor(color);
			g.drawLine( x, y + border, rightX, y + border );
			border--;
		}
		// paint bottom border:
		colors = this.bottomColors;
		border = this.borderWidthBottom - 1;
		while ( border >= 0) {
			int color = colors[border].getColor();
			g.setColor(color);
			g.drawLine( x, bottomY - border, rightX, bottomY - border );
			border--;
		}
		// paint left border:
		border = this.borderWidthLeft - 1;
		colors = this.leftColors;
		while ( border >= 0) {
			int color = colors[border].getColor();
			g.setColor(color);
			g.drawLine( x + border, y, x + border, bottomY );
			border--;
		}
		// paint right border:
		border = this.borderWidthRight - 1;
		colors = this.rightColors;
		while ( border >= 0) {
			int color = colors[border].getColor();
			g.setColor(color);
			g.drawLine( rightX - border, y, rightX - border, bottomY );
			border--;
		}
	}

}
