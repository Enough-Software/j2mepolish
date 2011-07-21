/*
 * Created on Jun 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.webprocessor;

import java.util.Comparator;

/**
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KeywordComparator implements Comparator {

	/**
	 * 
	 */
	public KeywordComparator() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if (o1 instanceof Keyword && o2 instanceof Keyword ) {
			Keyword keyword1 = (Keyword) o1;
			Keyword keyword2 = (Keyword) o2;
			return keyword1.indexKeywordLowercase.compareTo( keyword2.indexKeywordLowercase );
		} else {
			return 0;
		}
	}

}
