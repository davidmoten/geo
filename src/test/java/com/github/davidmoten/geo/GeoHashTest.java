package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

public class GeoHashTest {

	private static final double HARTFORD_LON = -72.727175;
	private static final double HARTFORD_LAT = 41.842967;
	private static final double SCHENECTADY_LON = -73.950691;
	private static final double SCHENECTADY_LAT = 42.819581;
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

	/**
	 * <p>
	 * Use this <a href=
	 * "http://www.lucenerevolution.org/sites/default/files/Lucene%20Rev%20Preso%20Smiley%20Spatial%20Search.pdf"
	 * >link</a> for double-checking.
	 * </p>
	 */
	@Test
	public void testCoverBoundingBoxAroundBoston() {

		Set<String> hashes = GeoHash.hashesToCoverBoundingBox(SCHENECTADY_LAT,
				SCHENECTADY_LON, HARTFORD_LAT, HARTFORD_LON, 1);

		// check schenectady hash
		assertEquals("dre",
				GeoHash.encodeHash(SCHENECTADY_LAT, SCHENECTADY_LON, 3));
		// check hartford hash
		assertEquals("drk", GeoHash.encodeHash(HARTFORD_LAT, HARTFORD_LON, 3));

		// check neighbours
		assertEquals("drs", GeoHash.adjacentHash("dre", Direction.RIGHT));
		assertEquals("dr7", GeoHash.adjacentHash("dre", Direction.BOTTOM));
		assertEquals("drk", GeoHash.adjacentHash("drs", Direction.BOTTOM));

		for (String hash : hashes) {
			System.out.println(GeoHash.decodeHash(hash) + ", hash=" + hash);
		}
		// checked qualitatively against
		//
		// assertEquals(Sets.newHashSet("dre", "dr7", "drs", "drk"), hashes);
		assertEquals(Sets.newHashSet("dreq", "dr7q", "dreu", "dres", "dr7w",
				"dre6", "dre2", "drek", "drkn", "dreb", "drsh", "dref", "dred",
				"dre8", "dr7y", "drs4", "drsn", "drew", "drs0", "drey"), hashes);
	}

	@Test
	public void testCoverBoundingBoxAroundBostonNumIsTwo() {

		Set<String> hashes = GeoHash.hashesToCoverBoundingBox(SCHENECTADY_LAT,
				SCHENECTADY_LON, HARTFORD_LAT, HARTFORD_LON, 3);

		for (String hash : hashes) {
			System.out.println(GeoHash.decodeHash(hash) + ", hash=" + hash);
		}
		// checked qualitatively against
		//
		// assertEquals(Sets.newHashSet("dre", "dr7", "drs", "drk"), hashes);
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
	public void testTo180() {
		assertEquals(0, GeoHash.to180(0), PRECISION);
		assertEquals(10, GeoHash.to180(10), PRECISION);
		assertEquals(-10, GeoHash.to180(-10), PRECISION);
		assertEquals(180, GeoHash.to180(180), PRECISION);
		assertEquals(-180, GeoHash.to180(-180), PRECISION);
		assertEquals(-170, GeoHash.to180(190), PRECISION);
		assertEquals(170, GeoHash.to180(-190), PRECISION);
		assertEquals(-170, GeoHash.to180(190 + 360), PRECISION);
	}

	@Test
	public void testLongitudeDiff() {
		assertEquals(10, GeoHash.longitudeDiff(15, 5), PRECISION);
		assertEquals(10, GeoHash.longitudeDiff(-175, 175), PRECISION);
		assertEquals(350, GeoHash.longitudeDiff(175, -175), PRECISION);
	}

	@Test
	public void testGeoHashWidthDegrees() {
		GeoHash.encodeHash(-25.382708, -49.265506, 6);
		GeoHash.encodeHash(-25.382708, -49.265506, 5);
		GeoHash.encodeHash(-25.382708, -49.265506, 4);
		GeoHash.encodeHash(-25.382708, -49.265506, 3);
		GeoHash.encodeHash(-25.382708, -49.265506, 2);
		GeoHash.encodeHash(-25.382708, -49.265506, 1);
		assertEquals(45.0, GeoHash.getGeoHashWidthInDegrees(1), 0.00001);

		assertEquals(11.25, GeoHash.getGeoHashWidthInDegrees(2), 0.00001);
		assertEquals(1.40625, GeoHash.getGeoHashWidthInDegrees(3), 0.00001);
		assertEquals(0.3515625, GeoHash.getGeoHashWidthInDegrees(4), 0.00001);
		assertEquals(0.0439453125, GeoHash.getGeoHashWidthInDegrees(5), 0.00001);
		assertEquals(0.010986328125, GeoHash.getGeoHashWidthInDegrees(6),
				0.00001);
	}

	@Test
	public void testGeoHashHeightDegrees() {
		GeoHash.encodeHash(-25.382708, -49.265506, 6);
		GeoHash.encodeHash(-25.382708, -49.265506, 5);
		GeoHash.encodeHash(-25.382708, -49.265506, 4);
		GeoHash.encodeHash(-25.382708, -49.265506, 3);
		GeoHash.encodeHash(-25.382708, -49.265506, 2);
		GeoHash.encodeHash(-25.382708, -49.265506, 1);
		assertEquals(45.0, GeoHash.getGeoHashHeightInDegrees(1), 0.00001);

		assertEquals(11.25, GeoHash.getGeoHashHeightInDegrees(2), 0.00001);
		assertEquals(1.40625, GeoHash.getGeoHashHeightInDegrees(3), 0.00001);
		assertEquals(0.3515625, GeoHash.getGeoHashHeightInDegrees(4), 0.00001);
		assertEquals(0.0439453125, GeoHash.getGeoHashHeightInDegrees(5),
				0.00001);
		assertEquals(0.010986328125, GeoHash.getGeoHashHeightInDegrees(6),
				0.00001);
	}
}
