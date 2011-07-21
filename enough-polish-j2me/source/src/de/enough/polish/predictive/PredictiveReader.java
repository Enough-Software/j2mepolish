//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive;

import javax.microedition.lcdui.Canvas;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.TextField;

public abstract class PredictiveReader {
	
	protected StringBuffer letters = null;
	
	protected int selectedWord = 0;
	
	protected boolean empty;
	protected boolean wordFound;
	
	protected int keyCount = 0;
	
	public PredictiveReader()
	{
		this.letters = new StringBuffer(10);
				
		this.empty 		= true;
		this.wordFound	= true;
	}
	
	public abstract void keyNum(int keyCode) throws RecordStoreException;
	
	public abstract void keyClear() throws RecordStoreException;
	
	public abstract String getSelectedWord();
	
	protected void setLetters(int keyCode)
	{
		this.letters.setLength(0);
		switch(keyCode)
		{
			case Canvas.KEY_NUM1: 	this.letters.append(TextField.CHARACTERS[1]); break; 
			case Canvas.KEY_NUM2: 	this.letters.append(TextField.CHARACTERS[2]); break;
			case Canvas.KEY_NUM3: 	this.letters.append(TextField.CHARACTERS[3]); break;
			case Canvas.KEY_NUM4: 	this.letters.append(TextField.CHARACTERS[4]); break;
			case Canvas.KEY_NUM5: 	this.letters.append(TextField.CHARACTERS[5]); break;
			case Canvas.KEY_NUM6: 	this.letters.append(TextField.CHARACTERS[6]); break;
			case Canvas.KEY_NUM7: 	this.letters.append(TextField.CHARACTERS[7]); break;
			case Canvas.KEY_NUM8: 	this.letters.append(TextField.CHARACTERS[8]); break;
			case Canvas.KEY_NUM9: 	this.letters.append(TextField.CHARACTERS[9]); break;
			default: 				this.letters.append(TextField.CHARACTERS[1]); break;
		}
	}
	
	public void setSelectedWord(int selectedWord) {
		this.selectedWord = selectedWord;
	}

	public boolean isEmpty() {
		return this.empty;
	}

	protected void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
	public boolean isWordFound() {
		return this.wordFound;
	}

	protected void setWordFound(boolean wordFound) {
		this.wordFound = wordFound;
	}
	
	public int getKeyCount()
	{
		return this.keyCount;
	}
}
