package com.github.davidmoten.geo.mem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class GeomemTest {

    private static final double topLeftLat = -5;
    private static final double topLeftLong = 100;
    private static final double bottomRightLat = -45;
    private static final double bottomRightLong = 170;
    private static final double PRECISION = 0.00001;

    @Test
    public void testGeomemFindWhenNoData() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.find(topLeftLat, topLeftLong, bottomRightLat, bottomRightLong, 0,
                1000);
    }

    @Test
    public void testGeomemFindWhenOneEntryInsideRegion() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.add(-15, 120, 500, "A1", "a1");
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        assertEquals(1, list.size());
        assertEquals(-15, list.get(0).lat(), PRECISION);
        assertEquals(120, list.get(0).lon(), PRECISION);
        assertEquals(500L, list.get(0).time());
        assertEquals("A1", list.get(0).value());
        assertEquals("a1", list.get(0).id().get());
    }

    @Test
    public void testGeomemFindWhenOneEntryOutsideRegion() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.add(15, 120, 500, "A1", "a1");
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        assertTrue(list.isEmpty());
    }

}
