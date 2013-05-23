//#condition polish.usePolishGui
package de.enough.polish.ui;

public class ItemChangedEvent
{
	public static final int CHANGE_ADD = 1;
	public static final int CHANGE_REMOVE = 2;
	public static final int CHANGE_SET = 3;
	public static final int CHANGE_COMPLETE_REFRESH = 10;
	
	private final int	change;
	private final Item	affectedItem;
	private int	itemIndex;
	
	public ItemChangedEvent(int change, int itemIndex, Item affectedItem)
	{
		this.change = change;
		this.itemIndex = itemIndex;
		this.affectedItem = affectedItem;
	}

	/**
	 * @return the change
	 */
	public int getChange()
	{
		return this.change;
	}
	
	public int getItemIndex()
	{
		return this.itemIndex;
	}

	/**
	 * @return the affectedItem
	 */
	public Item getAffectedItem()
	{
		return this.affectedItem;
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		switch (this.change)
		{
		case CHANGE_ADD:
			buffer.append("add: ");
			break;
		case CHANGE_REMOVE:
			buffer.append("remove: ");
			break;
		case CHANGE_SET:
			buffer.append("set: ");
			break;
		case CHANGE_COMPLETE_REFRESH:
			return "complete refresh";
		default:
			buffer.append("unknown change [").append(this.change).append("]: ");	
		}
		buffer.append("itemIndex=").append(this.itemIndex);
		buffer.append(", item=").append(this.affectedItem);
		return buffer.toString();
	}

	public void setItemIndex(int index) {
		this.itemIndex = index;
	}
	
}
