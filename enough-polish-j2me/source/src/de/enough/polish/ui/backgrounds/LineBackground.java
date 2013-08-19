package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;

public class LineBackground extends Background
{
	public static final int POSITION_CENTER = 0;
	public static final int POSITION_TOP = 1;
	public static final int POSITION_BOTTOM = 2;
	private Dimension lineWidth;
	private Color color;
	private int position;
	

	public LineBackground(Dimension width, Color color, int position)
	{
		this.lineWidth = width;
		this.color = color;
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		g.setColor(this.color.getColor());
		int lineWidthInt = this.lineWidth.getValue(height);
		switch (this.position)
		{
		case POSITION_CENTER:
			y += (height - lineWidthInt) / 2;
			break;
		case POSITION_BOTTOM:
			y += height - lineWidthInt;
			break;
		}
		if (lineWidthInt == 1)
		{
			g.drawLine(x, y, x + width, y);
		}
		else
		{
			g.fillRect(x, y, width, lineWidthInt);
		}
	}
	
	
	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-line-color
				Color col = style.getColorProperty("background-line-color");
				if (col != null) {
					this.color = col;
				}
			//#endif
			//#if polish.css.background-line-width
				Dimension dim = (Dimension) style.getObjectProperty("background-line-line-width");
				if (dim != null) {
					this.lineWidth = dim;
				}
			//#endif
		}
	//#endif

}
