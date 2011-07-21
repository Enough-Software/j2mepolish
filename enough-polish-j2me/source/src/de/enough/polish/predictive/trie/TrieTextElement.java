//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

import de.enough.polish.predictive.TextElement;
import de.enough.polish.ui.PredictiveAccess;
import de.enough.polish.util.ArrayList;

public class TrieTextElement extends TextElement {
	ArrayList trieResults = null;
	ArrayList customResults = null;
	
	public TrieTextElement(Object object) {
		super(object);
		
		if(this.element instanceof TrieReader)
		{
			this.trieResults		= new ArrayList();
			this.customResults		= new ArrayList();
		}
	}
	
	public void setResults() {
		
		if (this.element instanceof TrieReader) 
		{
				this.customResults.clear();
				
				PredictiveAccess.PROVIDER.getCustom().getWords(this.customResults,this.keyCodes);
				
				shiftResults(this.customResults);
				
				this.trieResults.clear();
				
				if(((TrieReader)this.element).isWordFound() || 
					!this.isCustomFound() ||
					this.getKeyCount() == ((TrieReader)this.element).getKeyCount())
				{
					ArrayList nodes = ((TrieReader) this.element).getNodes();
					
					for(int i=0; i<nodes.size();i++)
					{
						TrieNode node = (TrieNode)nodes.get(i);
						
						this.trieResults.add(node.getWord().toString());
					}
					
					PredictiveAccess.PROVIDER.getOrder().getOrder(this.trieResults, this.keyCodes);
						
					shiftResults(this.trieResults);
				}
		}
	}
	
	protected String getSelectedString()
	{
		int totalElements = this.customResults.size() + this.trieResults.size();
		if(totalElements <= this.selectedWordIndex)
			this.selectedWordIndex = 0;
		
		if(this.isSelectedCustom())
			return (String)this.customResults.get(this.selectedWordIndex - this.trieResults.size());
		else
			return (String)this.trieResults.get(this.selectedWordIndex);
	}
	
	public void setSelectedWordIndex(int selected)
	{
		this.selectedWordIndex = selected;
		
		if(selected > 0)
			PredictiveAccess.PROVIDER.getOrder().addOrder(this.keyCodes, (byte)selected);
	}
	
	public int getSelectedWordIndex()
	{
		return this.selectedWordIndex;
	}
	
	public boolean isSelectedCustom()
	{
		if(this.trieResults.size() > 0)
			return (this.selectedWordIndex >= this.trieResults.size());
		else
			return (this.customResults.size() > 0);
	}
	
	public void convertReader()
	{
		PredictiveAccess.PROVIDER.releaseRecords();
		this.element = getSelectedString();
	}
	
	public ArrayList getResults()
	{
		return this.trieResults;
	}
	
	public ArrayList getCustomResults()
	{
		return this.customResults;
	}
	
	public boolean isCustomFound()
	{
		return this.customResults.size() > 0;
	}
	
	public boolean isWordFound()
	{
		if(!isString())
			return ((TrieReader)this.element).isWordFound() || isCustomFound();
		
		return false;
	}

	public Object getElement() {
		return this.element;
	}
	
	public int getKeyCount()
	{
		return this.keyCodes.length;
	}
}
