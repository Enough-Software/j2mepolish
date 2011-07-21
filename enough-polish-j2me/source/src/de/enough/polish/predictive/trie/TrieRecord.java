//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

public class TrieRecord {
	private int id;
	private byte[] record;
	private int references;
	
	public TrieRecord(int id, byte[] record)
	{
		this.id = id;
		this.record = record;
		this.references = 0;
	}
	
	public void addReference()
	{
		this.references++;
	}
	
	public int getReferences()
	{
		return this.references;
	}

	public int getId() {
		return this.id;
	}

	public byte[] getRecord() {
		return this.record;
	}
}
