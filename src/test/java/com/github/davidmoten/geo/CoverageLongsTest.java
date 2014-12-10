package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link CoverageLongs}.
 *
 */
public class CoverageLongsTest {

    @Test
    public void testCoverageLongsHashLength() {
        CoverageLongs coverage = new CoverageLongs(new long[] {}, 0, 1.0);
        assertEquals(0, coverage.getHashLength());
        // get coverage of toString
        System.out.println(coverage);
    }

    @Test
    public void testCoverageLongsOfAnAreaThatCantBeCoveredWithHashOfLengthOne() {
        CoverageLongs coverage = GeoHash.coverBoundingBoxLongs(-5, 100, -45, 170, 1);
        assertEquals(1, coverage.getHashLength());
    }
}
