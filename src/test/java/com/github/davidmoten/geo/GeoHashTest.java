package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

public class GeoHashTest {

	private static final double PRECISION_2 = 0.0001;
	private static final double PRECISION = 0.000000001;

	@Test
	public void testWhiteHouseHashEncode() {
		assertEquals("dqcjqcp84c6e",
				GeoHash.encodeHash(38.89710201881826, -77.03669792041183));
	}

	@Test
	public void testWhiteHouseHashDecode() {
		LatLong point = GeoHash.decodeHash("dqcjqcp84c6e");
		assertEquals(point.getLat(), 38.89710201881826, PRECISION);
		assertEquals(point.getLon(), -77.03669792041183, PRECISION);
	}

	@Test
	public void testFromGeoHashDotOrg() {
		assertEquals("6gkzwgjzn820", GeoHash.encodeHash(-25.382708, -49.265506));
	}

	@Test
	public void testHashOfNonDefaultLength() {
		assertEquals("6gkzwg", GeoHash.encodeHash(-25.382708, -49.265506, 6));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHashEncodeGivenNonPositiveLength() {
		GeoHash.encodeHash(-25.382708, -49.265506, 0);
	}

	@Test
	public void testAnother() {
		assertEquals("sew1c2vs2q5r", GeoHash.encodeHash(20, 31));
	}

	@Test
	public void testAdjacentBottom() {
		assertEquals("u0zz", GeoHash.adjacentHash("u1pb", Direction.BOTTOM));
	}

	@Test
	public void testAdjacentTop() {
		assertEquals("u1pc", GeoHash.adjacentHash("u1pb", Direction.TOP));
	}

	@Test
	public void testAdjacentLeft() {
		assertEquals("u1p8", GeoHash.adjacentHash("u1pb", Direction.LEFT));
	}

	@Test
	public void testAdjacentRight() {
		assertEquals("u300", GeoHash.adjacentHash("u1pb", Direction.RIGHT));
	}

	@Test
	public void testNeighbouringHashes() {
		String center = "dqcjqc";
		Set<String> neighbours = Sets.newHashSet("dqcjqf", "dqcjqb", "dqcjr1",
				"dqcjq9", "dqcjqd", "dqcjr4", "dqcjr0", "dqcjq8");
		assertEquals(neighbours, Sets.newHashSet(GeoHash.neighbours(center)));
	}

	@Test
	public void testHashDecodeOnBlankString() {
		LatLong point = GeoHash.decodeHash("");
		assertEquals(0, point.getLat(), PRECISION);
		assertEquals(0, point.getLon(), PRECISION);
	}

	@Test
	public void testInstantiation() {
		GeoHash.instantiate();
	}

	@Test
	public void testSpeed() {
		long t = System.currentTimeMillis();
		int numIterations = 100000;
		for (int i = 0; i < numIterations; i++)
			GeoHash.encodeHash(38.89710201881826, -77.03669792041183);
		double numPerSecond = numIterations / (System.currentTimeMillis() - t)
				* 1000;
		System.out.println("num encodeHash per second=" + numPerSecond);

	}

	@Test
	public void testHashLengthCalculationForZeroSeparationDistance() {
		assertEquals(
				11,
				GeoHash.minHashLengthToEnsureCellCentreSeparationDistanceIsLessThanMetres(0));
	}

	@Test
	public void testHashLengthCalculationWhenVeryLargeSeparationDistance() {
		assertEquals(
				1,
				GeoHash.minHashLengthToEnsureCellCentreSeparationDistanceIsLessThanMetres(5003530 * 2));
	}

	@Test
	public void testHashLengthCalculationWhenMediumDistance() {
		assertEquals(
				5,
				GeoHash.minHashLengthToEnsureCellCentreSeparationDistanceIsLessThanMetres(3900));
	}

	@Test
	public void testCoverBoundingBoxAroundBoston() {
		double d = 0.1;

		Set<String> hashes = GeoHash.hashesToCoverBoundingBox(42.3583 + d,
				-71.0603 - d, 42.3583 - d, -71.0603 + d, 1);
		for (String hash : hashes) {
			System.out.println(GeoHash.decodeHash(hash) + ", hash=" + hash);
		}
		// checked qualitatively against
		// http://www.lucenerevolution.org/sites/default/files/Lucene%20Rev%20Preso%20Smiley%20Spatial%20Search.pdf
		assertEquals(Sets.newHashSet("drt2t", "drt2k", "drt2m", "drt2s"),
				hashes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCoverBoundingBoxMustBePassedMinHashesGreaterThanZero() {
		GeoHash.hashesToCoverBoundingBox(0, 135, 10, 145, 0);
	}

	@Test
	public void test() {
		String h = "sew1c2vs2q5r";
		for (int i = h.length(); i >= 1; i--) {
			String hash = h.substring(0, i);
			LatLong p1 = GeoHash.decodeHash(hash);
			LatLong p2 = GeoHash.decodeHash(GeoHash.adjacentHash(hash,
					Direction.RIGHT));
			LatLong p3 = GeoHash.decodeHash(GeoHash.adjacentHash(hash,
					Direction.BOTTOM));
			double v = Math.abs(180 / (p2.getLon() - p1.getLon()));
			double v2 = Math.abs(360 / (p3.getLat() - p1.getLat()));
			System.out.println("lon " + i + "\t" + Math.log(v) / Math.log(2));
			System.out.println("lat " + i + "\t" + Math.log(v2) / Math.log(2));
		}
	}

	@Test
	public void testGeoHashWidth() {
		assertEquals(90, GeoHash.getGeoHashWidthInDegrees(1), 0.0001);
		assertEquals(360.0 / 16, GeoHash.getGeoHashWidthInDegrees(2),
				PRECISION_2);
		assertEquals(360.0 / 128, GeoHash.getGeoHashWidthInDegrees(3),
				PRECISION_2);
	}

	@Test
	public void testGeoHashHeight() {
		assertEquals(180.0 / 8, GeoHash.getGeoHashHeightInDegrees(1),
				PRECISION_2);
		assertEquals(180.0 / 64, GeoHash.getGeoHashHeightInDegrees(2),
				PRECISION_2);
		assertEquals(180.0 / 256, GeoHash.getGeoHashHeightInDegrees(3),
				PRECISION_2);
	}
}
