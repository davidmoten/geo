package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoHashTest {

	@Test
	public void testWhiteHouseHashEncode() {
		assertEquals("dqcjqcp84c6e",
				GeoHash.encodeHash(38.89710201881826, -77.03669792041183));
	}

	@Test
	public void testFromGeoHashDotOrg() {
		assertEquals("6gkzwgjzn820", GeoHash.encodeHash(-25.382708, -49.265506));
	}

	@Test
	public void testPrecision() {
		assertEquals("6gkzwg", GeoHash.encodeHash(-25.382708, -49.265506, 6));
	}
}
