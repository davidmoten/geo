package com.github.davidmoten.geo.mem;

import org.junit.Test;

public class GeomemTest {

    private static final double topLeftLat = -5;
    private static final double topLeftLong = 100;
    private static final double bottomRightLat = -45;
    private static final double bottomRightLong = 170;

    @Test
    public void testGeomemFindWhenNoData() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.find(topLeftLat, topLeftLong, bottomRightLat, bottomRightLong, 0,
                1000);
    }

}
