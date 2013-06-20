package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.github.davidmoten.geo.GeoHash.Direction;
import com.google.common.collect.Sets;

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

	@Test
	public void testAnother() {
		assertEquals("sew1c2vs2q5r", GeoHash.encodeHash(20, 31));
	}

	@Test
	public void testAdjacentBottom() {
		assertEquals("u0zz", GeoHash.adjacentHash("u1pb", Direction.BOTTOM));
	}

	@Test
	public void testAdjacentTop() {
		assertEquals("u1pc", GeoHash.adjacentHash("u1pb", Direction.TOP));
	}

	@Test
	public void testAdjacentLeft() {
		assertEquals("u1p8", GeoHash.adjacentHash("u1pb", Direction.LEFT));
	}

	@Test
	public void testAdjacentRight() {
		assertEquals("u300", GeoHash.adjacentHash("u1pb", Direction.RIGHT));
	}

	@Test
	public void testNeighbouringHashes() {
		String center = "dqcjqc";
		Set<String> neighbours = Sets.newHashSet("dqcjqf", "dqcjqb", "dqcjr1",
				"dqcjq9", "dqcjqd", "dqcjr4", "dqcjr0", "dqcjq8");
		assertEquals(neighbours, Sets.newHashSet(GeoHash.neighbours(center)));
	}

}
