package com.github.davidmoten.geo;

import static com.github.davidmoten.geo.GeoHash.adjacentHash;
import static com.github.davidmoten.geo.GeoHash.bottom;
import static com.github.davidmoten.geo.GeoHash.coverBoundingBox;
import static com.github.davidmoten.geo.GeoHash.coverBoundingBoxMaxHashes;
import static com.github.davidmoten.geo.GeoHash.decodeHash;
import static com.github.davidmoten.geo.GeoHash.encodeHash;
import static com.github.davidmoten.geo.GeoHash.gridAsString;
import static com.github.davidmoten.geo.GeoHash.hashLengthToCoverBoundingBox;
import static com.github.davidmoten.geo.GeoHash.heightDegrees;
import static com.github.davidmoten.geo.GeoHash.left;
import static com.github.davidmoten.geo.GeoHash.neighbours;
import static com.github.davidmoten.geo.GeoHash.right;
import static com.github.davidmoten.geo.GeoHash.top;
import static com.github.davidmoten.geo.GeoHash.widthDegrees;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * Unit tests for {@link GeoHash}.
 * 
 * @author dave
 * 
 */
public class GeoHashTest {

    private static final double HARTFORD_LON = -72.727175;
    private static final double HARTFORD_LAT = 41.842967;
    private static final double SCHENECTADY_LON = -73.950691;
    private static final double SCHENECTADY_LAT = 42.819581;
    private static final double PRECISION = 0.000000001;
    private static final int I_LEFT      = 0;
    private static final int I_RIGHT     = 1;
    private static final int I_TOP       = 2;
    private static final int I_BOTTOM    = 3;
    private static final int I_LEFT_TOP  = 4;
    private static final int I_LEFT_BOT  = 5;
    private static final int I_RIGHT_TOP = 6;
    private static final int I_RIGHT_BOT = 7;
    

    @Test
    public void getCoverageOfPrivateConstructor() {
        TestingUtil.callConstructorAndCheckIsPrivate(GeoHash.class);
    }

