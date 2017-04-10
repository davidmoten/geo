package com.github.davidmoten.geo.util;

import org.junit.Test;

import com.github.davidmoten.geo.TestingUtil;

public class PreconditionsTest {

    @Test
    public void getCoverageOfConstructorAndCheckConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Preconditions.class);
    }

    @Test(expected = NullPointerException.class)
    public void testCheckNotNullGivenNullThrowsException() {
        Preconditions.checkNotNull(null, "message");
    }

}
