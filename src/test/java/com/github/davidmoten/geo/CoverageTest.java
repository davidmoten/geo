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
    }

}
