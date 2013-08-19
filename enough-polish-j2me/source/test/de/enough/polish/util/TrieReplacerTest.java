package de.enough.polish.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TrieReplacerTest extends TestCase
{
	public void testReplaceMatches()
	{
		TrieReplacer replacer = new TrieReplacer();
		replacer.addReplacement("<1>", "[one]");
		replacer.addReplacement("<2>", "[two]");
		replacer.addReplacement("<3>", "[three]");
		replacer.addReplacement("<4>", "[four]");
		replacer.addReplacement("<12345>", "[numbers]");
		
		String input, output;
		
		input = null;
		output = replacer.replaceMatches(input);
		Assert.assertNull(output);
		
		input = "";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("", output);
		
		input = "<1>";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("[one]", output);

		input = "<1><2>";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("[one][two]", output);

		input = "<1><2><3>";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("[one][two][three]", output);

		input = "abc<<1><2><3>>";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("abc<[one][two][three]>", output);

		input = "front<1><2><3>";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("front[one][two][three]", output);

		input = "front<1><2><3>tail";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("front[one][two][three]tail", output);

		input = "front <1> <2> <3> tail";
		output = replacer.replaceMatches(input);
		Assert.assertEquals("front [one] [two] [three] tail", output);

	}
}
