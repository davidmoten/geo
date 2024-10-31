package com.github.davidmoten.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link Direction}.
 * 
 * @author dave
 * 
 */
public class DirectionTest {

    @Test
    public void testOpposite() {
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
        assertEquals(Direction.TOP, Direction.BOTTOM.opposite());
        assertEquals(Direction.BOTTOM, Direction.TOP.opposite());
    }
}
