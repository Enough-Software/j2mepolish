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
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;

/**
 * <p>Visualizes the chart as a pie chart.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jul 19, 2007 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class PieChartView extends ItemView {
		
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		initContentByParent(parent, firstLineWidth, availWidth, availHeight);
		
		this.contentHeight = this.contentWidth;
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
		
		int[] colors = chart.getColors();
		int colorIndex = 0;
		
		for (int i = 0; i < sequences.length; i++) {
			int[] dataRow = sequences[i];
			if (dataRow == null || dataRow.length == 0) {
				continue;
			}
			
			int radius = this.contentWidth - ((this.contentWidth / sequences.length) * i);
			int top = y + (this.contentWidth - radius) / 2;
			int left = x + (this.contentWidth - radius) / 2; 
			
			//Calculate total amount
			int total = 0;
			for (int j = 0; j < dataRow.length; j++) {
				int value = dataRow[j];
				
				//No negative numbers allowed
				if(value < 0)
					value = value * -1;
				
				total += value;
			}
			
			//Draw arcs
			int offset = 0;
			int degrees = 0;
			for (int j = 0; j < dataRow.length; j++) {
				int color = colors[colorIndex];
				g.setColor( color );
				
				int value = dataRow[j];
				
				//No negative numbers allowed
				if(value < 0)
					value = value * -1;
				
				colorIndex = (colorIndex+1) % colors.length;
				
				degrees = (((value * 100) /total) * 360) / 100;
				
				if(j == (dataRow.length -1))
					g.fillArc(left, top, radius, radius, offset, 360 - offset);
				else	
					g.fillArc(left, top, radius, radius, offset, degrees);
				
				offset += degrees;	
			}
		}

	}
	

}
