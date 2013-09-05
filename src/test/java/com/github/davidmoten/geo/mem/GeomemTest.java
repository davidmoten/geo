package com.github.davidmoten.geo.mem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
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
    public void testRegionFilter() {

        Geomem<String, String> g = new Geomem<String, String>();
        Predicate<Info<String, String>> predicate = g.createRegionFilter(
                topLeftLat, topLeftLong, bottomRightLat, bottomRightLong);
        {
            // inside
            assertTrue(predicate.apply(createInfo(topLeftLat - 1,
                    topLeftLong + 1)));
            // outside north
            assertFalse(predicate.apply(createInfo(topLeftLat + 1,
                    topLeftLong + 1)));
            // outside west
            assertFalse(predicate.apply(createInfo(topLeftLat - 1,
                    topLeftLong - 1)));
            // outside east
            assertFalse(predicate.apply(createInfo(topLeftLat - 1,
                    bottomRightLong + 1)));
            // outside south
            assertFalse(predicate.apply(createInfo(bottomRightLat - 1,
                    bottomRightLong - 1)));
        }

    }

    private Info<String, String> createInfo(double lat, double lon) {
        return new Info<String, String>(lat, lon, 100, "A", Optional.of("A"));
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
        // get coverage of toString
        System.out.println(list.get(0));
    }

    @Test
    public void testGeomemFindWhenOneEntryInsideRegionUsingAlternativeAddMethod() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.add(-15, 120, 500, "A1");
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        assertEquals(1, list.size());
        assertEquals(-15, list.get(0).lat(), PRECISION);
        assertEquals(120, list.get(0).lon(), PRECISION);
        assertEquals(500L, list.get(0).time());
        assertEquals("A1", list.get(0).value());
        assertEquals("A1", list.get(0).id().get());
        // get coverage of toString
        System.out.println(list.get(0));
    }

    @Test
    public void testGeomemFindWhenOneEntryInsideRegionUsingAlternativeAddMethod2() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.add(-15, 120, 500, "A1", Optional.<String> absent());
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        assertEquals(1, list.size());
        assertEquals(-15, list.get(0).lat(), PRECISION);
        assertEquals(120, list.get(0).lon(), PRECISION);
        assertEquals(500L, list.get(0).time());
        assertEquals("A1", list.get(0).value());
        assertFalse(list.get(0).id().isPresent());
        // get coverage of toString
        System.out.println(list.get(0));
    }

    @Test
    public void testGeomemFindWhenOneEntryOutsideRegion() {
        Geomem<String, String> g = new Geomem<String, String>();
        g.add(15, 120, 500, "A1", "a1");
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        assertTrue(list.isEmpty());
    }

    /**
     * Indicates 4.5MB per 1000 records. Thus one million entries needs 4500
     * entries.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGeomemManyEntries() throws InterruptedException {
        System.out.println("maxMemory=" + Runtime.getRuntime().maxMemory()
                / 1048576 + "MB");
        Geomem<String, String> g = new Geomem<String, String>();
        System.gc();
        Thread.sleep(100);
        reportMemoryUsage();
        for (int i = 0; i < 10000; i++) {
            if (i % 1000 == 0) {
                System.out.println("count=" + i);
                reportMemoryUsage();
            }
            addRandomEntry(g);
        }
        reportMemoryUsage();
        System.gc();
        Thread.sleep(100);
        reportMemoryUsage();
        List<Info<String, String>> list = Lists.newArrayList(g.find(topLeftLat,
                topLeftLong, bottomRightLat, bottomRightLong, 0, 1000));
        System.out.println(list.size());

    }

    private void reportMemoryUsage() {
        System.out.println("memUsed="
                + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                        .freeMemory()) / 1048576 + "MB");
    }

    private void addRandomEntry(Geomem<String, String> g) {
        double lat = GeomemTest.topLeftLat + 5 - Math.random() * 40;
        double lon = GeomemTest.topLeftLong - 5 + Math.random() * 80;
        long t = Math.round(Math.random() * 1200);
        String id = UUID.randomUUID().toString().substring(0, 2);
        g.add(lat, lon, t, id, id);
    }
}
