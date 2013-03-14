package de.enough.polish.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import de.enough.polish.util.Trie.Node;

import junit.framework.TestCase;

public class TrieTest extends TestCase {

	private static final String[] SMILIES = new String[] { ":)", ":))", "XOXO", ":)V", ":-D", "=)", ";)", ":-X", ":-*", ":-P", ":\'-(", "T_T", "(stop)", "u_u", ":-b", "%-}", "(@-))", "(exciting1)", "(grin2)", "(kiss2)", "(x_x2)", "(nerd2)", "(music2)", "(on_fire2)", "(sick2)", "(crying2)", "(boo2)", "(ninja2)" };
	private static final String LONGSEARCHTEXT = ":) kaslfkjsd fj ioasjfiose hfioashfsdiofh sio fhoiüg hgiodhgiodhg ag dighioghroighgoihfgoi hofhg oihjoighfd oigsdgfh afd :-P gfdsfg g (stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stopg gdf g fd gfd h lksdjf kld(stop)(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop";

	protected LinkedList searchResults = new LinkedList();

	private final class SearchResultConsumer implements Trie.TrieSearchConsumer {
		public void onWordFound(String originalText, String matchedWord, int matchIndex) {
			addSearchResult(originalText, matchedWord, matchIndex);
		}
	}

	private class SearchResult {
		public SearchResult(String originalText, String matchedWord, int matchIndex) {
			this.originalText = originalText;
			this.matchedWord = matchedWord;
			this.matchIndex = matchIndex;

		}

		public String originalText;
		public String matchedWord;
		public int matchIndex;
	}
	
	public void testSmoketest() {
		String source = "Hallo:)Welt";
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.search(source, trieSearchConsumer);

		assertEquals(1, this.searchResults.size());

		SearchResult searchResult;
		searchResult = (SearchResult) this.searchResults.get(0);
		assertEquals(":)", searchResult.matchedWord);
		assertEquals(5, searchResult.matchIndex);
	}
	
	public void testSmoketestWithResult() {
		String source = "Hallo:)Welt";
		Trie.TrieSearchResult searchResult = new Trie.TrieSearchResult();
		int startIndex = 0;
		Trie trie = new Trie(SMILIES);
		boolean found = trie.search(source, startIndex, searchResult);

		assertTrue(found);
		assertEquals(":)", searchResult.matchedWord);
		assertEquals(5, searchResult.matchedWordIndex);
		
		startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();
		found = trie.search(source, startIndex, searchResult);
		assertFalse(found);
	}


	public void testGreedyOption() {
		String source = "HalloWelt:))";
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.search(source, trieSearchConsumer);

		assertEquals(1, this.searchResults.size());

		SearchResult searchResult;
		searchResult = (SearchResult) this.searchResults.get(0);
		assertEquals(":))", searchResult.matchedWord);
		assertEquals(9, searchResult.matchIndex);
	}

	public void testGreedyOptionWithResult() {
		String source = "HalloWelt:))";
		Trie.TrieSearchResult searchResult = new Trie.TrieSearchResult();
		int startIndex = 0;
		Trie trie = new Trie(SMILIES);
		boolean found = trie.search(source, startIndex, searchResult);

		assertTrue(found);
		assertEquals(":))", searchResult.matchedWord);
		assertEquals(9, searchResult.matchedWordIndex);
		
		startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();
		found = trie.search(source, startIndex, searchResult);
		assertFalse(found);
	}

	public void testShortestMatch() {
		String source = "HalloWelt:))";
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.setLongestMatchOption(false);
		trie.search(source, trieSearchConsumer);

		assertEquals(2, this.searchResults.size());

		SearchResult searchResult;
		searchResult = (SearchResult) this.searchResults.get(0);
		assertEquals(":)", searchResult.matchedWord);
		assertEquals(9, searchResult.matchIndex);

		searchResult = (SearchResult) this.searchResults.get(1);
		assertEquals(":))", searchResult.matchedWord);
		assertEquals(9, searchResult.matchIndex);
	}
	
