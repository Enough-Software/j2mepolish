package de.enough.polish.app.model;

import java.util.Random;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;

public class ContactCollection
{
	
	private static ContactCollection instance; 
	private ArrayList contactsList;
	
	private ContactCollection()
	{
		this.contactsList = new ArrayList();
	}
	
	public void addContact(Contact contact)
	{
		this.contactsList.add(contact);
	}
	
	public int size()
	{
		return this.contactsList.size();
	}
	
	public Contact getContact(int index)
	{
		return (Contact) this.contactsList.get(index);
	}
	
	public Object[] getInternalArray()
	{
		return this.contactsList.getInternalArray();
	}

	public static ContactCollection getInstance()
	{
		if (instance == null)
		{
			instance = new ContactCollection();
			instance.setup();
		}
		return instance;
	}

	private void setup()
	{
		String[] firstNames = new String[]{ 
				"Anna", "Andy", "Benny", "Chris", "Deeboorah", "Denise", "Eliza", "Frank", "Guz", "Heather", "Ivan", "Jim", "Kim", "Kimberley", "Liz", "Madlaine", "Michael", "Nele", "Olga", "Peter", "Robert", "Sina", "Tom", "Udo", "Vic", "Walt", "Yvy", "Zach"
 		};
		String[] lastNames = new String[]{ 
				"Archer", "Beiley", "Baker", "Brewer", "Carter", "Chandler", "Cooper", "Cook", "Thatcher", "Walker", "Weaver", "Sawyer", "Slater", "Wright", "Brown", "Short", "Burton", "Hamilton", "Richardson"
 		};
		Random random = new Random(System.currentTimeMillis());
		int numberOfContacts = 1000;
		while (numberOfContacts > 0)
		{
			String first = setupName(random, firstNames);
			String last = setupName(random, lastNames);
			Contact contact = new Contact( first, last );
			addContact( contact );
			numberOfContacts--;
		}
		sort();
	}

	private void sort()
	{
		Arrays.shellSort(this.contactsList);
	}

	private String setupName(Random random, String[] names)
	{
		boolean appendDelimiter = (random.nextInt(2) == 1);
		int number = random.nextInt(3) + 1;
		StringBuffer buffer = new StringBuffer();
		boolean isFirstRound = true;
		while (number >= 1)
		{
			int index = random.nextInt(names.length);
			String name = names[index];
			if (!isFirstRound)
			{
				if (appendDelimiter) {
					buffer.append('-');
				} else {
					buffer.append(' ');
				}
			}
			buffer.append(name);
			number--;
			isFirstRound = false;
		}
		return buffer.toString();
	}
}
