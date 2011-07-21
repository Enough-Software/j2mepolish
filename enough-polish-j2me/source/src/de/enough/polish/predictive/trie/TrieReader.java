//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.predictive.PredictiveReader;
import de.enough.polish.ui.PredictiveAccess;
import de.enough.polish.util.ArrayList;

public class TrieReader extends PredictiveReader {
	static final byte V_OFFSET = 0;
	static final byte CC_OFFSET = 2;
	static final byte CR_OFFSET = 3;
	static final byte NODE_SIZE = 5;
	static final byte COUNT_SIZE = 1;
	
	private ArrayList 	nodes = null;
	private ArrayList 	newNodes = null;
	private Stack 		prevNodes = null;
	
	byte[] record = null;
	
	public TrieReader() throws RecordStoreException
	{
		super();
		
		this.nodes 		= new ArrayList();
		this.newNodes 	= new ArrayList();
		this.prevNodes 	= new Stack();
	}
		
	public void keyNum(int keyCode) throws RecordStoreException
	{	
		int partOffset = 0;
		
		pushNodes();
		
		if(this.nodes.size() == 0)
		{
			this.record = this.getRecord(1);
			partOffset = this.getPartOffset(this.record, getPartID(1));
			this.readNodes(this.record, partOffset, keyCode, null);
			
			setEmpty(this.newNodes.size() == 0);
		}
		else
		{
			for(int nodeIndex = 0; nodeIndex < this.nodes.size(); nodeIndex++)
			{
				TrieNode node = (TrieNode)this.nodes.get(nodeIndex);
				
				if(node.getReference() != 0)
				{
					this.record = this.getRecord(node.getReference());
					partOffset = this.getPartOffset(this.record, getPartID(node.getReference()));
					readNodes(this.record, partOffset, keyCode, node.getWord());
				}
			}
		}
		
		copyNodes(this.newNodes,this.nodes);
		this.newNodes.clear();
		
		setWordFound(this.nodes.size() > 0);
		
		if(!isWordFound())
			popNodes(true);
	}
	
	public void keyClear() throws RecordStoreException
	{	
		popNodes(false);
	}
	
	public ArrayList getNodes()
	{
		return this.nodes;
	}
	
	private void copyNodes(ArrayList source, ArrayList dest)
	{
		dest.clear();
		for(int i=0; i < source.size(); i++)
			dest.add(i, source.get(i));
	}
	
	private void pushNodes()
	{
		ArrayList newNodes = new ArrayList();
		
		copyNodes(this.nodes,newNodes);
		
		this.keyCount++;
		
		this.prevNodes.push(newNodes);
	}
	
	private void popNodes(boolean undo)
	{
		if(!isEmpty())
		{
			try
			{
				this.nodes = (ArrayList)this.prevNodes.pop();
				this.keyCount--;
			}
			catch(EmptyStackException e)
			{
				this.nodes = new ArrayList();
			}
			
			if(!undo)
			{
				setEmpty(this.nodes.size() == 0);
				setWordFound(this.nodes.size() > 0);
			}
		}
	}
	
	private int getRecordID(int id)
	{
		return ((id -1) / PredictiveAccess.PROVIDER.getLineCount()) + 1;
	}
	
	private int getPartID(int id)
	{
		return ((id -1) % PredictiveAccess.PROVIDER.getLineCount()) + 1;
	}
		
	private byte[] getRecord(int id) throws RecordStoreException
	{
		int recordID 	= getRecordID(id) + TrieInstaller.OVERHEAD % PredictiveAccess.PROVIDER.getChunkSize();
		
		return PredictiveAccess.PROVIDER.getRecord(recordID);
	}
	
	private int getPartOffset(byte[] record, int partID)
	{
		byte partCount 	= 0;
		int partOffset 	= 0;
		
		for(int i=0; i<partID; i++)
		{
			partCount = TrieUtils.byteToByte(record, partOffset);
			
			if(i == (partID - 1))
				return partOffset;
			else
				partOffset += (partCount * NODE_SIZE) + COUNT_SIZE;				
		}
		
		return 0;
	}
	
	private void readNodes(byte[] record, int partOffset, int keyCode, StringBuffer word)
	{
		char value 		= ' ';
		
		byte partCount = TrieUtils.byteToByte(record,partOffset);
		
		setLetters(keyCode);
		
		for(int i= (partOffset + COUNT_SIZE); i < (partOffset + COUNT_SIZE) + (partCount * NODE_SIZE); i = i + NODE_SIZE)
		{
			value = TrieUtils.byteToChar(record, i+V_OFFSET);
			
			for(int j=0;j< this.letters.length();j++)
			{
				if(value == this.letters.charAt(j))
				{
					TrieNode node = new TrieNode();
					
					node.appendToWord(word);
					node.appendToWord(value);
					
					if(TrieUtils.byteToByte(record, i+CC_OFFSET) != 0)
						node.setReference(TrieUtils.byteToChar(record, i+CR_OFFSET)); 
					else
						node.setReference((char)0);
					
					this.newNodes.add(node);
				}
			}
		}
	}
	
	public String getSelectedWord()
	{
		if(this.nodes.size() > 0)
			if(this.nodes.get(this.selectedWord) instanceof TrieNode)
			{
				TrieNode node = ((TrieNode)this.nodes.get(this.selectedWord));
				return node.getWord().toString();
			}
			else
				return null;
		else
			return null;
	}


}
