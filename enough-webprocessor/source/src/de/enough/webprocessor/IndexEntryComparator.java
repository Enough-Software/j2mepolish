/*
 * Created on Jun 4, 2004
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
public class IndexEntryComparator implements Comparator {
	
	public IndexEntryComparator() {
		// nothing has to be done
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if (o1 instanceof IndexEntry && o2 instanceof IndexEntry) {
			IndexEntry entry1 = (IndexEntry) o1;
			IndexEntry entry2 = (IndexEntry) o2;
			return entry1.fullName.compareTo( entry2.fullName );
		}
		return 0;
	}

}
