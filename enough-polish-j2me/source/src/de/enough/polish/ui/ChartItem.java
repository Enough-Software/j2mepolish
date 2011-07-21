//#condition polish.usePolishGui

/*
 * Created on Nov 28, 2006 at 1:34:21 PM.
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
package de.enough.polish.ui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.RgbImage;

/**
 * <p>The ChartItem renders numerical integer based data in a diagram.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Nov 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ChartItem
//#if polish.LibraryBuild
	extends FakeCustomItem 
//#else
	//# extends Item
//#endif
{
	private int[][] dataSequences; // actual numerical data that should be visualized
	private String[] labelsData; // labels for the each numerical data
	private int dataMaximum; // maximum value in the data sequence
	private int dataMinimum; // minimum value in the data sequence
	private String labelY; // vertical label
	private String labelX; // horizontal label
	private int baseLine; // the reference point from which the data should be shown. This can be a handy trick for making growth 
	                      // seem more spectacular, for example - just show the difference between a former year and today instead of 
	                      // showing the absolute growth, for example.
	private int divider; // this can be used to simulate floating point numbers with integer numbers, 
                         // a devider of 100 can make eurocents to euros, for example (or cents to dollars)
	private int[] colors; // the colors of each data sequence
	private int axisColor; // the color of the x and y axis
	
	private int scaleFactorX; // the factor in percent for scaling the diagram horizontally, is usually calculated dynamically using the available width
	private int scaleFactorY; // the factor in percent for scaling the diagram vertically, is usually calculated dynamically using the max-height of the item
	private Font font;
	private int fontColor;
	//#if polish.midp2 && polish.hasFloatingPoint
		//#define tmp.rotate
		private RgbImage rotatedLabelY;
	//#endif

    /**
     * Creates a new ChartItem.
     * 
     * @param label the label
     * @param dataSequences the actual data that should be visualized
     * @param colors the colors of each data sequence, can be overridden by the designer for most ChartItemViews
     */
	public ChartItem(String label, int[][] dataSequences, int[] colors ) {
		this( label, dataSequences, colors, null, null, null, 0, 0, null );
	}

    /**
     * Creates a new ChartItem.
     * 
     * @param label the label
     * @param dataSequences the actual data that should be visualized
     * @param colors the colors of each data sequence, can be overridden by the designer for most ChartItemViews
     * @param style the J2ME Polish style of this item
     */
	public ChartItem(String label, int[][] dataSequences, int[] colors, Style style ) {
		this( label, dataSequences, colors, null, null, null, 0, 0, style );
	}
	
    /**
     * Creates a new ChartItem.
     * 
     * @param label the label
     * @param dataSequences the actual data that should be visualized
     * @param colors the colors of each data sequence, can be overridden by the designer for most ChartItemViews
     * @param labelsData optional labels for the legend of this item
     * @param labelY the optional label for the vertical axis (e.g. revenue)
     * @param labelX the optional label for the horizontal axis (e.g. years)
     * @param baseLine the reference point from which the data should be shown. This can be a handy trick for making growth 
     *                 seem more spectacular, for example - just show the difference between a former year and today instead of 
     *                 showing the absolute growth, for example. Set to 0 for playing it clean.
     * @param divider can be used to simulate floating point numbers with integer numbers, a devider of 100 can make eurocents to euros, for example (or cents to dollars).
     *                A divider of 1.000.000 could show revenue in million pounds, for example.
     */
	public ChartItem(String label, int[][] dataSequences, int[] colors, String[] labelsData, String labelY, String labelX, int baseLine, int divider ) {
		this( label, dataSequences, colors, labelsData, labelY, labelX, baseLine, divider, null );
	}

    /**
     * Creates a new ChartItem.
     * 
     * @param label the label
     * @param dataSequences the actual data that should be visualized
     * @param colors the colors of each data sequence, can be overridden by the designer for most ChartItemViews
     * @param labelsData optional labels for the legend of this item
     * @param labelY the optional label for the vertical axis (e.g. revenue)
     * @param labelX the optional label for the horizontal axis (e.g. years)
     * @param baseLine the reference point from which the data should be shown. This can be a handy trick for making growth 
     *                 seem more spectacular, for example - just show the difference between a former year and today instead of 
     *                 showing the absolute growth, for example. Set to 0 for playing it clean.
     * @param divider can be used to simulate floating point numbers with integer numbers, a devider of 100 can make eurocents to euros, for example (or cents to dollars).
     *                A divider of 1.000.000 could show revenue in million pounds, for example.
     * @param style the J2ME Polish style of this item
     */
	public ChartItem(String label, int[][] dataSequences, int[] colors, String[] labelsData, String labelY, String labelX, int baseLine, int divider, Style style ) {
		super( label, LAYOUT_DEFAULT, PLAIN,  style );
		this.dataSequences = dataSequences;
		this.labelsData = labelsData;
		this.labelY = labelY;
		this.labelX = labelX;
		this.baseLine = baseLine;
		this.divider = divider;
		this.colors = colors;
		if (dataSequences != null) {
			setDataSequences( dataSequences );
		}
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#createCssSelector()
	 */
	protected String createCssSelector() {
		return "chart";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		if ( this.dataSequences == null || this.dataSequences.length == 0 || this.dataSequences[0].length == 0 ) {
			this.contentHeight = 0;
			this.contentWidth = 0;
			return;
		}
		if (this.font == null && (this.labelX != null || this.labelY != null)) {
			this.font = Font.getDefaultFont();
		}
		
		// horizontal / x axis initialization:
		int labelWidth = 0;
		if (this.labelX != null) {
			labelWidth = this.font.getHeight() + this.paddingVertical;
			availWidth -= labelWidth;
		}
		int length = this.dataSequences[0].length - 1;
		boolean isHorizontalShrink = (this.layout & LAYOUT_SHRINK) == LAYOUT_SHRINK;
		//#ifdef polish.css.max-width
			if (this.maximumWidth != null && this.maximumWidth.getValue(availWidth) < availWidth ) {
				availWidth = this.maximumWidth.getValue(availWidth);
			}
		//#endif
		if (isHorizontalShrink && length <= availWidth) {
			this.contentWidth = availWidth + labelWidth;
			this.scaleFactorX = 100;
		} else {
			this.scaleFactorX = (availWidth * 100) / length;
			this.contentWidth = (this.scaleFactorX * length) / 100 + labelWidth;
		}
		
		// vertical / y axis initialization:
		int maxHeight = availHeight;
		//#ifdef polish.css.max-height
			if (this.maximumHeight != null) {
				maxHeight = this.maximumHeight.getValue(availWidth);
			}
		//#endif
		int dataRange;
		if (this.dataMaximum > 0 ) {
			if (this.dataMinimum > 0) {
				// there are only positive values:
				dataRange = this.dataMaximum - this.baseLine;
			} else {
				// there are positive and negative values:
				dataRange = Math.abs( this.dataMaximum - this.dataMinimum ); 
			}
		} else {
			// there are only negative values:
			dataRange = Math.abs( this.baseLine - this.dataMinimum );
		}
		boolean isVerticalExpand = (this.layout & LAYOUT_VEXPAND) == LAYOUT_VEXPAND;
		int labelHeight = 0;
		if (this.labelX != null) {
			labelHeight = this.font.getHeight() + this.paddingVertical;
			maxHeight -= labelHeight;
		}
		if (!isVerticalExpand && dataRange <= maxHeight) {
			this.scaleFactorY = 100;
			this.contentHeight = dataRange + labelHeight;
		} else {
			this.scaleFactorY = (maxHeight * 100) / dataRange; 
			this.contentHeight = maxHeight + labelHeight;
		}
		
		//#if tmp.rotate
			if (this.labelY != null && this.rotatedLabelY == null) {
				// rotate label by 90 degrees:
				int[] original = TextEffect.getRgbData(this.labelY, this.fontColor, this.font);
				int height = this.font.getHeight();
				int width = original.length / height;
				int[] rotated = new int[ original.length ];
				for (int col = 0; col < height; col++) {
					for (int row = 0; row < width; row++ ) {
						rotated[ row*height + (height - (col + 1))] = 
							original[ col*width + row];
					}
				}
				this.rotatedLabelY = new RgbImage( rotated, height, true); // RotateTextEffect.rotate(this.labelY, this.fontColor, this.font, 270 ); 
			}
		//#endif

	}
	
	/**
	 * Paints the labels and the axis of this chart.
	 * 
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param inout_params an array with 2 elements for adjusting x and y:
	 * <pre>
	 * int[] inout_params = new int[]{ x, y };
	 * int baseLineY = chart.paintGrid(x, y, leftBorder, rightBorder, inout_params, g); 
	 * x = inout_params[0];
	 * y = inout_params[1];
	 * </pre>
	 * @param g the Graphics on which this item should be painted.
	 * @return the y position of the baseline
	 */
	public int paintGrid(int x, int y, int leftBorder, int rightBorder, int[] inout_params, Graphics g) {
		if (this.dataSequences == null || this.dataSequences.length == 0) {
			return y;
		}
		int dataLength = this.dataSequences[0].length - 1;
		int xAxisWidth = (dataLength*this.scaleFactorX)/100;
		int yAxisHeight = (Math.abs( this.dataMaximum - this.dataMinimum )  * this.scaleFactorY) /100;
			
		// draw axis labels:
		g.setColor( this.fontColor );
		g.setFont( this.font );
		if (this.labelX != null) {
			if (this.dataMaximum < 0) {
				//#if tmp.rotate
					g.drawString( this.labelX, x + xAxisWidth/2, y, Graphics.HCENTER | Graphics.TOP  );
				//#else
					g.drawString( this.labelX, x + xAxisWidth, y, Graphics.RIGHT | Graphics.TOP  );
				//#endif
				y += this.font.getHeight() + this.paddingVertical;
				inout_params[1] = y;
			} else {
				//#if tmp.rotate
					g.drawString( this.labelX, x + xAxisWidth/2, y + this.contentHeight, Graphics.HCENTER | Graphics.BOTTOM  );
				//#else
					g.drawString( this.labelX, x + xAxisWidth, y + this.contentHeight, Graphics.RIGHT | Graphics.TOP  );
			//#endif
			}
		}
		if (this.labelY != null) {
			//#if tmp.rotate
				this.rotatedLabelY.paint(x, y + (yAxisHeight - this.rotatedLabelY.getHeight())/2, g);
				x += this.font.getHeight() + this.paddingHorizontal;
			//#else
			//#endif
		}
		
		// calculate baseline:
		int baseLineY;
		if (this.dataMaximum > 0) {
			baseLineY = y + ((this.dataMaximum - this.baseLine) * this.scaleFactorY) / 100;
		} else {
			baseLineY = y + (this.baseLine * this.scaleFactorY) / 100;
		}

		// draw axis:
		g.setColor( this.axisColor );
		g.drawLine( x, baseLineY, x + xAxisWidth,  baseLineY );
		g.drawLine( x, y, x, y +  yAxisHeight );
		
		x++;
		inout_params[0] = x;
		
		return baseLineY;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO robertvirkus cache diagram in an image at least on nokia-ui-api and MIDP 2.0+ devices
		int[][] sequences = this.dataSequences;
		if (sequences == null) {
			return;
		}
		
		int[] inout_params = new int[]{ x, y };
		int baseLineY = paintGrid(x, y, leftBorder, rightBorder, inout_params, g); 
		x = inout_params[0];
		y = inout_params[1];

		// draw data:
		for (int i = 0; i < sequences.length; i++) {
			g.setColor( this.colors[i] );
			int[] dataRow = sequences[i];
			if (dataRow == null || dataRow.length == 0) {
				continue;
			}
			int lastDatumX = x;
			int lastDatumY = baseLineY - (dataRow[0] * this.scaleFactorY) / 100; 
			for (int j = 1; j < dataRow.length; j++) {
				int datum = dataRow[j];
				int datumX = x + (j * this.scaleFactorX) / 100;
				int datumY = baseLineY - (datum * this.scaleFactorY) / 100;
				g.drawLine(  lastDatumX, lastDatumY, datumX, datumY);
				lastDatumX = datumX;
				lastDatumY  = datumY;
			}				
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		this.fontColor = style.getFontColor();
		this.font = style.getFont();
		if (this.font == null) {
			this.font = Font.getDefaultFont();
		}
	} 

	/**
	 * @return the baseLine
	 */
	public int getBaseLine() {
		return this.baseLine;
	}

	/**
	 * @param baseLine the baseLine to set
	 */
	public void setBaseLine(int baseLine) {
		this.baseLine = baseLine;
	}

	/**
	 * @return the dataMaximum
	 */
	public int getDataMaximum() {
		return this.dataMaximum;
	}

	/**
	 * @param dataMaximum the dataMaximum to set
	 */
	public void setDataMaximum(int dataMaximum) {
		this.dataMaximum = dataMaximum;
	}

	/**
	 * @return the dataMinimum
	 */
	public int getDataMinimum() {
		return this.dataMinimum;
	}

	/**
	 * @param dataMinimum the dataMinimum to set
	 */
	public void setDataMinimum(int dataMinimum) {
		this.dataMinimum = dataMinimum;
	}

	/**
	 * @return the dataSequences
	 */
	public int[][] getDataSequences() {
		return this.dataSequences;
	}

	/**
	 * Sets the data that should be visualized.
	 * Also the minimum and maximum of the data is calculated here.
	 * 
	 * @param dataSequences the dataSequences to set
	 */
	public void setDataSequences(int[][] dataSequences) {
		this.dataSequences = dataSequences;
		if (dataSequences == null || (dataSequences.length == 0) ) {
			this.dataMaximum = this.dataMinimum = 0;
		} else {
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			for (int i = 0; i < dataSequences.length; i++) {
				int[] dataRow = dataSequences[i];
				for (int j = 0; j < dataRow.length; j++) {
					int datum = dataRow[j];
					if (datum < min) {
						min = datum;
					}
					if (datum > max) {
						max = datum;
					}
				}				
			}
			this.dataMaximum = max;
			this.dataMinimum = min;
			if (this.colors == null) {
				this.colors = new int[ this.dataSequences.length ];
			}
		}
	}

	/**
	 * @return the divider
	 */
	public int getDivider() {
		return this.divider;
	}

	/**
	 * @param divider the divider to set
	 */
	public void setDivider(int divider) {
		this.divider = divider;
	}

	/**
	 * @return the labelsData
	 */
	public String[] getLabelsData() {
		return this.labelsData;
	}

	/**
	 * @param labelsData the labelsData to set
	 */
	public void setLabelsData(String[] labelsData) {
		this.labelsData = labelsData;
	}

	/**
	 * @return the labelX
	 */
	public String getLabelX() {
		return this.labelX;
	}

	/**
	 * @param labelX the labelX to set
	 */
	public void setLabelX(String labelX) {
		this.labelX = labelX;
	}

	/**
	 * @return the labelY
	 */
	public String getLabelY() {
		return this.labelY;
	}

	/**
	 * @param labelY the labelY to set
	 */
	public void setLabelY(String labelY) {
		//#if tmp.rotate
			this.rotatedLabelY = null; 
			this.isInitialized = false;
		//#endif
		this.labelY = labelY;
	}

	/**
	 * @return the axisColor
	 */
	public int getAxisColor() {
		return this.axisColor;
	}

	/**
	 * @param axisColor the axisColor to set
	 */
	public void setAxisColor(int axisColor) {
		this.axisColor = axisColor;
	}

	/**
	 * @return the colors
	 */
	public int[] getColors() {
		return this.colors;
	}

	/**
	 * @param colors the colors to set
	 */
	public void setColors(int[] colors) {
		this.colors = colors;
	}

	/**
	 * Retrieves the scale factor used by the default implementation.
	 * This method is usually only interesting for specifc chart ItemViews.
	 *  
	 * @return the vertical scale factor
	 */
	public int getScaleFactorY() {
		return this.scaleFactorY;
	}
	
	/**
	 * Retrieves the scale factor used by the default implementation.
	 * This method is usually only interesting for specifc chart ItemViews.
	 *  
	 * @return the honrizontal scale factor
	 */
	public int getScaleFactorX() {
		return this.scaleFactorX;
	}


}
