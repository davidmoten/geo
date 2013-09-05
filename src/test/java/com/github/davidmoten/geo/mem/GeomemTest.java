package com.github.davidmoten.geo.mem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

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

    @Test
    public void testGeomemManyEntries() {
        System.out.println("maxMemory=" + Runtime.getRuntime().maxMemory()
                / 1048576 + "MB");
        Geomem<String, String> g = new Geomem<String, String>();
        for (int i = 0; i < 10000; i++) {
            if (i % 1000 == 0)
                System.out.println("count="
                        + i
                        + " memUsed="
                        + (Runtime.getRuntime().totalMemory() - Runtime
                                .getRuntime().freeMemory()) / 1048576 + "MB");
            addRandomEntry(g);
        }
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        System.out.println(list.size());
    }

    private void addRandomEntry(Geomem<String, String> g) {
        double lat = GeomemTest.topLeftLat + 5 - Math.random() * 40;
        double lon = GeomemTest.topLeftLong - 5 + Math.random() * 80;
        long t = Math.round(Math.random() * 1200);
        String id = UUID.randomUUID().toString().substring(0, 2);
        g.add(lat, lon, t, id, id);
    }
}
