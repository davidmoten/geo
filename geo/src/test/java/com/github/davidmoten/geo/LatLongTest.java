package com.github.davidmoten.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link LatLong}.
 * 
 * @author dave
 * 
 */
public class LatLongTest {

    @Test
    public void testToString() {
        assertEquals("LatLong [lat=10.0, lon=20.0]",
                new LatLong(10, 20).toString());
    }

    @Test
    public void testHashCode() {
        float lat = 20.05f;
        float lon = -15.5f;
        LatLong a = new LatLong(lat, lon);
        LatLong b = new LatLong(lat, lon);

        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, b);
    }

}
