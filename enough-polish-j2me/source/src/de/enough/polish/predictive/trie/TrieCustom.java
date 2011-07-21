//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

import javax.microedition.lcdui.Canvas;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.PredictiveAccess;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TrieCustom {
	
	private static int COUNT_SIZE = 1;
	private static int CHAR_SIZE = 2;
	
	byte[] 	bytes = null;
	
	public TrieCustom()
	{
		this.bytes 	= load();
	}
	
	public byte[] load()
	{
		try
		{
			this.bytes  = PredictiveAccess.PROVIDER.getRecord(TrieInstaller.CUSTOM_RECORD);
			
			if(this.bytes == null)
				this.bytes = new byte[0];
			
			return this.bytes;
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void addWord(String word)
	{
		if(word.length() > 0)
		{
			word = word.toLowerCase();
			
			char[] chars	= word.toCharArray();
			byte[] temp 	= null;
			
			byte length = (byte)chars.length;
			
			byte[] buffer = new byte[COUNT_SIZE + (length * CHAR_SIZE)];
			
			buffer[0] = length;
			
			for(int i=0; i<length; i++)
			{
				buffer[(i * CHAR_SIZE) + 1] = (byte) ((chars[i] >> 8) & 0x000000FF);
				buffer[(i * CHAR_SIZE) + 2] = (byte) (chars[i] & 0x00FF);
			}
			
			temp = new byte[this.bytes.length + buffer.length];
			
			System.arraycopy(this.bytes, 0, temp, 0, this.bytes.length);
			System.arraycopy(buffer, 0, temp, this.bytes.length, buffer.length);
			
			this.bytes = new byte[this.bytes.length + buffer.length];
			
			System.arraycopy(temp, 0, this.bytes, 0, temp.length);
			
			save(this.bytes);
		}
	}
	
	public void save(byte[] bytes)
	{
		try
		{
			PredictiveAccess.PROVIDER.setRecord(TrieInstaller.CUSTOM_RECORD, bytes);
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
		}
	}
	
	public void getWords(ArrayList words, int[] keyCodes)
	{
		boolean relevant = true;
		
		if(this.bytes.length > 0)
		{
			for(int i=0; i<this.bytes.length; i+= ((this.bytes[i] * CHAR_SIZE) + COUNT_SIZE))
			{
				char character;
				
				if(keyCodes.length <= this.bytes[i])
				{
					relevant = true;
					
					for(int k=0; k < keyCodes.length; k++)
					{
						character = byteToChar(this.bytes, i + COUNT_SIZE + (k * CHAR_SIZE));
						
						if(!isInCharset(keyCodes[k],character))
						{
							relevant = false;
							break;
						}
					}
					
					if(relevant)
					{
						words.add(getWord(this.bytes, i));
					}
				}
			}
		}
	}
	
	private boolean isInCharset(int keyCode, char character)
	{
		if(keyCode > TrieTextElement.SHIFT)
			keyCode -= TrieTextElement.SHIFT;
		
		switch(keyCode)
		{
			case Canvas.KEY_NUM0 : return (TextField.CHARACTERS[0].indexOf(character) != -1);
			case Canvas.KEY_NUM1 : return (TextField.CHARACTERS[1].indexOf(character) != -1);
			case Canvas.KEY_NUM2 : return (TextField.CHARACTERS[2].indexOf(character) != -1);
			case Canvas.KEY_NUM3 : return (TextField.CHARACTERS[3].indexOf(character) != -1);
			case Canvas.KEY_NUM4 : return (TextField.CHARACTERS[4].indexOf(character) != -1);
			case Canvas.KEY_NUM5 : return (TextField.CHARACTERS[5].indexOf(character) != -1);
			case Canvas.KEY_NUM6 : return (TextField.CHARACTERS[6].indexOf(character) != -1);
			case Canvas.KEY_NUM7 : return (TextField.CHARACTERS[7].indexOf(character) != -1);
			case Canvas.KEY_NUM8 : return (TextField.CHARACTERS[8].indexOf(character) != -1);
			case Canvas.KEY_NUM9 : return (TextField.CHARACTERS[9].indexOf(character) != -1);
		}
		
		return false;
	}
	
	private char byteToChar(byte[] bytes, int offset)
	{
		int high = bytes[offset] & 0xff;
		int low = bytes[offset+1] & 0xff;
		return (char)((int)( high << 8 | low ));
	}
	
	private String getWord(byte[] array, int offset)
	{
		String result = "";
		
		for(int i=0; i < array[offset] * CHAR_SIZE; i = i + CHAR_SIZE)
			result += (byteToChar(array, offset + i + 1));
		
		return result;
	}
}
