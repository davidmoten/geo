package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base32Test {

	@Test
	public void testEncodePositiveInteger() {
		assertEquals("15pn7", Base32.base32(1234567));
	}

	@Test
	public void testEncodesZero() {
		assertEquals("0", Base32.base32(0));
	}

	@Test
	public void testEncodesNegativeInteger() {
		assertEquals("-3v", Base32.base32(-123));
	}

	@Test
	public void testInstantiation() {
		Base32.instantiate();
	}

}
