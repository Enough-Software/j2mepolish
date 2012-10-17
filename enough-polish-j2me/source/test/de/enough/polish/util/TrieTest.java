package de.enough.polish.util;

import java.util.LinkedList;

import junit.framework.TestCase;

public class TrieTest extends TestCase {

	private static final String[] SMILIES = new String[] {":)",":))","XOXO",":)V",":-D","=)",";)",":-X",":-*",":-P",":\'-(","T_T","(stop)","u_u",":-b","%-}","(@-))","(exciting1)","(grin2)","(kiss2)","(x_x2)","(nerd2)","(music2)","(on_fire2)","(sick2)","(crying2)","(boo2)","(ninja2)"};
	private static final String LONGSEARCHTEXT = ":) kaslfkjsd fj ioasjfiose hfioashfsdiofh sio fhoiüg hgiodhgiodhg ag dighioghroighgoihfgoi hofhg oihjoighfd oigsdgfh afd :-P gfdsfg g (stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stopg gdf g fd gfd h lksdjf kld(stop)(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop(stop";
	protected LinkedList searchResults = new LinkedList();
	
	private final class SearchResultConsumer implements Trie.TrieSearchConsumer {
		public void onWordFound(String originalText, String matchedWord, int matchIndex) {
			addSearchResult(originalText, matchedWord, matchIndex);
		}
	}

	private class SearchResult{
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

		assertEquals(1,this.searchResults.size());
		
		SearchResult searchResult;
		searchResult = (SearchResult)this.searchResults.get(0);
		assertEquals(":)",searchResult.matchedWord);
		assertEquals(5,searchResult.matchIndex);
	}
	
	public void testGreedyOption() {
		String source = "HalloWelt:))";
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.search(source, trieSearchConsumer);

		assertEquals(1,this.searchResults.size());
		
		SearchResult searchResult;
		searchResult = (SearchResult)this.searchResults.get(0);
		assertEquals(":))",searchResult.matchedWord);
		assertEquals(9,searchResult.matchIndex);
	}
	
	public void testShortestMatch() {
		String source = "HalloWelt:))";
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.setLongestMatchOption(false);
		trie.search(source, trieSearchConsumer);

		assertEquals(2,this.searchResults.size());
		
		SearchResult searchResult;
		searchResult = (SearchResult)this.searchResults.get(0);
		assertEquals(":)",searchResult.matchedWord);
		assertEquals(9,searchResult.matchIndex);
		
		searchResult = (SearchResult)this.searchResults.get(1);
		assertEquals(":))",searchResult.matchedWord);
		assertEquals(9,searchResult.matchIndex);
	}
	
	public void testLongUnmatched() {
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		trie.search(LONGSEARCHTEXT, trieSearchConsumer);

		assertEquals(3,this.searchResults.size());
		
		SearchResult searchResult;
		searchResult = (SearchResult)this.searchResults.get(0);
		assertEquals(":)",searchResult.matchedWord);
		assertEquals(0,searchResult.matchIndex);

		searchResult = (SearchResult)this.searchResults.get(1);
		assertEquals(":-P",searchResult.matchedWord);
		assertEquals(121,searchResult.matchIndex);
		
		searchResult = (SearchResult)this.searchResults.get(2);
		assertEquals("(stop)",searchResult.matchedWord);
		assertEquals(531,searchResult.matchIndex);
	}
	
	public void testWrongConstructorParameters() {
		try {
			new Trie(null);
			fail();
		} catch(Throwable throwable) {
			// Good.
		}
		try {
			new Trie(new String[] {null});
			fail();
		} catch(Throwable throwable) {
			// Good.
		}
		try {
			new Trie(new String[] {""});
			fail();
		} catch(Throwable throwable) {
			// Good.
		}
	}
	
	public void testWrongAddWordParameter() {
		Trie trie = new Trie();
		try {
			trie.addWord(null);
		} catch(Throwable throwable) {
			// Good.
		}
		try {
			trie.addWord("");
		} catch(Throwable throwable) {
			// Good.
		}
	}
	
	public void testRaceToTheTop() {
		Trie.TrieSearchConsumer trieSearchConsumer = new SearchResultConsumer();
		Trie trie = new Trie(SMILIES);
		
		long timeStartTrie = System.currentTimeMillis();
		for(int looper = 0; looper < 10000; looper++) {
			trie.search(LONGSEARCHTEXT, trieSearchConsumer);
		}
		long timeEndTrie = System.currentTimeMillis();
		
		long timeStartIndexOf = System.currentTimeMillis();
		for(int looper = 0; looper < 10000; looper++) {
			for(int smilieIndex = 0; smilieIndex < SMILIES.length; smilieIndex++) {
				LONGSEARCHTEXT.indexOf(SMILIES[smilieIndex]);
			}
		}
		long timeEndIndexOf = System.currentTimeMillis();
		
		long durationTrie = timeEndTrie-timeStartTrie;
		long durationIndexOf = timeEndIndexOf-timeStartIndexOf;
		assertTrue("Duration of trie run was '"+durationTrie+"' and of indexOf was '"+durationIndexOf+"'", durationTrie <= durationIndexOf);
	}
	
	protected void addSearchResult(String originalText, String matchedWord, int matchIndex) {
		this.searchResults.add(new SearchResult(originalText,matchedWord,matchIndex));
	}
}
