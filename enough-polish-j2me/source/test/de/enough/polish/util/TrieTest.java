package de.enough.polish.util;

import java.util.LinkedList;

import junit.framework.TestCase;

public class TrieTest extends TestCase {

	private static final String[] SMILIES = new String[] {":)",":))","XOXO",":)V",":-D","=)",";)",":-X",":-*",":-P",":\'-(","T_T","(stop)","u_u",":-b","%-}","(@-))","(exciting1)","(grin2)","(kiss2)","(x_x2)","(nerd2)","(music2)","(on_fire2)","(sick2)","(crying2)","(boo2)","(ninja2)"};
	
	protected LinkedList searchResults = new LinkedList();
	
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
		Trie.TrieSearchConsumer trieSearchConsumer = new Trie.TrieSearchConsumer() {
			
			public void onWordFound(String originalText, String matchedWord, int matchIndex) {
				addSearchResult(originalText, matchedWord, matchIndex);
			}
		};
		Trie trie = new Trie(SMILIES);
		System.out.println(trie.toString());
		trie.search(source, trieSearchConsumer);

		assertEquals(1,this.searchResults.size());
		SearchResult searchResult = (SearchResult)this.searchResults.get(0);
		assertEquals(":)",searchResult.matchedWord);
		assertEquals(5,searchResult.matchIndex);
	}
	
	public void testGreedyOption() {
		String source = "HalloWelt:))";
		Trie.TrieSearchConsumer trieSearchConsumer = new Trie.TrieSearchConsumer() {
			
			public void onWordFound(String originalText, String matchedWord, int matchIndex) {
				addSearchResult(originalText, matchedWord, matchIndex);
			}
		};
		Trie trie = new Trie(SMILIES);
		trie.search(source, trieSearchConsumer);

		assertEquals(1,this.searchResults.size());
		SearchResult searchResult = (SearchResult)this.searchResults.get(0);
		assertEquals(":))",searchResult.matchedWord);
		assertEquals(9,searchResult.matchIndex);
	}
	
	public void testShortestMatch() {
		String source = "HalloWelt:))";
		Trie.TrieSearchConsumer trieSearchConsumer = new Trie.TrieSearchConsumer() {
			
			public void onWordFound(String originalText, String matchedWord, int matchIndex) {
				addSearchResult(originalText, matchedWord, matchIndex);
			}
		};
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
	
	
	
	protected void addSearchResult(String originalText, String matchedWord, int matchIndex) {
		this.searchResults.add(new SearchResult(originalText,matchedWord,matchIndex));
	}

}
