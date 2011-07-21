package de.enough.polish.util;

import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

public class  PathUtilTest extends TestCase {

	public void testProperties() {
		Properties props = System.getProperties();
		Object[] keys = props.keySet().toArray();
		Arrays.sort( keys );
		for (int i = 0; i < keys.length; i++) {
			Object key  = keys[i];
			System.out.println( key + "=" + System.getProperty( (String) key ));
		}
	}
	
}
