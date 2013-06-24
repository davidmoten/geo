package com.github.davidmoten.geo;

import java.util.HashMap;

import com.google.common.annotations.VisibleForTesting;

/**
 * Conversion methods between long values and geohash-style base 32 encoding.
 * 
 * @author dave
 * 
 */
public final class Base32 {

	/**
	 * Constructor.
	 */
	private Base32() {
		// prevent instantiation.
	}

	@VisibleForTesting
	static void instantiate() {
		// ensure full code coverage
		new Base32();
	}

	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	final static HashMap<Character, Integer> lookup = new HashMap<Character, Integer>();
	static {
		int i = 0;
		for (char c : digits)
			lookup.put(c, i++);
	}

	public static String base32(long i) {
		char[] buf = new char[65];
		int charPos = 64;
		boolean negative = (i < 0);
		if (!negative)
			i = -i;
		while (i <= -32) {
			buf[charPos--] = digits[(int) (-(i % 32))];
			i /= 32;
		}
		buf[charPos] = digits[(int) (-i)];
		if (negative)
			buf[--charPos] = '-';
		return new String(buf, charPos, (65 - charPos));
	}

}