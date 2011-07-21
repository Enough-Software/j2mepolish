//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public abstract class TextElement {
	protected Object element = null; 
	
	protected int[] keyCodes = null; 
	
	protected int selectedWordIndex;		
	
	public static final int SHIFT = 10000;
	
	public TextElement(Object object) 
	{
		this.element 		= object;
		
		this.selectedWordIndex	= 0;
		
		this.keyCodes 			= new int[0];
	}

	public int getLength() {
		if (this.element != null) {
			if (this.element instanceof String) {
				return ((String)this.element).length();
			} else if (this.element instanceof PredictiveReader) {
				return this.getSelectedWord().length();
			}
		}

		return -1;
	}
	
	public void keyNum(int keyCode, int shift) 
	{	
		int[] newKeyCodes = new int[this.keyCodes.length + 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length);
		newKeyCodes[newKeyCodes.length - 1] = keyCode;
		this.keyCodes = newKeyCodes;
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.keyCodes[this.keyCodes.length - 1] += SHIFT;
		
		this.selectedWordIndex = 0;
	}
	
	public void keyClear() 
	{
		if(this.keyCodes.length > 0)
		{
			int[] newKeyCodes = new int[this.keyCodes.length - 1];
			System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length - 1);
			this.keyCodes = newKeyCodes;
			
			this.selectedWordIndex = 0;
		}
	}
	
	public boolean isString()
	{
		if(this.element instanceof String)
			return true;
		else
			return false;
	}
		
	public void shiftResults(ArrayList results)
	{
		StringBuffer buffer = null;
		
		for (int i = 0; i < results.size(); i++) {
			
			buffer = new StringBuffer((String)results.get(i));
			
			for (int j = 0; j < this.keyCodes.length; j++)
			{
				if (this.keyCodes[j] > SHIFT && j < buffer.length())
					buffer.setCharAt(j, Character.toUpperCase(buffer.charAt(j)));
			}
			
			results.set(i, buffer.toString());
		}

	}

	public abstract void setResults();
	
	protected abstract String getSelectedString();
	
	public abstract void setSelectedWordIndex(int selected);
		
	public int getSelectedWordIndex()
	{
		return this.selectedWordIndex;
	}

	
	public String getSelectedWord()
	{
		String word = getSelectedString();
		
		if(word == null)
			return "";
		
		if(this.keyCodes.length == word.length())
			return getSelectedString();
		else
		{
			if(word.length() > this.keyCodes.length)
			{
				word = word.substring(0, this.keyCodes.length);
				return word;
			}
			else
			{
				return word;
			}
		}
		
	}
	
	public abstract boolean isSelectedCustom();
	
	public abstract void convertReader();
	
	public abstract ArrayList getResults();
	
	public abstract ArrayList getCustomResults();
		
	public abstract boolean isWordFound();

	public Object getElement() {
		return this.element;
	}

	public void setElement(Object element)
	{
		this.element = element;
	}
	
	public int getKeyCount()
	{
		return this.keyCodes.length;
	}
	
}
