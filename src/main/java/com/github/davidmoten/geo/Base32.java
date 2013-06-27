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

	private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
			'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private final static HashMap<Character, Integer> lookup = new HashMap<Character, Integer>();
	static {
		int i = 0;
		for (char c : digits)
			lookup.put(c, i++);
	}

	/**
	 * Returns the base 32 encoding used by geohashing from a long value.
	 * 
	 * @param i
	 * @return
	 */
	public static String encodeBase32(long i) {
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

	/**
	 * Returns the conversion of a base32 string to a long.
	 * 
	 * @param s
	 * @return
	 */
	public static long decodeBase32(String s) {
		boolean isNegative = s.startsWith("-");
		int startIndex = isNegative ? 1 : 0;
		long base = 1;
		long result = 0;
		for (int i = s.length() - 1; i >= startIndex; i--) {
			int j = getCharIndex(s.charAt(i));
			result = result + base * j;
			base = base * 32;
		}
		if (isNegative)
			result *= -1;
		return result;
	}

	/**
	 * Returns the index in the digits array of the character ch. Throws an
	 * {@link IllegalArgumentException} if the character is not found in the
	 * array.
	 * 
	 * @param ch
	 * @return
	 */
	@VisibleForTesting
	static int getCharIndex(char ch) {
		// TODO not very performant this method. Use a map?
		for (int i = 0; i < digits.length; i++)
			if (ch == digits[i])
				return i;
		throw new IllegalArgumentException("not a base32 character: " + ch);
	}

}