	public void testShortestMatchWithResult() {
		String source = "HalloWelt:))";
		Trie.TrieSearchResult searchResult = new Trie.TrieSearchResult();
		int startIndex = 0;
		Trie trie = new Trie(SMILIES);
		trie.setLongestMatchOption(false);
		boolean found = trie.search(source, startIndex, searchResult);

		assertTrue(found);
		assertEquals(":)", searchResult.matchedWord);
		assertEquals(9, searchResult.matchedWordIndex);
		startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();
		
		found = trie.search(source, startIndex, searchResult);
		assertFalse(found);
	}


	public void testLongUnmatched() {
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.search(LONGSEARCHTEXT, trieSearchConsumer);

		assertEquals(3, this.searchResults.size());

		SearchResult searchResult;
		searchResult = (SearchResult) this.searchResults.get(0);
		assertEquals(":)", searchResult.matchedWord);
		assertEquals(0, searchResult.matchIndex);

		searchResult = (SearchResult) this.searchResults.get(1);
		assertEquals(":-P", searchResult.matchedWord);
		assertEquals(122, searchResult.matchIndex);

		searchResult = (SearchResult) this.searchResults.get(2);
		assertEquals("(stop)", searchResult.matchedWord);
		assertEquals(532, searchResult.matchIndex);
	}
	
	public void testLongUnmatchedWithResult() {
		Trie trie = new Trie(SMILIES);
		Trie.TrieSearchResult searchResult = new Trie.TrieSearchResult();
		int startIndex = 0;
		boolean found = trie.search(LONGSEARCHTEXT, startIndex, searchResult);
		assertTrue( found );
		assertEquals(":)", searchResult.matchedWord);
		assertEquals(0, searchResult.matchedWordIndex);
		startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();
		
		found = trie.search(LONGSEARCHTEXT, startIndex, searchResult);
		assertTrue( found );
		assertEquals(":-P", searchResult.matchedWord);
		assertEquals(122, searchResult.matchedWordIndex);
		startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();

		found = trie.search(LONGSEARCHTEXT, startIndex, searchResult);
		assertEquals("(stop)", searchResult.matchedWord);
		assertEquals(532, searchResult.matchedWordIndex);

		startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();
		found = trie.search(LONGSEARCHTEXT, startIndex, searchResult);
		assertFalse( found );
	}


	public void testWrongConstructorParameters() {
		try {
			new Trie(null);
			fail();
		} catch (Throwable throwable) {
			// Good.
		}
		try {
			new Trie(new String[] { null });
			fail();
		} catch (Throwable throwable) {
			// Good.
		}
		try {
			new Trie(new String[] { "" });
			fail();
		} catch (Throwable throwable) {
			// Good.
		}
	}

	public void testWrongAddWordParameter() {
		Trie trie = new Trie();
		try {
			trie.addWord(null);
		} catch (Throwable throwable) {
			// Good.
		}
		try {
			trie.addWord("");
		} catch (Throwable throwable) {
			// Good.
		}
	}

	public void testRaceToTheTop() {
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);

		long timeStartTrie = System.currentTimeMillis();
		for (int looper = 0; looper < 5000; looper++) {
			trie.search(LONGSEARCHTEXT, trieSearchConsumer);
		}
		long timeEndTrie = System.currentTimeMillis();

		long timeStartIndexOf = System.currentTimeMillis();
		for (int looper = 0; looper < 10000; looper++) {
			for (int smilieIndex = 0; smilieIndex < SMILIES.length; smilieIndex++) {
				LONGSEARCHTEXT.indexOf(SMILIES[smilieIndex]);
			}
		}
		long timeEndIndexOf = System.currentTimeMillis();

