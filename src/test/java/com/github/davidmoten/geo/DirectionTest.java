package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DirectionTest {

	@Test
	public void testOpposite() {
		assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
		assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
		assertEquals(Direction.TOP, Direction.BOTTOM.opposite());
		assertEquals(Direction.BOTTOM, Direction.TOP.opposite());
	}
}
