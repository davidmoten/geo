package com.github.davidmoten.geo;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /**
     * test copied from https://github.com/davidmoten/geo/pull/25/files to validate bug fix
     */
    @Test
    public void testWideCoverage() {
        double top = -1;
        double left = -26;
        double bottom = -2;
        double right = 175;

        Coverage coverage = GeoHash.coverBoundingBox(top, left, bottom, right, 1);
        assertTrue(coverage.getHashes().contains("r"));
    }

    /**
     * test copied from https://github.com/davidmoten/geo/issues/34 to validate bug fix
     */
    @Test
    public void testCoverageAllWorld() {
        double topLeftLat = 90d;
        double topLeftLon = -179d;
        double bottomRightLat = -90d;
        double bottomRightLon = 180d;
        Coverage coverage = GeoHash.coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, 1);
        assertEquals(Sets.newHashSet("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
                coverage.getHashes());
    }

    @Test
    public void testCoverageAllWorldLeaflet() {
        double topLeftLat = 90;
        double topLeftLon = -703;
        double bottomRightLat = -90;
        double bottomRightLon = 624;
        Coverage coverage = GeoHash.coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, 1);
        assertEquals(Sets.newHashSet("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
                coverage.getHashes());
    }

    @Test
    public void testCoverageAntimeridianGoogleMaps() {
        double topLeftLat = 39;
        double topLeftLon = 156;
        double bottomRightLat = 3;
        double bottomRightLon = -118;
        Coverage coverage = GeoHash.coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, 1);
        assertEquals(Sets.newHashSet("x", "8", "9"), coverage.getHashes());
    }

    @Test
    public void testCoverageAntimeridianLeaflet() {
        double topLeftLat = 39;
        double topLeftLon = -204;
        double bottomRightLat = 2;
        double bottomRightLon = -121;
        Coverage coverage = GeoHash.coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, 1);
        assertEquals(Sets.newHashSet("x", "8", "9"), coverage.getHashes());
    }

    @Test
    public void testCoverageAntimeridianLeaflet2() {
        double topLeftLat = 44;
        double topLeftLon = 110;
        double bottomRightLat = 9;
        double bottomRightLon = 194;
        Coverage coverage = GeoHash.coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, 1);
        assertEquals(Sets.newHashSet("w", "x", "8"), coverage.getHashes());
    }

    @Test
    public void testCoverageMaxHashes() {
        double topLeftLat = 50.1112;
        double topLeftLon = -6.8167;
        double bottomRightLat = 46.6997;
        double bottomRightLon = 3.7301;
        Coverage coverage = GeoHash.coverBoundingBoxMaxHashes(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon, 64);
        assertEquals(3, coverage.getHashLength());
        assertEquals(24, coverage.getHashes().size());
    }
}
