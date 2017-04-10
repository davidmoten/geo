package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * Unit tests for {@link Coverage}.
 * 
 * @author dave
 * 
 */
public class CoverageTest {

    @Test
    public void testCoverageHashLength() {
        Coverage coverage = new Coverage(Sets.<String> newHashSet(), 1.0);
        assertEquals(0, coverage.getHashLength());
        // get coverage of toString
        System.out.println(coverage);
    }

    @Test
    public void testCoverageOfAnAreaThatCantBeCoveredWithHashOfLengthOne() {
        Coverage coverage = GeoHash.coverBoundingBox(-5, 100, -45, 170);
        assertEquals(1, coverage.getHashLength());
        assertEquals(Sets.newHashSet("q", "r"), coverage.getHashes());
    }
}
