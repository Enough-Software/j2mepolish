//#condition polish.usePolishGui
/*
 * Created on Jan 30, 2007 at 7:07:55 AM.
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
 */
package de.enough.polish.ui.chartviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ChartItem;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;

/**
 * <p>Visualizes the chart with vertical bars.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jan 30, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalBarChartView extends ItemView {

	//#if polish.css.chart-vertical-bars-shadow-color
		private int shadowColor = -1;
	//#endif	
		
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		initContentByParent(parent, firstLineWidth, availWidth, availHeight);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		//#if polish.LibraryBuild
			ChartItem chart = (ChartItem) ((Object)parent);
		//#else
			//# ChartItem chart = (ChartItem) parent;
		//#endif
		int[][] sequences = chart.getDataSequences();
		if (sequences == null) {
			return;
		}
		int[] inout_params = new int[]{ x, y };
		int baseLineY = chart.paintGrid(x, y, leftBorder, rightBorder, inout_params, g); 
		x = inout_params[0];
		y = inout_params[1];
		int[] colors = chart.getColors();
		int scaleFactorY = chart.getScaleFactorY();
		
		//#if polish.css.chart-vertical-bars-shadow-color
			boolean drawShadow = (this.shadowColor != -1 );
		//#endif

		
		// draw data:
		int length = sequences[0].length;
		//#if polish.css.chart-vertical-bars-shadow-color
			if (drawShadow) {
				length <<= 1;
			}
		//#endif
		int leftSpace = rightBorder - (x + length);
		int barWidth = Math.max( 2, leftSpace /  sequences[0].length );
		int barSpace = 1;
		for (int i = 0; i < sequences.length; i++) {
			int color = colors[i];
			g.setColor( color );
			int[] dataRow = sequences[i];
			if (dataRow == null || dataRow.length == 0) {
				continue;
			}
			int datumX = x;
			for (int j = 0; j < dataRow.length; j++) {
				int datum = dataRow[j];
				int datumY = baseLineY - (datum * scaleFactorY) / 100;
				int top, bottom;
				if (baseLineY > datumY) {
					top = datumY;
					bottom = baseLineY;
				} else {
					top = baseLineY;
					bottom = datumY;
				}
				
				g.fillRect( datumX, top, barWidth, bottom - top );
				//#if polish.css.chart-vertical-bars-shadow-color
					if (drawShadow ) {
						g.setColor( this.shadowColor );
						g.drawLine( datumX + 1, top - 1, datumX + barWidth, top - 1 );
						g.drawLine( datumX + barWidth, top, datumX + barWidth, bottom - 2);
						g.setColor( color );
						datumX += 1;
					}
				//#endif
				datumX += barWidth + barSpace;
			}				
		}

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.chart-vertical-bars-shadow-color
			Color shadowColorInt = style.getColorProperty("chart-vertical-bars-shadow-color");
			if (shadowColorInt != null) {
				this.shadowColor = shadowColorInt.getColor();
			}
		//#endif
	}
	
	

}
