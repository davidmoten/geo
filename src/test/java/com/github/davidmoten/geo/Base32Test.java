package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base32Test {

	@Test
	public void testEncode() {
		assertEquals("15pn7", Base32.base32(1234567));
	}

}