		long durationTrie = timeEndTrie - timeStartTrie;
		long durationIndexOf = timeEndIndexOf - timeStartIndexOf;
		assertTrue("Duration of trie run was '" + durationTrie + "' and of indexOf was '" + durationIndexOf + "'", durationTrie <= durationIndexOf);
	}

	public void testNodeEquality() {
		Trie.Node root1 = new Trie.Node((char) 0);
		populateNode(root1, true, true, 3, 'A');
		Trie.Node root2 = new Trie.Node((char) 0);
		populateNode(root2, true, true, 3, 'A');

		assertTrue(root1.equals(root2));
	}

	public void testTrieEquality() {
		Trie trie1 = new Trie(new String[] { ":-)", ":-(" });
		Trie trie2 = new Trie(new String[] { ":-)", ":-(" });
		assertTrue(trie1.equals(trie2));
	}

	public void testTrieInequality() {
		Trie trie1 = new Trie(new String[] { ":-)", ":-(" });
		Trie trie2 = new Trie(new String[] { ":-)", ":-" });
		assertFalse(trie1.equals(trie2));
	}

	public void testNodeStructure() {
		Node expectedRoot = new Node((char) 0);
		Node nodeA = new Node('A');
		Node nodeB = new Node('B');
		Node nodeC = new Node('C');
		Node nodeD = new Node('D');
		Node nodeE = new Node('E');
		Node nodeF = new Node('F');

		nodeA.word = "A";
		nodeC.word = "BC";
		nodeD.word = "BD";
		nodeE.word = "BE";
		nodeF.word = "F";

		expectedRoot.nextSibling = nodeA;
		nodeA.nextSibling = nodeB;
		nodeB.firstChild = nodeC;
		nodeB.nextSibling = nodeF;
		nodeC.nextSibling = nodeD;
		nodeD.nextSibling = nodeE;

		Trie trie = new Trie(new String[] { "A", "BC", "BD", "BE", "F" });
		Node actualRootNode = trie.getRootNode();
		assertEquals(expectedRoot, actualRootNode);
		assertEquals(expectedRoot.print(), actualRootNode.print());
	}

	public void testRead() {
		byte[] byteArray = new byte[] { 1, 1, 2, 0, 65, 0, 1, 65, 2, 0, 66, 0, 0, 1, 0, 67, 0, 2, 66, 67, 2, 0, 68, 0, 2, 66, 68, 2, 0, 69, 0, 2, 66, 69, 3, 2, 0, 70, 0, 1, 70, 3 };
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		Trie actualTrie = new Trie();
		try {
			actualTrie.read(dataInputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}
		Trie expectedTrie = new Trie(new String[] { "A", "BC", "BD", "BE", "F" });
		System.out.println(actualTrie);
		System.out.println(expectedTrie);
		assertEquals(expectedTrie, actualTrie);
	}

	public void testWrite() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		Trie trie = new Trie(new String[] { "A", "BC", "BD", "BE", "F" });
		try {
			trie.write(dataOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		byte[] actualByteArray = byteArrayOutputStream.toByteArray();
		byte[] expectedByteArray = new byte[] { 1, 1, 2, 0, 65, 0, 1, 65, 2, 0, 66, 0, 0, 1, 0, 67, 0, 2, 66, 67, 2, 0, 68, 0, 2, 66, 68, 2, 0, 69, 0, 2, 66, 69, 3, 2, 0, 70, 0, 1, 70, 3 };
		System.out.println(printByteArray(actualByteArray));

		assertEquals(expectedByteArray.length, actualByteArray.length);
		for (int i = 0; i < expectedByteArray.length; i++) {
			assertEquals(expectedByteArray[i], actualByteArray[i]);
		}
	}

	public void testReadWrite() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		Trie trie1 = new Trie(new String[] { ":-)", ":-(" });
		try {
			trie1.write(dataOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		Trie trie2 = new Trie();
		try {
			trie2.read(dataInputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}
		assertTrue(trie1.equals(trie2));
	}

	protected String printByteArray(byte[] byteArray) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			buffer.append(Integer.toString(byteArray[i]));
			buffer.append(" ");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	protected char populateNode(Trie.Node node, boolean addChild, boolean addSibling, int level, char character) {
		if (level == 0) {
			return character;
		}
		// if(node == null) {
		// return character;
		// }
		char nextCharacter = character;
		if (addChild) {
			node.firstChild = new Trie.Node(nextCharacter);
			nextCharacter = populateNode(node.firstChild, addChild, addSibling, level - 1, (char) (nextCharacter + 1));
		}
		if (addSibling) {
			// if(level%2 != 0) {
			// addSibling = false;
			// }
			node.nextSibling = new Trie.Node((char) (nextCharacter));
			nextCharacter = populateNode(node.nextSibling, addChild, addSibling, level - 1, (char) (nextCharacter + 1));
		}
		return nextCharacter;
	}

	protected void addSearchResult(String originalText, String matchedWord, int matchIndex) {
		this.searchResults.add(new SearchResult(originalText, matchedWord, matchIndex));
	}
}
