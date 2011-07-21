package de.enough.polish.sample.browser;

import de.enough.polish.browser.Browser;
import de.enough.polish.browser.TagHandler;
import de.enough.polish.ui.ChartItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.SimplePullParser;

/**
 * <p>Handles each &lt;div&gt; element with a <code>chart</code> class.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Michael Koch, j2mepolish@enough.de
 * @author Robert Virkus
 */
public class ChartTagHandler
extends TagHandler
{
	private static final String CLASS_CHART = "chart";
	private static final String CLASS_CHART_DATA = "chartdata";
	private static final String CLASS_CHART_COLORS = "chartcolors";
	private boolean collectData;
	private ArrayList strDataSequences = new ArrayList();
	private String strColors;
	private Style chartStyle;
	private TagHandler parent;
  
	/**
	 * Creates a new handler for "chart" div tags
	 * @param parent the default handler for div tags to which unprocessed
	 *        tags are forwarded, can be null
	 */
	public ChartTagHandler( TagHandler parent ) {
		this.parent = parent;
	}

	public void register(Browser browser)
	{
		browser.addTagHandler("div", this);
		browser.addTagHandler("span", this);
	}
  
	public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style)
	{
		//System.out.println("tag=" + tagName + ", class=" + attributeMap.get("class") + ", id=" + attributeMap.get("id") + ", style=" + style);
		String elementClass = (String) attributeMap.get("class");
		if (TextUtil.equalsIgnoreCase("div", tagName) 
    		&& 
    		( (!opening && this.collectData) 
    				|| TextUtil.equalsIgnoreCase(CLASS_CHART, elementClass)) 
    		)
		{
			if (opening)
			{
				// collect chart data until tag is being closed...
				this.strDataSequences.clear();
				this.strColors = null;
				this.collectData = true;
				this.chartStyle = style;
			}
			else
			{
				int[][] dataSequences = parseDataSequences();
				int[] colors = parseChartColors();
				
				//#style browserChart?
				ChartItem item = new ChartItem(null, dataSequences, colors);
				if (this.chartStyle != null) {
					item.setStyle(this.chartStyle);
					this.chartStyle = null;
				} 
				//#debug
				System.out.println("adding chartitem" + item);
				parentItem.add(item);
				this.collectData = false;
				this.strDataSequences.clear();
				this.strColors = null;
			}
			return true;
		}
		else if (TextUtil.equalsIgnoreCase("span", tagName) && this.collectData) 
		{
			if (opening && TextUtil.equalsIgnoreCase(CLASS_CHART_DATA, elementClass))
			{
				parser.next();
				String data = parser.getText();
				this.strDataSequences.add(data);
				return true;
			}
			else if (opening && TextUtil.equalsIgnoreCase(CLASS_CHART_COLORS, elementClass))
			{
				parser.next();
				this.strColors = parser.getText();
				return true;
			} else if (!opening) {
				// also handle the closing </span> tags while collecting data:
				return true;
			}
		}
		if (this.parent == null) {
			return false;
		} else {
			//#debug
			System.out.println("forwarding tag " + tagName + ", opening=" + opening + ", collectData=" + this.collectData + ", elementClass=" + elementClass);
			return this.parent.handleTag(parentItem, parser, tagName, opening, attributeMap, style);
		}
	}

	/**
	 * Parses and retrieves the colors for the chart.
	 * 
	 * @return the colors given in the "chartcolors" span element
	 */
	private int[] parseChartColors()
	{
		if (this.strColors == null) {
			return new int[]{ 0xFF0000, 0x00FF00, 0x0000FF };
		}
		String[] colorStrings = TextUtil.splitAndTrim(this.strColors, ',');
		int[] colors = new int[ colorStrings.length ];
		for (int i = 0; i < colors.length; i++)
		{
			String colorString = colorStrings[i];
			int color = 0xff0000;
			try {
				if (colorString.charAt(0) == '#') {
					color = Integer.parseInt(colorString.substring(1), 16);
				} else if (colorString.startsWith("0x")) {
					color = Integer.parseInt(colorString.substring(2), 16);
				} else {
					color = Integer.parseInt(colorString, 16);
				}
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to parse color definition " + colorString + e);
			}
			colors[i] = color;
		}
		return colors;
	}

	/**
	 * Parses and returns the data sequences used for the chart.
	 * 
	 * @return an array of integer arrays with the chart data
	 */
	private int[][] parseDataSequences()
	{
		int num = this.strDataSequences.size();
		int[][] result = new int[num][];
		
		for (int i = 0; i < num; i++)
		{
			String sequence = (String) this.strDataSequences.get(i);
			StringTokenizer st = new StringTokenizer(sequence, ',');
			int[] array = new int[st.countTokens()];
      
			for (int index = 0; st.hasMoreTokens(); index++)
			{
				array[index] = Integer.parseInt(st.nextToken());
			}
			result[i] = array;
		}
		return result;
	}
}
