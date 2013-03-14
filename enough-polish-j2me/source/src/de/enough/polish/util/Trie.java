package de.enough.polish.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * A trie is a data structure for efficient matching of words in a search text.
 * <dl>
 * <dt>Life Cycle:
 * <dd>The object is created with new. No factories necessary.
 * <dt>Statefulness:
 * <dd>The object is stateful. Once created, it contains the added words until its end. Words can not be removed.
 * <dt>Parents and Children:
 * <dd>The object is not managed by another object. It does not manage other objects.
 * <dt>Volatility:
 * <dd>The object is in-memory only. But it may be serialized with the interface Externalizable.
 * <dt>Synchronization:
 * <dd>The methods of the object are not thread-safe.
 * <dt>Configuration:
 * <dd>The words to match must be provided, either in the constructor or with {@link #addWord(String)}.
 * <dt>Dependencies:
 * <dd>For this object to be useful, another object should implement {@link #Trie.TrieSearchConsumer} to receive matched words.
 * </dl>
 * 
 * @author rickyn
 */
public class Trie implements Externalizable {

	/**
	 * A callback object which is called after a word in a search text was found.
	 * 
	 * @author rickyn
	 */
	public interface TrieSearchConsumer {

		/**
		 * Callback method which is called when the given word in the given search text was found.
		 * Invariant: The matchedWordIndex parameter is always equals or greater then the parameter of the next invocation.
		 * 
		 * @param searchText The search text in which the matched word was found.
		 * @param matchedWord The word which was found.
		 * @param matchedWordIndex The index in the search text at which the matched word was found.
		 */
		void onWordFound(String searchText, String matchedWord, int matchedWordIndex);
	}
	
	/**
	 * A search result that provides both the found word and that word's index
	 *  
	 * @author Robert Virkus
	 * @see Trie#search(String, int, TrieSearchResult)
	 */
	public static class TrieSearchResult
	{
		/**
		 * The found word
		 */
		public String matchedWord;
		
		/**
		 * The start index of the found word
		 */
		public int matchedWordIndex;
	}

	/**
	 * A node object is the building block of the trie. They relate in a child-sibling organization to prevent dynamic management of children (First-Child Next-Sibling Tree)
	 * 
	 * @author rickyn
	 */
	static class Node {
		public char character;
		public Node nextSibling;
		public Node firstChild;
		public String word;

		public Node(char character) {
			this.character = character;
		}

		public String toString() {
			return "Node={character:'" + ((this.character >= 33 && this.character <= 126) ? this.character + "" : "0x" + Integer.toHexString(this.character)) + "',word:'" + this.word + "'}";
		}

		public String print() {
			StringBuffer buffer = new StringBuffer();
			print0(0, buffer);
			return buffer.toString();
		}

		public void print0(int indent, StringBuffer buffer) {
			for (int i = 0; i < indent; i++) {
				buffer.append(" ");
			}
			buffer.append(toString());
			buffer.append("\n");
			if (this.firstChild != null) {
				this.firstChild.print0(indent + 2, buffer);
			}
			if (this.nextSibling != null) {
				this.nextSibling.print0(indent, buffer);
			}
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.character;
			result = prime * result + ((this.firstChild == null) ? 0 : this.firstChild.hashCode());
			result = prime * result + ((this.nextSibling == null) ? 0 : this.nextSibling.hashCode());
			result = prime * result + ((this.word == null) ? 0 : this.word.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (this.character != other.character)
				return false;
			if (this.firstChild == null) {
				if (other.firstChild != null)
					return false;
			} else if (!this.firstChild.equals(other.firstChild))
				return false;
			if (this.nextSibling == null) {
				if (other.nextSibling != null)
					return false;
			} else if (!this.nextSibling.equals(other.nextSibling))
				return false;
			if (this.word == null) {
				if (other.word != null)
					return false;
			} else if (!this.word.equals(other.word))
				return false;
			return true;
		}
	}

	private static final short LATEST_FORMAT_VERSION = 1;
	private static final short COMMAND_CHILD = 1;
	private static final short COMMAND_SIBLING = 2;
	private static final short COMMAND_LEAF = 3;

	private static final char ROOT_NODE_CHARACTER = 0;
	private static final char STACK_LOOKAT_CHILD = 2;
	private static final char STACK_LOOKAT_SIBLING = 1;

	private Node root = new Node(ROOT_NODE_CHARACTER);
	private boolean longestMatchOption = true;

	/**
	 * Construct a Trie object and add the given initial words.
	 * 
	 * @param words The Array parameter must not be null and must not contain null elements or empty strings.
	 * @see #addWord(String)
	 * @throws IllegalArgumentException Thrown if the parameter is null.
	 */
	public Trie(String[] words) {
		if (words == null) {
			throw new IllegalArgumentException("The parameter 'words' must not be null.");
		}
		for (int i = 0; i < words.length; i++) {
			addWord(words[i]);
		}
	}

	/**
	 * Construct a Trie object without any words registered.
	 * 
	 * @see #addWord(String)
	 */
	public Trie() {
		//
	}

	public String toString() {
		return this.root.print();
	}

	/**
	 * Register a word in this Trie to be found when a search text is searched with {@link #search(String, TrieSearchConsumer)}.
	 * 
	 * @param word The parameter must not be null or empty.
	 * @throws IllegalArgumentException Thrown if the parameter is invalid.
	 */
	public void addWord(String word) {
		if (word == null) {
			throw new IllegalArgumentException("The parameter 'word' must not be null.");
		}
		if (word.length() == 0) {
			throw new IllegalArgumentException("The String parameter 'word' must not be empty.");
		}
		int wordLength = word.length();
		char currentCharacter;
		Node currentNode = this.root;
		Node previousNode = currentNode;
		for (int i = 0; i < wordLength; i++) {
			currentCharacter = word.charAt(i);
			if (currentNode == null) {
				currentNode = new Node(currentCharacter);
				previousNode.firstChild = currentNode;
			}
			while (currentNode != null) {
				if (currentCharacter == currentNode.character) {
					break;
				} else {
					previousNode = currentNode;
					currentNode = currentNode.nextSibling;
				}
			}
			if (currentNode == null) {
				currentNode = new Node(currentCharacter);
				previousNode.nextSibling = currentNode;
			}
			if (i == wordLength - 1) {
				// The last character.
				currentNode.word = word;
			}
			previousNode = currentNode;
			currentNode = currentNode.firstChild;
		}
	}

	/**
	 * Search the given text to match words registered in this Trie object and notify the consumer about the findings by calling the given
	 * callback object.
	 * 
	 * @param text The parameter must not be null though the string may be empty.
	 * @param trieSearchConsumer If the parameter is null, no search is performed.
	 * @throws IllegalArgumentException Thrown if the parameter is invalid.
	 */
	public void search(String text, TrieSearchConsumer trieSearchConsumer) {
		if (text == null) {
			throw new IllegalArgumentException("The parameter 'text' must not be null.");
		}
		if (trieSearchConsumer == null) {
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
				if (currentCharacter == currentNode.character) {
					if (currentNode.word != null) {
						if (this.longestMatchOption) {
							// Just remember the word. We report it later.
							lastFoundWord = currentNode.word;
						} else {
							trieSearchConsumer.onWordFound(text, currentNode.word, textIndex);
						}
					}
					searchIndex += 1;
					if (searchIndex >= textLength) {
						break;
					}
					currentCharacter = text.charAt(searchIndex);
					currentNode = currentNode.firstChild;
				} else {
					currentNode = currentNode.nextSibling;
				}
			} while (currentNode != null);
			if (this.longestMatchOption) {
				if (lastFoundWord != null) {
					trieSearchConsumer.onWordFound(text, lastFoundWord, textIndex);
					lastFoundWord = null;
				}
			}
		}
	}
	
	/**
	 * Searches the given text to match words registered in this Trie object.
	 * 
	 * @param text The parameter must not be null though the string may be empty.
	 * @param result The result for the next match, this parameter should be reused over a search
	 * @throws IllegalArgumentException when either text or result are null
	 * @return true when the search was successful, false if nothing was found
	 */
	public boolean search(String text, int startIndex, TrieSearchResult result) {
		if (text == null) {
			throw new IllegalArgumentException("The parameter 'text' must not be null.");
		}
		if (result == null) {
			throw new IllegalArgumentException("The parameter 'result' must not be null.");
		}
		int textLength = text.length();
		int searchIndex;
		char currentCharacter;
		for (int textIndex = startIndex; textIndex < textLength; textIndex++) {
			Node currentNode = this.root;
			currentCharacter = text.charAt(textIndex);
			searchIndex = textIndex;
			String lastFoundWord = null;
			do {
				if (currentCharacter == currentNode.character) {
					if (currentNode.word != null) {
						if (this.longestMatchOption) {
							// Just remember the word. We report it later.
							lastFoundWord = currentNode.word;
						} else {
							result.matchedWord = currentNode.word;
							result.matchedWordIndex = textIndex;
							return true;
							//trieSearchConsumer.onWordFound(text, currentNode.word, textIndex);
						}
					}
					searchIndex += 1;
					if (searchIndex >= textLength) {
						break;
					}
					currentCharacter = text.charAt(searchIndex);
					currentNode = currentNode.firstChild;
				} else {
					currentNode = currentNode.nextSibling;
				}
			} while (currentNode != null);
			if (this.longestMatchOption) {
				if (lastFoundWord != null) {
					result.matchedWord = lastFoundWord;
					result.matchedWordIndex = textIndex;
					lastFoundWord = null;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Matching a word in a text can use a shortest or longest match mechanism.
	 * 
	 * @param longestMatchOption Value true if the longest matching word should be found in the text. Value false if every word should be
	 * matched even if it is a prefix to another word like :) to :)).
	 */
	public void setLongestMatchOption(boolean longestMatchOption) {
		this.longestMatchOption = longestMatchOption;
	}

	
	public void write(DataOutputStream out) throws IOException {
		/*
		 * The format of the output stream is as follows: The stream contains a header and a Command-Data pair for each node.
		 * The header contains
		 * <table>
		 * <tr>
		 * <td>Byte</td><td>Version of the format.</td>
		 * </tr>
		 * <tr>
		 * <td>Boolean</td><td>LongestMatchOption.</td>
		 * </tr>
		 * </table>
		 * 
		 * The Command-Data pairs contain
		 * <table>
		 * <tr>
		 * <td>Byte</td><td>A command on how to handle the next character. {@link #COMMAND_CHILD} creates a child node, {@link #COMMAND_SIBLING} creates a sibling node and {@link #COMMAND_LEAF} indicates a leaf node.</td>
		 * </tr>
		 * <tr>
		 * <td>Char</td><td>The character of the node</td>
		 * </tr>
		 * <tr>
		 * <td>String</td><td>The word at this node. If the string is empty, the original node must have a null value.</td>
		 * </tr>
		 * </table>
		 * All data types have the size and structure specified in {@link DataOutputStream} and {@link DataInputStream}.
		 */
		Node trieElement;

		out.writeByte(LATEST_FORMAT_VERSION);

		out.writeBoolean(this.longestMatchOption);
		Node stackElement = new Node((char) STACK_LOOKAT_SIBLING);
		stackElement.firstChild = this.root;
		Node topElement = stackElement;

		while (topElement != null) {
			// Look at element.
			stackElement = topElement;
			trieElement = stackElement.firstChild;

			if (stackElement.character == STACK_LOOKAT_CHILD) {
				// Process child.
				stackElement.character = 1;
				if (trieElement.firstChild != null) {
					stackElement = new Node(STACK_LOOKAT_CHILD);
					stackElement.firstChild = trieElement.firstChild;
					stackElement.nextSibling = topElement;
					topElement = stackElement;
					out.writeByte(COMMAND_CHILD);
					out.writeChar(stackElement.firstChild.character);
					String word = stackElement.firstChild.word;
					if (word == null) {
						word = "";
					}
					out.writeUTF(word);
				}
				continue;
			}
			if (stackElement.character == STACK_LOOKAT_SIBLING) {
				// Process the sibling.
				stackElement.character = STACK_LOOKAT_CHILD;
				if (trieElement.nextSibling != null) {
					stackElement.firstChild = trieElement.nextSibling;
					topElement = stackElement;
					out.writeByte(COMMAND_SIBLING);
					out.writeChar(stackElement.firstChild.character);
					String word = stackElement.firstChild.word;
					if (word == null) {
						word = "";
					}
					out.writeUTF(word);
				} else {
					out.writeByte(COMMAND_LEAF);
					// Pop.
					topElement.firstChild = null;
					topElement = topElement.nextSibling;
				}
				continue;
			}
		}
	}

	/**
	 * Initializes an empty Trie object with a serialized version.
	 * 
	 * @throws IllegalStateException Thrown if the trie object has already words added.
	 * @throws IOException Thrown if the format version of the serialized trie is not supported.
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		if (!(this.root.firstChild == null && this.root.nextSibling == null)) {
			throw new IllegalStateException("This object is already initialized with words. Only an empty Trie object can be read into. Create an empty Trie object with 'new Trie()'");
		}
		short version = in.readByte();
		if (version != LATEST_FORMAT_VERSION) {
			throw new IOException("The format version of the input is '" + version + "'. Only version '" + LATEST_FORMAT_VERSION + "' is supported. Convert the input or implement the unsupported version.");
		}
		
		this.longestMatchOption = in.readBoolean();

		Node stackElement = new Node((char) STACK_LOOKAT_SIBLING);
		stackElement.firstChild = this.root;
		Node topElement = stackElement;
		Node trieNode;
		while (topElement != null) {
			byte command = in.readByte();
			if (command == COMMAND_LEAF) {
				// Pop
				topElement = topElement.nextSibling;
				if (topElement != null) {
					topElement.character = STACK_LOOKAT_SIBLING;
				}
				continue;
			}
			char character = in.readChar();
			String word = in.readUTF();
			if (word.length() == 0) {
				word = null;
			}
			trieNode = new Node(character);
			trieNode.word = word;
			if (command == COMMAND_CHILD) {
				topElement.firstChild.firstChild = trieNode;
				stackElement = new Node(STACK_LOOKAT_CHILD);
				stackElement.nextSibling = topElement;
				stackElement.firstChild = trieNode;
				topElement = stackElement;
			}
			if (command == COMMAND_SIBLING) {
				topElement.firstChild.nextSibling = trieNode;
				topElement.firstChild = trieNode;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.longestMatchOption ? 1231 : 1237);
		result = prime * result + ((this.root == null) ? 0 : this.root.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trie other = (Trie) obj;
		if (this.longestMatchOption != other.longestMatchOption)
			return false;
		if (this.root == null) {
			if (other.root != null)
				return false;
		} else if (!this.root.equals(other.root))
			return false;
		return true;
	}

	/**
	 * Helper method for unit tests to inspect the internal state of this object.
	 * 
	 * @return
	 */
	Node getRootNode() {
		return this.root;
	}

}
