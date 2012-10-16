package de.enough.polish.util;


/**
 * 
 * @author rickyn
 *
 */
public class Trie {
	
	/**
	 * A callback object which is called after a word in a search text was found.
	 * @author rickyn
	 *
	 */
	public interface TrieSearchConsumer {
		
		/**
		 * 
		 * @param searchText The search text in which the matched word was found.
		 * @param matchedWord The word which was found.
		 * @param matchedWordIndex The index in the search text at which the matched word was found.
		 */
		public void onWordFound( String searchText, String matchedWord, int matchedWordIndex);
	}

	private Node root = new Node((char)0);
	private boolean longestMatchOption = true;
	
	private class Node{
		public char character;
		public Node nextSibling;
		public Node firstChild;
		public String word;
		public Node(char character) {
			this.character = character;
		}
		public String toString() {
			return "Node = {character:'"+this.character+"',word:'"+this.word+"'}";
		}
		public void print(int indent,StringBuffer buffer) {
			for(int i = 0; i < indent; i++) {
				buffer.append(" ");
			}
			buffer.append(toString());
			buffer.append("\n");
			if(firstChild != null) {
				for(int i = 0; i < indent; i++) {
					buffer.append(" ");
				}
				firstChild.print(indent+2, buffer);
			}
			if(nextSibling != null) {
				for(int i = 0; i < indent; i++) {
					buffer.append(" ");
				}
				nextSibling.print(indent, buffer);
			}
		}
	}
	
	/**
	 * 
	 * @param The words which should be found in the search text. Array MUST not be null and must not contain null elements or empty strings.
	 * @see #addWord(String)
	 */
	public Trie(String[] words) {
		if(words == null) {
			throw new IllegalArgumentException();
		}
		for(int i = 0; i < words.length;i++) {
			addWord(words[i]);
		}
	}
	
	public Trie() {
		
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		this.root.print(0, buffer);
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param word A word which should be found in a search text. Value must not be null or empty.
	 */
	public void addWord(String word) {
		if(word == null) {
			throw new IllegalArgumentException();
		}
		if(word.length() == 0) {
			throw new IllegalArgumentException();
		}
		int wordLength = word.length();
		char currentCharacter;
		Node currentNode = this.root;
		Node previousNode = currentNode;
		for (int i = 0; i < wordLength; i++) {
			currentCharacter = word.charAt(i);
			if(currentNode == null) {
				currentNode = new Node(currentCharacter);
				previousNode.firstChild = currentNode;
			}
			while(currentNode != null) {
				if(currentCharacter == currentNode.character) {
					break;
				} else {
					previousNode = currentNode;
					currentNode = currentNode.nextSibling;
				}
			}
			if(currentNode == null) {
				currentNode = new Node(currentCharacter);
				previousNode.nextSibling = currentNode;
			}
			if(i == wordLength-1) {
				// The last character.
				currentNode.word = word;
			}
			previousNode = currentNode;
			currentNode = currentNode.firstChild;
		}
	}
	
	/**
	 * 
	 * @param text The text in which words should be found.
	 * @param trieSearchConsumer A callback which is called when a word is found in the text.
	 */
	public void search(String text, TrieSearchConsumer trieSearchConsumer) {
		if(this.root == null) {
			return;
		}
		if(text == null) {
			throw new IllegalArgumentException();
		}
		if(trieSearchConsumer == null) {
			return;
		}
		int textLength = text.length();
		int searchIndex;
		char currentCharacter;
		for (int textIndex = 0; textIndex < textLength; textIndex++) {
			Node currentNode = this.root;
			currentCharacter = text.charAt(textIndex);
			searchIndex = textIndex;
			String lastFoundWord = null;
			do {
				if(currentCharacter == currentNode.character) {
					if(currentNode.word != null) {
						if(this.longestMatchOption) {
							// Just rememeber the word. We report it later.
							lastFoundWord = currentNode.word;
						} else {
							trieSearchConsumer.onWordFound(text, currentNode.word, textIndex);
						}
					}
					searchIndex += 1;
					if(searchIndex >= textLength) {
						break;
					}
					currentCharacter = text.charAt(searchIndex);
					currentNode = currentNode.firstChild;
				} else {
					currentNode = currentNode.nextSibling;
				}
			} while(currentNode != null);
			if(this.longestMatchOption) {
				if(lastFoundWord != null) {
					trieSearchConsumer.onWordFound(text, lastFoundWord, textIndex);
					lastFoundWord = null;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param longestMatchOption Value true if the longest matching word should be found in the text. Value false if every word should be matched even if it is a prefix to another word like :) to :)).
	 */
	public void setLongestMatchOption(boolean longestMatchOption) {
		this.longestMatchOption = longestMatchOption;
	}

}
