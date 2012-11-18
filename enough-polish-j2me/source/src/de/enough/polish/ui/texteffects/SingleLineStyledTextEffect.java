//#condition polish.usePolishGui
package de.enough.polish.ui.texteffects;

import java.util.Stack;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.WrappedText;

/**
 * A fast text effect optimized for a single line of text with following styles supported:
 * <ul>
 * 	<li>&lt;b&gt; for bold text: <code>&lt;b&gt;bold text&lt;/b&gt;</li>
 * 	<li>&lt;i&gt; for italic text: <code>&lt;i&gt;italic text&lt;/i&gt;</li>
 * 	<li>&lt;color=&quot;#fff&quot;&gt; for colored text: <code>&lt;color=&quot;#777&quot;&gt;light gray text&lt;/color&gt;</code></li>
 * 	<li>You can also combine styles: <code>&lt;b&gt;&lt;i&gt;&lt;color=&quot;#777&quot;&gt;bold italic light gray text&lt;/color&gt;&lt;/i&gt;&lt;/b&gt;</code></li>
 * </ul>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SingleLineStyledTextEffect extends TextEffect
{
	private final ArrayList styledTextsList = new ArrayList();
	private int completeWidth;
	
	public SingleLineStyledTextEffect()
	{
		this.isTextSensitive = true;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String text, int textColor, Font font,
			int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedText)
	{
		this.styledTextsList.clear();
		int width = 0;
		int lastStartIndex = 0;
		Stack previousStylesStack = new Stack(); 
		int textLength = text.length();
		for (int charIndex=0; charIndex<textLength; charIndex++)
		{
			char c = text.charAt(charIndex);
			if (c == '<')
			{
				if (charIndex > lastStartIndex)
				{
					// add found text:
					String subText = text.substring(lastStartIndex, charIndex);
					int subWidth = font.stringWidth(subText);
					StyledText styledText = new StyledText(subText, font, textColor, subWidth);
					styledTextsList.add(styledText);
					width += subWidth;
				}
				if (charIndex >= textLength - 2)
				{
					lastStartIndex = textLength;
					break;
				}
				charIndex++;
				c = text.charAt(charIndex);
				if (c == '/')
				{
					// the style or color definition is stopped here:
					Object o = previousStylesStack.pop();
					if (o instanceof Font)
					{
						font = (Font) o;
					}
					else
					{
						textColor = ((Integer)o).intValue();
					}
				}
				else
				{
					// a new style starts here:
					if (c == 'b')
					{
						previousStylesStack.push(font);
						font = Font.getFont(font.getFace(), font.getStyle() | Font.STYLE_BOLD, font.getSize() );
					}
					else if (c == 'i')
					{
						previousStylesStack.push(font);
						font = Font.getFont(font.getFace(), font.getStyle() | Font.STYLE_ITALIC, font.getSize() );
					}
					else if (c == 'c') // -> color
					{
						previousStylesStack.push( new Integer( textColor ) );
						charIndex += "color=\"".length();
						int colorDefinitionStartIndex = charIndex;
						while (c != '"' && charIndex < textLength-1)
						{
							charIndex++;
							c = text.charAt(charIndex);
						}
						String colorDefinition = text.substring(colorDefinitionStartIndex, charIndex);
						try 
						{
							Color color = CssInterpreter.parseColor(colorDefinition);
							textColor = color.getColor();
						} 
						catch (Exception e)
						{
							System.out.println("Unable to parse color " + colorDefinition + e);
						}
					}
				}
				// go to the end of the style or color definition:
				while (c != '>' && charIndex < textLength-1)
				{
					charIndex++;
					c = text.charAt(charIndex);
				}
				lastStartIndex = charIndex + 1;
			}
		}
		
		// add tail:
		if (lastStartIndex < textLength-1)
		{
			// add found text:
			String subText = text.substring(lastStartIndex);
			int subWidth = font.stringWidth(subText);
			StyledText styledText = new StyledText(subText, font, textColor, subWidth);
			styledTextsList.add(styledText);
			width += subWidth;
		}

		wrappedText.addLine(text, width);
		this.completeWidth = width;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(de.enough.polish.util.WrappedText, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(WrappedText textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g)
	{
		int anchor;
		//#if polish.Bugs.needsBottomOrientiationForStringDrawing
			anchor = Graphics.BOTTOM | Graphics.LEFT;
		//#else
			anchor = Graphics.TOP | Graphics.LEFT;
		//#endif
		Object[] styledTexts = this.styledTextsList.getInternalArray();
		if (( ( layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ))
		{
			x = leftBorder + ((rightBorder - leftBorder) - this.completeWidth)/2;
		}
		else if ( ( layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT )
		{
			x = leftBorder + ((rightBorder - leftBorder) - this.completeWidth);
		}
		for (int i = 0; i < styledTexts.length; i++)
		{
			StyledText text = (StyledText) styledTexts[i];
			if (text == null)
			{
				break;
			}
			g.setFont(text.font);
			g.setColor(text.color);
			g.drawString(text.text, x, y, anchor);
			x += text.width; 
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y, int anchor, Graphics g)
	{
		// not needed as we overwrite drawStrings
	}

	
	
	private static class StyledText
	{
		String text;
		Font font;
		int color;
		int width;
		public StyledText(String text, Font font, int textColor, int width)
		{
			this.text = text;
			this.font = font;
			this.color = textColor;
			this.width = width;
		}
		
	}

}
