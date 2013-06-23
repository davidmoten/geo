package com.github.davidmoten.geo;

import static com.github.davidmoten.geo.GeoHash.adjacentHash;
import static com.github.davidmoten.geo.GeoHash.bottom;
import static com.github.davidmoten.geo.GeoHash.coverBoundingBox;
import static com.github.davidmoten.geo.GeoHash.decodeHash;
import static com.github.davidmoten.geo.GeoHash.encodeHash;
import static com.github.davidmoten.geo.GeoHash.gridToString;
import static com.github.davidmoten.geo.GeoHash.hashLengthToEncloseBoundingBox;
import static com.github.davidmoten.geo.GeoHash.heightDegrees;
import static com.github.davidmoten.geo.GeoHash.instantiate;
import static com.github.davidmoten.geo.GeoHash.left;
import static com.github.davidmoten.geo.GeoHash.longitudeDiff;
import static com.github.davidmoten.geo.GeoHash.neighbours;
import static com.github.davidmoten.geo.GeoHash.right;
import static com.github.davidmoten.geo.GeoHash.to180;
import static com.github.davidmoten.geo.GeoHash.top;
import static com.github.davidmoten.geo.GeoHash.widthDegrees;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.google.common.collect.Sets;

public class GeoHashTest {

	private static final double HARTFORD_LON = -72.727175;
	private static final double HARTFORD_LAT = 41.842967;
	private static final double SCHENECTADY_LON = -73.950691;
	private static final double SCHENECTADY_LAT = 42.819581;
	private static final double PRECISION = 0.000000001;

	@Test
	public void testWhiteHouseHashEncode() {
		assertEquals("dqcjqcp84c6e",
				encodeHash(38.89710201881826, -77.03669792041183));
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

	@Test
	public void testInstantiation() {
		instantiate();
	}

	@Test
	public void testSpeed() {
		long t = System.currentTimeMillis();
		int numIterations = 100000;
		for (int i = 0; i < numIterations; i++)
			encodeHash(38.89710201881826, -77.03669792041183);
		double numPerSecond = numIterations / (System.currentTimeMillis() - t)
				* 1000;
		System.out.println("num encodeHash per second=" + numPerSecond);
	}

	@Test
	public void testMovingHashCentreUpByGeoHashHeightGivesAdjacentHash() {
		String hash = "drt2";
		String top = top(hash);
		double d = heightDegrees(hash.length());
		assertEquals(top, encodeHash(decodeHash(hash).add(d, 0), hash.length()));
		assertEquals(top,
				encodeHash(decodeHash(hash).add(d / 2 + 0.1, 0), hash.length()));
		assertEquals(hash,
				encodeHash(decodeHash(hash).add(d / 2 - 0.1, 0), hash.length()));
	}

	@Test
	public void testMovingHashCentreUpBySlightlyMoreThanHalfGeoHashHeightGivesAdjacentHash() {
		String hash = "drt2";
		String top = top(hash);
		double d = heightDegrees(hash.length());
		assertEquals(top,
				encodeHash(decodeHash(hash).add(d / 2 + 0.1, 0), hash.length()));
	}

	@Test
	public void testMovingHashCentreUpBySlightlyLessThanHalfGeoHashHeightGivesSameHash() {
		String hash = "drt2";
		double d = heightDegrees(hash.length());
		assertEquals(hash,
				encodeHash(decodeHash(hash).add(d / 2 - 0.1, 0), hash.length()));
	}

	@Test
	public void testMovingHashCentreRightByGeoHashWidthGivesAdjacentHash() {
		String hash = "drt2";
		String right = right(hash);
		double d = widthDegrees(hash.length());
		assertEquals(right,
				encodeHash(decodeHash(hash).add(0, d), hash.length()));
	}

	@Test
	public void testMovingHashCentreRightBySlighltyMoreThanHalfGeoHashWidthGivesAdjacentHash() {
		String hash = "drt2";
		String right = right(hash);
		double d = widthDegrees(hash.length());
		assertEquals(right,
				encodeHash(decodeHash(hash).add(0, d / 2 + 0.1), hash.length()));
	}

	@Test
	public void testMovingHashCentreRightBySlightlyLessThanHalfGeoHashWidthGivesAdjacentHash() {
		String hash = "drt2";
		double d = widthDegrees(hash.length());
		assertEquals(hash,
				encodeHash(decodeHash(hash).add(0, d / 2 - 0.1), hash.length()));
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

		for (String hash : hashes) {
			System.out.println(decodeHash(hash) + ", hash=" + hash);
		}
		System.out.println(gridToString("dreb", 5, hashes));

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
		assertEquals(0.0011801509677274082, coverage.getRatio(), PRECISION);
	}

	@Test
	public void testCoverBoundingBoxWithOptimalHashLengthAroundBoston() {
		Coverage coverage = coverBoundingBox(SCHENECTADY_LAT, SCHENECTADY_LON,
				HARTFORD_LAT, HARTFORD_LON);
		assertEquals(4, coverage.getHashes().size());
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
		int length = hashLengthToEncloseBoundingBox(SCHENECTADY_LAT,
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
	public void test() {
		String h = "sew1c2vs2q5r";
		for (int i = h.length(); i >= 1; i--) {
			String hash = h.substring(0, i);
			LatLong p1 = decodeHash(hash);
			LatLong p2 = decodeHash(adjacentHash(hash, Direction.RIGHT));
			LatLong p3 = decodeHash(adjacentHash(hash, Direction.BOTTOM));
			double v = Math.abs(180 / (p2.getLon() - p1.getLon()));
			double v2 = Math.abs(360 / (p3.getLat() - p1.getLat()));
			System.out.println("lon " + i + "\t" + Math.log(v) / Math.log(2));
			System.out.println("lat " + i + "\t" + Math.log(v2) / Math.log(2));
		}
	}

	@Test
	public void testTo180() {
		assertEquals(0, to180(0), PRECISION);
		assertEquals(10, to180(10), PRECISION);
		assertEquals(-10, to180(-10), PRECISION);
		assertEquals(180, to180(180), PRECISION);
		assertEquals(-180, to180(-180), PRECISION);
		assertEquals(-170, to180(190), PRECISION);
		assertEquals(170, to180(-190), PRECISION);
		assertEquals(-170, to180(190 + 360), PRECISION);
	}

	@Test
	public void testLongitudeDiff() {
		assertEquals(10, longitudeDiff(15, 5), PRECISION);
		assertEquals(10, longitudeDiff(-175, 175), PRECISION);
		assertEquals(350, longitudeDiff(175, -175), PRECISION);
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
		assertEquals(45.0 / 2, heightDegrees(1), 0.00001);

		assertEquals(11.25 / 2, heightDegrees(2), 0.00001);
		assertEquals(1.40625 / 2, heightDegrees(3), 0.00001);
		assertEquals(0.3515625 / 2, heightDegrees(4), 0.00001);
		assertEquals(0.0439453125 / 2, heightDegrees(5), 0.00001);
		assertEquals(0.010986328125 / 2, heightDegrees(6), 0.00001);
	}

	@Test
	public void testMatrix() {
		System.out.println(gridToString("dred", -5, -5, 5, 5));
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
	public void testHashLengthToEncloseBoundingBoxReturns0IfBoxTooBig() {
		assertEquals(0,
				GeoHash.hashLengthToEncloseBoundingBox(80, -170, -80, 170));
	}
}
