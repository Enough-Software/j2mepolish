/*
 * Copyright (c) 2013 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.util.Hashtable;

/**
 * Uses a trie for simple search and replace tasks.
 * @author Robert Virkus
 * @see Trie
 */
public class TrieReplacer
{
	private Trie trie;
	private Hashtable replacements;
	private Trie.TrieSearchResult result;

	/**
	 * Creates a new TrieReplacer with an initial capacity of 113
	 */
	public TrieReplacer()
	{
		this(113);
	}

	/**
	 * Creates a new TrieReplacer
	 * @param initialCapacity the initial capacity
	 */
	public TrieReplacer(int initialCapacity)
	{
		this.trie = new Trie();
		this.replacements = new Hashtable(initialCapacity);
		this.result = new Trie.TrieSearchResult();
	}

	/**
	 * Adds a replacement
	 * @param match the matching word
	 * @param replacement the corresponding replacement
	 */
	public void addReplacement(String match, String replacement)
	{
		this.trie.addWord(match);
		this.replacements.put(match, replacement);
	}
	
	/**
	 * Replaces all found matches in the given input text
	 * @param input the input text
	 * @return the input text where all matches are replaced by their corresponding replacements 
	 */
	public String replaceMatches(String input)
	{
		if (input == null)
		{
			return null;
		}
		int startIndex = 0;
		Trie.TrieSearchResult res = this.result;
		boolean foundMatch = this.trie.search(input, startIndex, res);
		if (!foundMatch)
		{
			return input;
		}
		
		StringBuffer output = new StringBuffer();
		while (foundMatch)
		{
			if (res.matchedWordIndex > startIndex)
			{
				output.append(input.substring(startIndex, res.matchedWordIndex));
			}
			String replacement = (String) this.replacements.get(res.matchedWord);
			output.append(replacement);
			startIndex = res.matchedWordIndex + res.matchedWord.length();
			foundMatch = this.trie.search(input, startIndex, res);
		}
		if (startIndex < input.length())
		{
			output.append(input.substring(startIndex));
		}
		return output.toString();
	}
}
