package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LatLongTest {

    @Test
    public void testToString() {
        assertEquals("LatLong [lat=10.0, lon=20.0]",
                new LatLong(10, 20).toString());
    }

}
