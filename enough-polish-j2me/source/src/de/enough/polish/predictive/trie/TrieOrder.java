//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.PredictiveAccess;
import de.enough.polish.util.ArrayList;

public class TrieOrder {
	
	private static int COUNT_SIZE = 1;
	private static int KEY_SIZE = 1;
	private static int INDEX_SIZE = 1;
	
	byte[] 	bytes = null;
	
	public TrieOrder()
	{
		this.bytes = load();
	}
	
	public byte[] load()
	{
		try
		{
			this.bytes = PredictiveAccess.PROVIDER.getRecord(TrieInstaller.ORDER_RECORD);
			
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
	
	public void addOrder(int[] keyCodes, byte index)
	{
		byte[] buffer 	= new byte[COUNT_SIZE + (keyCodes.length * KEY_SIZE)  + INDEX_SIZE];
		byte[] temp 	= null;
		
		buffer[0] = (byte)keyCodes.length;
		
		for(int i=0; i<keyCodes.length; i++ )
		{
			buffer[i + COUNT_SIZE] = (byte)(keyCodes[i] % TrieTextElement.SHIFT);
		}
		
		buffer[buffer.length - 1] = index;
		
		temp = new byte[this.bytes.length + buffer.length];
		
		System.arraycopy(this.bytes, 0, temp, 0, this.bytes.length);
		System.arraycopy(buffer, 0, temp, this.bytes.length, buffer.length);
		
		this.bytes = new byte[this.bytes.length + buffer.length];
		
		System.arraycopy(temp, 0, this.bytes, 0, temp.length);
		
		save(this.bytes);
	}
	
	public void save(byte[] bytes)
	{
		try
		{
			PredictiveAccess.PROVIDER.setRecord(TrieInstaller.ORDER_RECORD, bytes);
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
		}
	}
	
	public void getOrder(ArrayList words, int[] keyCodes)
	{
		if(this.bytes.length > 0)
		{
			for(int i=0; i<this.bytes.length; i+= COUNT_SIZE + (this.bytes[i] * KEY_SIZE) + INDEX_SIZE)
			{
				if(matchesKeyCode(keyCodes, i))
				{	
					byte length = this.bytes[i];
					byte index = TrieUtils.byteToByte(this.bytes, i + COUNT_SIZE + (length * KEY_SIZE));
					
					if(index > 0 && index < words.size())
					{
						String word = (String)words.get(index);
						words.remove(index);
						words.add(0, word);
					}
				}
			}
		}
	}
	
	public boolean matchesKeyCode(int[] keyCodes,int offset)
	{
		if(this.bytes[offset] != keyCodes.length)
		{
			return false;
		}
		else
		{
			int start 	= offset + 1;
			int end 	= (this.bytes[offset] * KEY_SIZE) + start;
			
			for(int i=start; i < end;i += KEY_SIZE)
			{
				byte keyCode = this.bytes[i];
				byte storedKeyCode = (byte)(keyCodes[(i - offset - 1) / KEY_SIZE]);
				
				if(keyCode != storedKeyCode)
				{
					return false;
				}
			}
						
			return true;
		}
		
	}
}
