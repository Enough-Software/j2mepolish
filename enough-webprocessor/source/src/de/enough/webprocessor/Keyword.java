/*
 * Created on Jun 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.webprocessor;

import de.enough.webprocessor.util.TextUtil;

/**
 * Represents a keyword for an index
 * 
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Keyword {
	/**
	 * The complete and original keyword which can contain a "::" for
	 * separating the index-key from the search-key
	 */
	protected String pattern;
	/** 
	 * the orignal index-keyword which appears in the keyword-index
	 */
	protected String indexKeyword;
	/** 
	 * the orignal index-keyword in lowercase is used for sorting
	 */
	protected String indexKeywordLowercase;
	/**
	 * The searchkey contains the word for which is searched in lowercase
	 */
	protected String searchkey;
	/**
	 * Creates a new keyword
	 * 
	 * @param pattern the pattern for keyword which can contain a "::" to separate the index- from the actual search-keyword
	 */
	public Keyword( String pattern ) {
		pattern = TextUtil.replace( pattern, "<", "&lt;" );
		pattern = TextUtil.replace( pattern, ">", "&gt;" );
		this.pattern = pattern;

		int separatorPos = pattern.indexOf("::");
		if (separatorPos == -1) {
			// this is a normal keyword
			this.indexKeyword = pattern;
			this.indexKeywordLowercase = pattern.toLowerCase();
			this.searchkey = this.indexKeywordLowercase;
		} else {
			this.indexKeyword = pattern.substring(0, separatorPos ).trim();
			this.indexKeywordLowercase = this.indexKeyword.toLowerCase();
			this.searchkey = pattern.substring( separatorPos + 2 ).trim();
			//System.out.println("Keyword=[" + pattern + "]   indexKey=[" + this.indexKeywordLowercase + "]   searchkey=[" + this.searchkey + "].");
		}
	}
	
	

	/**
	 * @return Returns the indexKeyword.
	 */
	public String getIndexKeyword() {
		return this.indexKeyword;
	}
	/**
	 * @return Returns the indexKeywordLowercase.
	 */
	public String getIndexKeywordLowercase() {
		return this.indexKeywordLowercase;
	}
	/**
	 * @return Returns the pattern.
	 */
	public String getPattern() {
		return this.pattern;
	}
	/**
	 * @return Returns the searchkey.
	 */
	public String getSearchkey() {
		return this.searchkey;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Keyword) {
			return ((Keyword) o).pattern.equals( this.pattern );
		} else {
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.pattern.hashCode();
	}
}
