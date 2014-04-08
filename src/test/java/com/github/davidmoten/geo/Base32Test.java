package com.github.davidmoten.geo;

import static com.github.davidmoten.geo.Base32.decodeBase32;
import static com.github.davidmoten.geo.Base32.encodeBase32;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link Base32}.
 * 
 * @author dave
 * 
 */
public class Base32Test {

	@Test
	public void testEncodePositiveInteger() {
		assertEquals("15pn7", encodeBase32(1234567, 5));
	}

	@Test
	public void testEncodesZero() {
		assertEquals("0", encodeBase32(0, 1));
	}

	@Test
	public void testEncodesNegativeInteger() {
		assertEquals("-3v", encodeBase32(-123, 2));
	}

	@Test
	public void testDecodeToPositiveInteger() {
		assertEquals(1234567, decodeBase32("15pn7"));
	}

	@Test
	public void testDecodeToZero() {
		assertEquals("0", encodeBase32(0, 1));
		assertEquals(0, decodeBase32("0"));
	}

	@Test
	public void testDecodeThenEncodeIsIdentity() {
		assertEquals("1000", encodeBase32(decodeBase32("1000"), 4));
	}

	@Test
	public void testDecodeManyZeros() {
		assertEquals(0, decodeBase32("0000000"));
	}

	@Test
	public void testDecodeOneZero() {
		assertEquals(0, decodeBase32("0"));
	}

	@Test
	public void testDecodeToNegativeInteger() {
		assertEquals("-3v", encodeBase32(-123, 2));
		assertEquals(-123, decodeBase32("-3v"));
	}

	@Test
	public void testEncodePadsToLength12() {
		assertEquals("00000000003v", encodeBase32(123));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCharIndexThrowsExceptionWhenNonBase32CharacterGiven() {
		Base32.getCharIndex('?');
	}

	@Test
	public void getCoverageOfConstructorAndCheckConstructorIsPrivate() {
		TestingUtil.callConstructorAndCheckIsPrivate(Base32.class);
	}

}
