package com.github.davidmoten.geo.util;

import com.github.davidmoten.geo.Base32;
import com.github.davidmoten.geo.TestingUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreconditionsTest {

    @Test
    public void getCoverageOfConstructorAndCheckConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Preconditions.class);
    }

    @Test
    public void testCheckNotNullGivenNullThrowsException() {
        assertThrows(NullPointerException.class, () -> Preconditions.checkNotNull(null, "message"));
    }

}