    @Test
    public void encodeHashToLong() {
        assertEquals(0x65c0000000000002L, GeoHash.encodeHashToLong(41.842967, -72.727175, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromLongToStringInvalid() {
        GeoHash.fromLongToString(0xff);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromLongToStringZero() {
        GeoHash.fromLongToString(0);
    }

    @Test
    public void testWhiteHouseHashEncode() {
        assertEquals("dqcjqcp84c6e",
                encodeHash(38.89710201881826, -77.03669792041183));
    }

    @Test
    public void testWhiteHouseHashEncodeUsingLatLongObject() {
        assertEquals("dqcjqcp84c6e", encodeHash(new LatLong(38.89710201881826,
                -77.03669792041183)));
    }

    @Test
    public void testWhiteHouseHashDecode() {
        LatLong point = decodeHash("dqcjqcp84c6e");
        assertEquals(point.getLat(), 38.89710201881826, PRECISION);
        assertEquals(point.getLon(), -77.03669792041183, PRECISION);
    }

    @Test
    public void testFromGeoHashDotOrg() {
        assertEquals("6gkzwgjzn820", encodeHash(-25.382708, -49.265506));
    }

    @Test
    public void testHashOfNonDefaultLength() {
        assertEquals("6gkzwg", encodeHash(-25.382708, -49.265506, 6));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashEncodeGivenNonPositiveLength() {
        encodeHash(-25.382708, -49.265506, 0);
    }

    @Test
    public void testAnother() {
        assertEquals("sew1c2vs2q5r", encodeHash(20, 31));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncodeHashWithLatTooBig() {
        encodeHash(1000, 100, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncodeHashWithLatTooSmall() {
        encodeHash(-1000, 100, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdjacentHashThrowsExceptionGivenNullHash() {
        GeoHash.adjacentHash(null, Direction.RIGHT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdjacentHashThrowsExceptionGivenBlankHash() {
        GeoHash.adjacentHash("", Direction.RIGHT);
    }

    @Test
    public void testAdjacentBottom() {
        assertEquals("u0zz", adjacentHash("u1pb", Direction.BOTTOM));
    }

    @Test
    public void testAdjacentTop() {
        assertEquals("u1pc", adjacentHash("u1pb", Direction.TOP));
    }

    @Test
    public void testAdjacentLeft() {
        assertEquals("u1p8", adjacentHash("u1pb", Direction.LEFT));
    }

    @Test
    public void testAdjacentRight() {
        assertEquals("u300", adjacentHash("u1pb", Direction.RIGHT));
    }

    @Test
    public void testLeft() {
        assertEquals("u1p8", left("u1pb"));
    }

    @Test
    public void testRight() {
        assertEquals("u300", right("u1pb"));
    }

    @Test
    public void testTop() {
        assertEquals("u1pc", top("u1pb"));
    }

    @Test
    public void testBottom() {
        assertEquals("u0zz", bottom("u1pb"));
    }

    @Test
    public void testNeighbouringHashes() {
        String center = "dqcjqc";
        Set<String> neighbours = Sets.newHashSet("dqcjqf", "dqcjqb", "dqcjr1",
                "dqcjq9", "dqcjqd", "dqcjr4", "dqcjr0", "dqcjq8");
        assertEquals(neighbours, Sets.newHashSet(neighbours(center)));
    }

    @Test
    public void testHashDecodeOnBlankString() {
        LatLong point = decodeHash("");
        assertEquals(0, point.getLat(), PRECISION);
        assertEquals(0, point.getLon(), PRECISION);
    }

//    @Test
    public void testSpeed() {
        long t = System.currentTimeMillis();
        int numIterations = 10000;
        for (int i = 0; i < numIterations; i++)
            encodeHash(38.89710201881826, -77.03669792041183);
        double numPerSecond = numIterations / (System.currentTimeMillis() - t)
                * 1000;
        System.out.println("num encodeHash per second=" + numPerSecond);
    }

    @Test
    public void testMovingHashCentreUpByGeoHashHeightGivesAdjacentHash() {
        String fullHash = "dqcjqcp84c6e";
        for (int i = 1; i <= GeoHash.MAX_HASH_LENGTH; i++) {
            String hash = fullHash.substring(0, i);
            String top = top(hash);
            double d = heightDegrees(hash.length());
            assertEquals(top,
                    encodeHash(decodeHash(hash).add(d, 0), hash.length()));
        }
    }

    @Test
    public void testMovingHashCentreUpBySlightlyMoreThanHalfGeoHashHeightGivesAdjacentHash() {
        String fullHash = "dqcjqcp84c6e";
        for (int i = 1; i <= GeoHash.MAX_HASH_LENGTH; i++) {
            String hash = fullHash.substring(0, i);
            String top = top(hash);
            double d = heightDegrees(hash.length());
            assertEquals(
                    top,
                    encodeHash(decodeHash(hash).add(d / 2 * 1.01, 0),
                            hash.length()));
        }
    }

    @Test
    public void testMovingHashCentreUpBySlightlyLessThanHalfGeoHashHeightGivesSameHash() {
        String fullHash = "dqcjqcp84c6e";
        for (int i = 1; i <= GeoHash.MAX_HASH_LENGTH; i++) {
            String hash = fullHash.substring(0, i);
            double d = heightDegrees(hash.length());
            assertEquals(
                    hash,
                    encodeHash(decodeHash(hash).add(d / 2 * 0.99, 0),
                            hash.length()));
        }
    }

    @Test
    public void testMovingHashCentreRightByGeoHashWidthGivesAdjacentHash() {
        String fullHash = "dqcjqcp84c6e";
        for (int i = 1; i <= GeoHash.MAX_HASH_LENGTH; i++) {
            String hash = fullHash.substring(0, i);
            String right = right(hash);
            double d = widthDegrees(hash.length());
            assertEquals(right,
                    encodeHash(decodeHash(hash).add(0, d), hash.length()));
        }
    }

    @Test
    public void testMovingHashCentreRightBySlighltyMoreThanHalfGeoHashWidthGivesAdjacentHash() {
        String fullHash = "dqcjqcp84c6e";
        for (int i = 1; i <= GeoHash.MAX_HASH_LENGTH; i++) {
            String hash = fullHash.substring(0, i);
            String right = right(hash);
            double d = widthDegrees(hash.length());
            assertEquals(
                    right,
                    encodeHash(decodeHash(hash).add(0, d / 2 * 1.01),
                            hash.length()));
        }
    }

    @Test
    public void testMovingHashCentreRightBySlightlyLessThanHalfGeoHashWidthGivesAdjacentHash() {
        String fullHash = "dqcjqcp84c6e";
        for (int i = 1; i <= GeoHash.MAX_HASH_LENGTH; i++) {
            String hash = fullHash.substring(0, i);
            double d = widthDegrees(hash.length());
            assertEquals(
                    hash,
                    encodeHash(decodeHash(hash).add(0, d / 2 * 0.99),
                            hash.length()));
        }
    }

    /**
     * <p>
     * Use this <a href=
     * "http://www.lucenerevolution.org/sites/default/files/Lucene%20Rev%20Preso%20Smiley%20Spatial%20Search.pdf"
     * >link</a> for double-checking.
     * </p>
     */
    @Test
    public void testCoverBoundingBoxWithHashLength4AroundBoston() {

        Set<String> hashes = coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON,
                HARTFORD_LAT, HARTFORD_LON, 4).getHashes();

        // check schenectady hash
        assertEquals("dre7", encodeHash(SCHENECTADY_LAT, SCHENECTADY_LON, 4));
        // check hartford hash
        assertEquals("drkq", encodeHash(HARTFORD_LAT, HARTFORD_LON, 4));
        assertEquals("drs7", encodeHash(SCHENECTADY_LAT, HARTFORD_LON, 4));
        assertEquals("dr7q", encodeHash(HARTFORD_LAT, SCHENECTADY_LON, 4));

        // check neighbours
        assertEquals("drs", adjacentHash("dre", Direction.RIGHT));
        assertEquals("dr7", adjacentHash("dre", Direction.BOTTOM));
        assertEquals("drk", adjacentHash("drs", Direction.BOTTOM));

        System.out.println(gridAsString("dreb", 5, hashes));

        // check corners are in
        assertTrue(hashes.contains("dre7"));
        assertTrue(hashes.contains("drkq"));
        assertTrue(hashes.contains("drs7"));
        assertTrue(hashes.contains("dr7q"));
        String expected = "dre7,dree,dreg,drs5,drs7,dre6,dred,dref,drs4,drs6,dre3,dre9,drec,drs1,drs3,dre2,dre8,dreb,drs0,drs2,dr7r,dr7x,dr7z,drkp,drkr,dr7q,dr7w,dr7y,drkn,drkq";
        TreeSet<String> ex = Sets
                .newTreeSet(Arrays.asList(expected.split(",")));
        System.out.println(ex);
        System.out.println(hashes);
        assertEquals(ex, hashes);
    }

    @Test
    public void testCoverBoundingBoxWithHashLengthOneAroundBoston() {
        Coverage coverage = coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON,
                HARTFORD_LAT, HARTFORD_LON, 1);
        assertEquals(Sets.newHashSet("d"), coverage.getHashes());
        assertEquals(1, coverage.getHashLength());
        System.out.println(coverage.getRatio());
        assertEquals(1694.6984366342194, coverage.getRatio(), PRECISION);
    }

    @Test
    public void testCoverBoundingBoxWithOptimalHashLengthAroundBoston() {
        Coverage coverage = coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON,
                HARTFORD_LAT, HARTFORD_LON);
        assertEquals(4, coverage.getHashes().size());
        assertEquals(3, coverage.getHashLength());
    }

    @Test
    public void testCoverBoundingBoxWithMaxHashesThrowsException() {
        Coverage coverage = coverBoundingBoxMaxHashes(SCHENECTADY_LAT,
                SCHENECTADY_LON, HARTFORD_LAT, HARTFORD_LON, 0);
        assertNull(coverage);
    }

    @Test
    public void testCoverBoundingBoxWithMaxHashesIsOne() {
        Coverage coverage = coverBoundingBoxMaxHashes(SCHENECTADY_LAT,
                SCHENECTADY_LON, HARTFORD_LAT, HARTFORD_LON, 1);
        assertEquals(1, coverage.getHashes().size());
        assertEquals(2, coverage.getHashLength());
    }

    @Test
    public void testCoverBoundingBoxWithMaxHashesReturnsMoreThanMax() {
        Coverage coverage = coverBoundingBoxMaxHashes(SCHENECTADY_LAT,
                SCHENECTADY_LON, SCHENECTADY_LAT - 0.000000001,
                SCHENECTADY_LON + 0.000000001, Integer.MAX_VALUE);
        assertEquals(GeoHash.MAX_HASH_LENGTH, coverage.getHashLength());
    }

    @Test
    public void testDisplayOfCoverages() {
        for (int i = 1; i <= 6; i++) {
            Coverage coverage = coverBoundingBox(SCHENECTADY_LAT,
                    SCHENECTADY_LON, HARTFORD_LAT, HARTFORD_LON, i);
            System.out.println("length="
                    + i
                    + ",numHashes="
                    + coverage.getHashes().size()
                    + ", ratio="
                    + coverage.getRatio()
                    + ", processingTimeFactor="
                    + (Math.pow(coverage.getHashes().size(), 2)
                            * widthDegrees(i) * heightDegrees(i)));
        }
    }

    @Test
    public void testEnclosingHashLengthAroundBoston() {
        int length = hashLengthToCoverBoundingBox(SCHENECTADY_LAT,
                SCHENECTADY_LON, HARTFORD_LAT, HARTFORD_LON);
        Set<String> hashes = coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON,
                HARTFORD_LAT, HARTFORD_LON, length).getHashes();
        assertEquals(Sets.newHashSet("dr"), hashes);
    }

    @Test
    public void testCoverBoundingBoxWithHashLength3AroundBoston() {
        Set<String> hashes = coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON,
                HARTFORD_LAT, HARTFORD_LON, 3).getHashes();
        assertEquals(Sets.newHashSet("dr7", "dre", "drk", "drs"), hashes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCoverBoundingBoxWithZeroLengthThrowsException() {
        coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON, HARTFORD_LAT,
                HARTFORD_LON, 0);
    }
    
    @Test
    public void testGeoHashLengthAcrossLongitude180() {
        assertEquals(0, hashLengthToCoverBoundingBox(71.676351, 178.389963, 70.633291,
                -177.116629));
    }

    @Test
    public void testGeoHashLengthAcrossEcuador() {
        assertEquals(0, hashLengthToCoverBoundingBox(2.935289, -82.751538, -5.623710,
                        -74.753492));
    }

    @Test
    public void testGeoHashLengthAcrossSouthPole() {
        assertEquals(0, hashLengthToCoverBoundingBox(-80, 10, 80,
                        20));
    }

    @Test
    public void testGeoHashWidthDegrees() {
        encodeHash(-25.382708, -49.265506, 6);
        encodeHash(-25.382708, -49.265506, 5);
        encodeHash(-25.382708, -49.265506, 4);
        encodeHash(-25.382708, -49.265506, 3);
        encodeHash(-25.382708, -49.265506, 2);
        encodeHash(-25.382708, -49.265506, 1);
        assertEquals(45.0, widthDegrees(1), 0.00001);

        assertEquals(11.25, widthDegrees(2), 0.00001);
        assertEquals(1.40625, widthDegrees(3), 0.00001);
        assertEquals(0.3515625, widthDegrees(4), 0.00001);
        assertEquals(0.0439453125, widthDegrees(5), 0.00001);
        assertEquals(0.010986328125, widthDegrees(6), 0.00001);
    }

    @Test
    public void testGeoHashHeightDegrees() {
        encodeHash(-25.382708, -49.265506, 6);
        encodeHash(-25.382708, -49.265506, 5);
        encodeHash(-25.382708, -49.265506, 4);
        encodeHash(-25.382708, -49.265506, 3);
        encodeHash(-25.382708, -49.265506, 2);
        encodeHash(-25.382708, -49.265506, 1);
        assertEquals(45.0, heightDegrees(1), 0.00001);

        assertEquals(11.25 / 2, heightDegrees(2), 0.00001);
        assertEquals(1.40625, heightDegrees(3), 0.00001);
        assertEquals(0.3515625 / 2, heightDegrees(4), 0.00001);
        assertEquals(0.0439453125, heightDegrees(5), 0.00001);
        assertEquals(0.010986328125 / 2, heightDegrees(6), 0.00001);
    }

    @Test
    public void testHeightDegreesForLengthEqualsZero() {
        assertEquals(180.0, heightDegrees(0), PRECISION);
    }

    @Test
    public void testHeightDegreesForLengthGreaterThanMax() {
        assertEquals(4.190951585769653e-8, heightDegrees(13), PRECISION);
    }

    @Test
    public void testWidthDegreesForLengthEqualsZero() {
        assertEquals(360.0, widthDegrees(0), PRECISION);
    }

    @Test
    public void testWidthDegreesForLengthGreaterThanMax() {
        assertEquals(4.190951585769653e-8, widthDegrees(13), PRECISION);
    }

    @Test
    public void testGridToString() {
        System.out.println(gridAsString("dred", -5, -5, 5, 5));
        System.out.println(gridAsString("dr", 1,
                Collections.<String> emptySet()));
    }

    @Test
    public void testHashContains() {
        LatLong centre = decodeHash("dre7");
        assertTrue(GeoHash.hashContains("dre7", centre.getLat(),
                centre.getLon()));
        assertFalse(GeoHash.hashContains("dre7", centre.getLat() + 20,
                centre.getLon()));
        assertFalse(GeoHash.hashContains("dre7", centre.getLat(),
                centre.getLon() + 20));
    }

    @Test
    public void testHashContainsNearLongitudeBoundary() {
        String hash = encodeHash(-25, -179, 1);
        assertFalse(GeoHash.hashContains(hash, -25, 179));
        assertTrue(GeoHash.hashContains(hash, -25, -178));
    }

    @Test
    public void testHashLengthToEncloseBoundingBoxReturns0IfBoxTooBig() {
        assertEquals(0,
                GeoHash.hashLengthToCoverBoundingBox(80, -170, -80, 170));
    }

    @Test
    public void testRightNeighbourCloseTo180Longitude() {
        assertEquals("2", GeoHash.adjacentHash("r", Direction.RIGHT));
    }

    @Test
    public void testLeftNeighbourCloseToMinus180Longitude() {
        assertEquals("r", GeoHash.adjacentHash("2", Direction.LEFT));
    }

    @Test
    public void testTopNeighbourCloseToNorthPole() {
        String hash = GeoHash.encodeHash(90, 0, 1);
        assertEquals("u", hash);
        assertEquals("b", GeoHash.adjacentHash(hash, Direction.TOP));
    }

    @Test
    public void testBottomNeighbourCloseToSouthPole() {
        String hash = GeoHash.encodeHash(-90, 0, 1);
        assertEquals("h", hash);
        assertEquals("0", GeoHash.adjacentHash(hash, Direction.BOTTOM));
    }

    // Nice map here at poles
    // http://www.bigdatamodeling.org/2013/01/intuitive-geohash.html
    @Test
    public void testNeighboursAtSouthPole() {
        String poleHash = GeoHash.encodeHash(-90, 0);
        assertEquals("h00000000000", poleHash);

        List<String> neighbors = GeoHash.neighbours(poleHash);
        assertEquals(8, neighbors.size());

        assertEquals("5bpbpbpbpbpb", neighbors.get(I_LEFT));
        assertEquals("h00000000002", neighbors.get(I_RIGHT));
        assertEquals("h00000000001", neighbors.get(I_TOP));
        assertEquals("00000000000p", neighbors.get(I_BOTTOM));
        assertEquals("5bpbpbpbpbpc", neighbors.get(I_LEFT_TOP));
        assertEquals("pbpbpbpbpbpz", neighbors.get(I_LEFT_BOT));
        assertEquals("h00000000003", neighbors.get(I_RIGHT_TOP));
        assertEquals("00000000000r", neighbors.get(I_RIGHT_BOT));
    }

    @Test
    public void testNeighboursAtNorthPole() {
        String poleHash = GeoHash.encodeHash(90, 0);
        assertEquals("upbpbpbpbpbp", poleHash);
        
        List<String> neighbors = GeoHash.neighbours(poleHash);
        assertEquals(8, neighbors.size());

        assertEquals("gzzzzzzzzzzz", neighbors.get(I_LEFT));
        assertEquals("upbpbpbpbpbr", neighbors.get(I_RIGHT));
        assertEquals("bpbpbpbpbpb0", neighbors.get(I_TOP));
        assertEquals("upbpbpbpbpbn", neighbors.get(I_BOTTOM));
        assertEquals("zzzzzzzzzzzb", neighbors.get(I_LEFT_TOP));
        assertEquals("gzzzzzzzzzzy", neighbors.get(I_LEFT_BOT));
        assertEquals("bpbpbpbpbpb2", neighbors.get(I_RIGHT_TOP));
        assertEquals("upbpbpbpbpbq", neighbors.get(I_RIGHT_BOT));
    }

    @Test
    public void testNeighboursAtLongitude180() {
        String hash = GeoHash.encodeHash(0, 180);
        assertEquals("xbpbpbpbpbpb", hash);
        
        List<String> neighbors = GeoHash.neighbours(hash);
        assertEquals(8, neighbors.size());

        assertEquals("xbpbpbpbpbp8", neighbors.get(I_LEFT));
        assertEquals("800000000000", neighbors.get(I_RIGHT));
        assertEquals("xbpbpbpbpbpc", neighbors.get(I_TOP));
        assertEquals("rzzzzzzzzzzz", neighbors.get(I_BOTTOM));
        assertEquals("xbpbpbpbpbp9", neighbors.get(I_LEFT_TOP));
        assertEquals("rzzzzzzzzzzx", neighbors.get(I_LEFT_BOT));
        assertEquals("800000000001", neighbors.get(I_RIGHT_TOP));
        assertEquals("2pbpbpbpbpbp", neighbors.get(I_RIGHT_BOT));
    }

    @Test
    public void testNeighboursAtLongitudeMinus180() {
        String hash = GeoHash.encodeHash(0, -180);
        assertEquals("800000000000", hash);
        
        List<String> neighbors = GeoHash.neighbours(hash);
        System.out.println(neighbors);
        assertEquals(8, neighbors.size());

        assertEquals("xbpbpbpbpbpb", neighbors.get(I_LEFT));
        assertEquals("800000000002", neighbors.get(I_RIGHT));
        assertEquals("800000000001", neighbors.get(I_TOP));
        assertEquals("2pbpbpbpbpbp", neighbors.get(I_BOTTOM));
        assertEquals("xbpbpbpbpbpc", neighbors.get(I_LEFT_TOP));
        assertEquals("rzzzzzzzzzzz", neighbors.get(I_LEFT_BOT));
        assertEquals("800000000003", neighbors.get(I_RIGHT_TOP));
        assertEquals("2pbpbpbpbpbr", neighbors.get(I_RIGHT_BOT));
    }

    
    @Test(expected=IllegalArgumentException.class)
    public void testCoverBoundingBoxPreconditionLat() {
        GeoHash.coverBoundingBox(0, 100, 10, 120);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCoverBoundingBoxPreconditionLon() {
        GeoHash.coverBoundingBox(10, 120, 0, 100);
    }
}
