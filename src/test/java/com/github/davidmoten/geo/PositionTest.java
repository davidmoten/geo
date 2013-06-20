package com.github.davidmoten.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PositionTest {

	private static final int ACCEPTABLE_DISTANCE_PRECISION = 3;
	private static final double PRECISION = 0.00001;
	private static final double MIN_DISTANCE_KM = 200;
	List<Position> squareRegion;

	@Before
	public void init() {
		squareRegion = new ArrayList<Position>();
		squareRegion.add(new Position(20, 20));
		squareRegion.add(new Position(40, 20));
		squareRegion.add(new Position(40, 40));
		squareRegion.add(new Position(20, 40));
	}

	@Test
	public final void testPredict() {
		Position p = new Position(53, 3);
		Position p2 = p.predict(100, 30);
		Assert.assertEquals(53.77644258276322, p2.getLat(), 0.01);
		Assert.assertEquals(3.7609191005595877, p2.getLon(), 0.01);

		// large distance tests around the equator

		double meanCircumferenceKm = 40041.47;

		// start on equator, just East of Greenwich
		p = new Position(0, 3);

		// quarter circumference, Still in Eastern hemisphere

		p2 = p.predict(meanCircumferenceKm / 4.0, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(93.0, p2.getLon(), 0.1);

		// half circumference, Now in Western hemisphere,
		// just East of the International date line

		p2 = p.predict(meanCircumferenceKm / 2.0, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-177.0, p2.getLon(), 0.1);

		// three quarters circumference, Now in Western hemisphere,

		p2 = p.predict(3.0 * meanCircumferenceKm / 4.0, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-87.0, p2.getLon(), 0.1);

		// full circumference, back to start
		// relax Longitude tolerance slightly

		p2 = p.predict(meanCircumferenceKm, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.2);

		// same thing but backwards (heading west)

		// quarter circumference, Still in western hemisphere

		p2 = p.predict(meanCircumferenceKm / 4.0, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-87.0, p2.getLon(), 0.1);

		// half circumference, Now in Western hemisphere,
		// just East of the International date line

		p2 = p.predict(meanCircumferenceKm / 2.0, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(-177.0, p2.getLon(), 0.1);

		// three quarters circumference, Now in eastern hemisphere,

		p2 = p.predict(3.0 * meanCircumferenceKm / 4.0, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(93.0, p2.getLon(), 0.1);

		// full circumference, back to start
		// relax Longitude tolerance slightly

		p2 = p.predict(meanCircumferenceKm, 90);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.001);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.2);

		// OVER THE South POLE
		// ===================

		// quarter circumference, should be at south pole

		p2 = p.predict(meanCircumferenceKm / 4.0, 180);
		Assert.assertEquals(-90.0, p2.getLat(), 0.1);

		// this next assertion is by no means confident
		// expecting 3 but getting it's reciprocal.
		// Strange things happen at the pole!!

		Assert.assertEquals(-177, p2.getLon(), 0.00001);

		// half circumference, should be at the equator
		// but in the Western hemisphere

		p2 = p.predict(meanCircumferenceKm / 2.0, 180);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.1);
		Assert.assertEquals(-177, p2.getLon(), 0.00001);

		// 3/4 circumference, should be at the north Pole
		// but in the Western hemisphere

		p2 = p.predict(3.0 * meanCircumferenceKm / 4.0, 180);
		Assert.assertEquals(90.0, p2.getLat(), 0.1);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.00001);

		// full circumference, back to start
		// relax latitude tolerance slightly

		p2 = p.predict(meanCircumferenceKm, 270);
		Assert.assertEquals(p.getLat(), p2.getLat(), 0.1);
		Assert.assertEquals(p.getLon(), p2.getLon(), 0.2);

	}

	/**
	 * worked example from http://en.wikipedia.org/wiki/Great-circle_distance#
	 * Radius_for_spherical_Earth used for test below
	 */
	@Test
	public final void testDistance() {

		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);
		Assert.assertEquals(2886.45, p1.getDistanceToKm(p2), 0.01);
	}

	@Test
	public final void testBearing() {
		// test case taken from Geoscience Australia implementation of
		// Vincenty formula.
		// http://www.ga.gov.au/bin/gda_vincenty.cgi?inverse=0&lat_degrees1=-37&lat_minutes1=57&lat_seconds1=03.72030&NamePoint1=Flinders+Peak
		// &lon_degrees1=144&lon_minutes1=25&lon_seconds1=29.52440&forward_azimuth_deg=306&forward_azimuth_min=52&forward_azimuth_sec=05.37
		// &NamePoint2=Buninyong&ellipsoidal_dist=54972.217&lat_deg1=-37+deg&lat_min1=57+min&lat_sec1=3.7203+sec&lon_deg1=144+deg
		// &lon_min1=25+min&lon_sec1=29.5244+sec&f_az_deg=306+deg&f_az_min=52+min&f_az_sec=5.37+sec&Submit=Submit+Data
		// Note that we are not using Vincenty formula but we are close to the
		// answer (within 0.2 degrees). That's sufficient!
		Position p1 = new Position(-(37 + 57.0 / 60 + 3.72030 / 3600), 144
				+ 25.0 / 60 + 29.52440 / 3600);
		Position p2 = new Position(-(37 + 39.0 / 60 + 10.15718 / 3600), 143
				+ 55.0 / 60 + 35.38564 / 3600);
		Assert.assertEquals(Position.toDegrees(306, 52, 5.37),
				p1.getBearingDegrees(p2), 0.2);
	}

	@Test
	public final void testBearingDifference() {
		double precision = 0.00001;
		Assert.assertEquals(15.0, Position.getBearingDifferenceDegrees(20, 5),
				precision);
		Assert.assertEquals(15.0,
				Position.getBearingDifferenceDegrees(20, 365), precision);
		Assert.assertEquals(15.0, Position.getBearingDifferenceDegrees(20, 5),
				precision);
		Assert.assertEquals(15.0, Position.getBearingDifferenceDegrees(380, 5),
				precision);
		Assert.assertEquals(-25, Position.getBearingDifferenceDegrees(-20, 5),
				precision);
		Assert.assertEquals(5, Position.getBearingDifferenceDegrees(-20, -25),
				precision);
	}

	@Test
	public final void testLatLonPresentation() {
		char d = 0x00B0;
		char m = '\'';
		Assert.assertEquals("25" + d + "30.00" + m + "S",
				Position.toDegreesMinutesDecimalMinutesLatitude(-25.5));
		Assert.assertEquals("0" + d + "00.00" + m + "N",
				Position.toDegreesMinutesDecimalMinutesLatitude(0));
		Assert.assertEquals("0" + d + "30.00" + m + "S",
				Position.toDegreesMinutesDecimalMinutesLatitude(-0.5));
		Assert.assertEquals("0" + d + "30.00" + m + "N",
				Position.toDegreesMinutesDecimalMinutesLatitude(0.5));
		Assert.assertEquals("1" + d + "30.00" + m + "N",
				Position.toDegreesMinutesDecimalMinutesLatitude(1.5));
		Assert.assertEquals("1" + d + "00.00" + m + "N",
				Position.toDegreesMinutesDecimalMinutesLatitude(1.0));

		Assert.assertEquals("1" + d + "00.00" + m + "E",
				Position.toDegreesMinutesDecimalMinutesLongitude(1.0));
		Assert.assertEquals("1" + d + "00.00" + m + "W",
				Position.toDegreesMinutesDecimalMinutesLongitude(-1.0));
	}

	@Test
	public final void testGetPositionAlongPath() {

		// Create new position objects
		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);

		{
			double distanceKm = p1.getDistanceToKm(p2);
			double bearingDegrees = p1.getBearingDegrees(p2);
			Position p3 = p1.predict(distanceKm * 0.7, bearingDegrees);

			// Expected position
			Position actual = p1.getPositionAlongPath(p2, 0.7);
			// Test expected Lat return position
			Assert.assertEquals(p3.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p3.getLon(), actual.getLon(), 0.01);
			// Test expected Lat return position
			Assert.assertEquals(35.47, actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(-109.11, actual.getLon(), 0.01);

		}

		{
			// If start point equals end point then a proportion
			// along the path should equal the start point.
			Position actual = p1.getPositionAlongPath(p1, 0.7);
			// Test expected Lat return position
			Assert.assertEquals(p1.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p1.getLon(), actual.getLon(), 0.01);
		}

		{
			// If proportion is 0.0 then should return start point
			Position actual = p1.getPositionAlongPath(p2, 0.0);
			// Test expected Lat return position
			Assert.assertEquals(p1.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p1.getLon(), actual.getLon(), 0.01);
		}

		{
			// If proportion is 1.0 then should return end point
			Position actual = p1.getPositionAlongPath(p2, 1.0);
			// Test expected Lat return position
			Assert.assertEquals(p2.getLat(), actual.getLat(), 0.01);
			// Test expected Lon return position
			Assert.assertEquals(p2.getLon(), actual.getLon(), 0.01);
		}
	}

	@Test(expected = RuntimeException.class)
	public final void testGetPositionAlongPathExceptionsGreater() {
		// Create new position objects
		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);

		p1.getPositionAlongPath(p2, 1.1);

	}

	@Test(expected = RuntimeException.class)
	public final void testGetPositionAlongPathExceptionsLess() {
		// Create new position objects
		Position p1 = new Position(36.12, -86.67);
		Position p2 = new Position(33.94, -118.4);

		p1.getPositionAlongPath(p2, -0.1);
	}

	@Test(expected = NullPointerException.class)
	public final void testGetPositionAlongPathExceptionsNull() {
		// Create new position objects
		Position p1 = new Position(36.12, -86.67);

		p1.getPositionAlongPath(null, 0.7);

	}

	@Test
	public final void testContinuous() {
		{
			Position a = new Position(-35, 179);
			Position b = new Position(-35, -178);
			Position c = b.ensureContinuous(a);
			Assert.assertEquals(182, c.getLon(), 0.0001);
			Assert.assertEquals(b.getLat(), c.getLat(), 0.0001);

		}
		{
			Position a = new Position(-35, -2);
			Position b = new Position(-35, 360);
			Position c = b.ensureContinuous(a);
			Assert.assertEquals(0, c.getLon(), 0.0001);
		}
		{
			Position a = new Position(-35, -2);
			Position b = new Position(-35, -3);
			Position c = b.ensureContinuous(a);
			Assert.assertEquals(-3.0, c.getLon(), 0.0001);
		}

	}

	@Test
	public final void testIsWithin() {
		final List<Position> testRegion;
		// square polygon with v cut out shape
		testRegion = Lists.newArrayList();
		testRegion.add(new Position(-20, 130));
		testRegion.add(new Position(-20, 140));
		testRegion.add(new Position(-30, 140));
		testRegion.add(new Position(-25, 135));
		testRegion.add(new Position(-30, 130));

		Position bothLatAndLongInsideRegion = new Position(-24, 135);
		Position bothLatAndLongOutsideRegion = new Position(-35, 145);

		Position latInAndLongOutRegion = new Position(-25, 145);
		Position latOutAndLongInRegion = new Position(-35, 135);

		Position latInAndLongInVRegion = new Position(-28, 138);
		Position latOutAndLongOutVRegion = new Position(-26, 135);

		Position bothLatAndLongOnBorder = new Position(-20, 135);
		Position bothLatAndLongOnOutsideCorner = new Position(-20, 130);
		Position bothLatAndLongOnInsideCorner = new Position(-25, 135);

		Assert.assertTrue(bothLatAndLongInsideRegion.isWithin(testRegion));
		Assert.assertTrue(latInAndLongInVRegion.isWithin(testRegion));
		Assert.assertTrue(bothLatAndLongOnInsideCorner.isWithin(testRegion));

		Assert.assertFalse(bothLatAndLongOutsideRegion.isWithin(testRegion));
		Assert.assertFalse(latInAndLongOutRegion.isWithin(testRegion));
		Assert.assertFalse(latOutAndLongInRegion.isWithin(testRegion));
		Assert.assertFalse(latOutAndLongOutVRegion.isWithin(testRegion));
		Assert.assertFalse(bothLatAndLongOnBorder.isWithin(testRegion));
		Assert.assertFalse(bothLatAndLongOnOutsideCorner.isWithin(testRegion));

	}

	/**
	 * Returned intersection occurs in the centre of the segment and the segment
	 * latitude is constant.
	 */
	@Test
	public void testGetClosestIntersectionWithSegment1() {
		Position p = new Position(-30, 120);
		Position r = p.getClosestIntersectionWithSegment(
				new Position(-20, 110), new Position(-20, 130));

		assertEquals(-20.0, r.getLat(), 1);
		assertEquals(120.0, r.getLon(), 1);
	}

	/**
	 * Returned intersection doesn't occur in the centre of the segment and the
	 * segment latitude is constant.
	 */
	@Test
	public void testGetClosestIntersectionWithSegment2() {
		Position p = new Position(-30, 110);
		Position r = p.getClosestIntersectionWithSegment(
				new Position(-20, 100), new Position(-20, 130));

		assertEquals(-20.58106, r.getLat(), PRECISION);
		assertEquals(110.218334, r.getLon(), PRECISION);
	}

	/**
	 * Returned intersection occurs in the centre of the segment and the segment
	 * longitude is constant.
	 */
	@Test
	public void testGetClosestIntersectionWithSegment3() {
		Position p = new Position(-30, 120);
		Position r = p.getClosestIntersectionWithSegment(
				new Position(-20, 110), new Position(-20, 130));

		assertEquals(-20.28353, r.getLat(), PRECISION);
		assertEquals(119.90349, r.getLon(), PRECISION);
	}

	/**
	 * Returned intersection doesn't occur in the centre of the segment and the
	 * segment longitude is constant.
	 */
	@Test
	public void testGetClosestIntersectionWithSegment4() {
		Position p = new Position(-30, 110);
		Position r = p.getClosestIntersectionWithSegment(
				new Position(-20, 100), new Position(-20, 130));

		assertEquals(-20.58106, r.getLat(), PRECISION);
		assertEquals(110.218334, r.getLon(), PRECISION);
	}

	/**
	 * The test position should return the closest distance to the segment when
	 * it's perpendicular to the segment.
	 */
	@Test
	public void testGetDistanceToSegmentKm1() {
		Position p = new Position(-30, 110);

		Position sp1 = new Position(-20, 100);
		Position sp2 = new Position(-20, 130);

		double r = p.getDistanceToSegmentKm(sp1, sp2);

		Position intersection = p.getClosestIntersectionWithSegment(sp1, sp2);
		System.out
				.println("Position of intersection, used to manually calculate the distance via http://www.nhc.noaa.gov/gccalc.shtml : "
						+ intersection);

		// 1047 according to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(1047, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * The test position should return the distance to the closest segment end
	 * when the intersection falls out of the given segment. In this test the
	 * position is closest to the <b>first</b> point of the segment.
	 */
	public void testGetDistanceToSegmentKm2() {
		Position p = new Position(-10, 115);

		// Segment made of two points
		Position sp1 = new Position(-30, 140);
		Position sp2 = new Position(-30, 140);

		Position r1 = p.getClosestIntersectionWithSegment(sp1, sp2);
		assertEquals(sp1.getLat(), r1.getLat(), PRECISION);
		assertEquals(sp1.getLon(), r1.getLon(), PRECISION);
	}

	/**
	 * The test position should return the distance to the closest segment end
	 * when the intersection falls out of the given segment. In this test the
	 * position is closest to the <b>second</b> point of the segment.
	 */
	public void testGetDistanceToSegmentKm3() {
		Position p = new Position(-10, 150);

		// Segment made of two points
		Position sp1 = new Position(-20, 120);
		Position sp2 = new Position(-30, 140);

		Position r = p.getClosestIntersectionWithSegment(sp1, sp2);
		assertEquals(sp2.getLat(), r.getLat(), PRECISION);
		assertEquals(sp2.getLon(), r.getLon(), PRECISION);
	}

	/**
	 * Test the position when the segment's start and end points are identical.
	 * In this test the position is closest to the both the start and end
	 * points.
	 */
	@Test
	public void testGetDistanceToSegmentKm4() {
		Position p = new Position(-10, 150);

		// Segment made of two points
		Position sp1 = new Position(-20, 120);
		Position sp2 = new Position(-20, 120);

		double r = p.getDistanceToSegmentKm(sp1, sp2);
		// 3399.0 according to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(3399.0, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * Test the position when the it lies on segment. In this test the position
	 * should return 0 since it lies on the line.
	 */
	@Test
	public void testGetDistanceToSegmentKm5() {

		// Segment made of two points
		Position sp1 = new Position(-10, 120);
		Position p = sp1.getPositionAlongPath(sp1, 0.4);
		Position sp2 = new Position(-10, 150);

		double r = p.getDistanceToSegmentKm(sp1, sp2);
		assertEquals(0, r, PRECISION);
	}

	/**
	 * Test the position when it lies outside of the segment but in line(great
	 * cirle path) with the two segment points. In this test the position is
	 * closest to the <b>second</b> segment point.
	 */
	@Test
	public void testGetDistanceToSegmentKm6() {

		// Segment made of two points
		Position sp1 = new Position(-10, 120);
		Position p = new Position(-10, 150);
		Position sp2 = sp1.getPositionAlongPath(p, 0.5);

		double r = p.getDistanceToSegmentKm(sp1, sp2);
		// 1641 according to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(1641.00, r, ACCEPTABLE_DISTANCE_PRECISION);

	}

	/**
	 * The test position should return the closest distance to either a segment
	 * point or segment intersection on the path. The path constitutes several
	 * valid {@link Position}'s. In this test the point is closest to the
	 * <b>first segment</b>.
	 */
	@Test
	public void testGetDistanceToPathKm1() {
		// positions to test
		Position p = new Position(-10, 115);

		// segment positions constituting a path
		Position sp1 = new Position(-30, 100);
		Position sp2 = new Position(-20, 120);
		Position sp3 = new Position(-30, 140);

		// path with two segments
		List<Position> path = new ArrayList<Position>();
		path.add(sp1);
		path.add(sp2);
		path.add(sp3);

		// expect that the closes point will be sp2 itself since the test
		// position can't be perpendicular to the segment and sp2 is the closest
		// point.

		double r = p.getDistanceToPathKm(path);

		Position intersection = p.getClosestIntersectionWithSegment(sp1, sp2);
		System.out
				.println("Position of intersection, used to manually calculate the distance via http://www.nhc.noaa.gov/gccalc.shtml : "
						+ intersection);
		// 1234 according to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(1234, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * The test position should return the closest distance to either a segment
	 * point or segment intersection on the path. The path constitutes several
	 * valid {@link Position}'s. In this test the point is closest to the
	 * <b>first segment start position</b> .
	 */
	@Test
	public void testGetDistanceToPathKm2() {
		// positions to test
		Position p = new Position(-10, 80);

		// segment positions constituting a path
		Position sp1 = new Position(-30, 100);
		Position sp2 = new Position(-20, 120);
		Position sp3 = new Position(-30, 140);

		// path with two segments
		List<Position> path = new ArrayList<Position>();
		path.add(sp1);
		path.add(sp2);
		path.add(sp3);

		// expect that the closes point will be sp2 itself since the test
		// position can't be perpendicular to the segment and sp2 is the closest
		// point.

		double r = p.getDistanceToPathKm(path);
		// 3039 accroding to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(3039, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * The test position should return the closest distance to either a segment
	 * point or segment intersection on the path. The path constitutes several
	 * valid {@link Position}'s. In this test the point is closest to the
	 * <b>second segment</b>.
	 */
	@Test
	public void testGetDistanceToPathKm3() {
		// positions to test
		Position p = new Position(-10, 115);

		// segment positions constituting a path
		Position sp1 = new Position(-30, 100);
		Position sp2 = new Position(-20, 120);
		Position sp3 = new Position(-30, 140);

		// path with two segments
		List<Position> path = new ArrayList<Position>();
		path.add(sp1);
		path.add(sp2);
		path.add(sp3);

		// expect that the closes point will be sp2 itself since the test
		// position can't be perpendicular to the segment and sp2 is the closest
		// point.

		double r = p.getDistanceToPathKm(path);

		Position intersection = p.getClosestIntersectionWithSegment(sp1, sp2);
		System.out
				.println("Position of intersection, used to manually calculate the distance via http://www.nhc.noaa.gov/gccalc.shtml : "
						+ intersection);
		// 1234 according to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(1234, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * The test position should return the closest distance to either a segment
	 * point or segment intersection on the path. The path constitutes several
	 * valid {@link Position}'s. In this test the point is closest to the
	 * <b>second segment end position</b>.
	 */
	@Test
	public void testGetDistanceToPathKm4() {
		// positions to test
		Position p = new Position(-10, 150);

		// segment positions constituting a path
		Position sp1 = new Position(-30, 100);
		Position sp2 = new Position(-20, 120);
		Position sp3 = new Position(-30, 140);

		// path with two segments
		List<Position> path = new ArrayList<Position>();
		path.add(sp1);
		path.add(sp2);
		path.add(sp3);

		// expect that the closes point will be sp2 itself since the test
		// position can't be perpendicular to the segment and sp2 is the closest
		// point.

		double r = p.getDistanceToPathKm(path);
		// 2452 according http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(2452, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * Test when a position is given an empty path.
	 */
	@Test(expected = RuntimeException.class)
	public void testGetDistanceToPathKm5() {
		// empty initialized list
		List<Position> path = new ArrayList<Position>();

		// positions to test
		Position p = new Position(-10, 150);

		p.getDistanceToPathKm(path);

	}

	/**
	 * Test when a position is given a null path.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetDistanceToPathKm6() {
		// empty initialized list
		List<Position> path = null;

		// positions to test
		Position p = new Position(-10, 150);

		p.getDistanceToPathKm(path);

	}

	/**
	 * Test when a position is given a path with only one position in it.
	 */
	@Test
	public void testGetDistanceToPathKm7() {
		// empty initialized list
		List<Position> path = new ArrayList<Position>();
		path.add(new Position(-30, 100));

		// positions to test
		Position p = new Position(-30, 120);

		double r = p.getDistanceToPathKm(path);
		// 1922 according to http://www.nhc.noaa.gov/gccalc.shtml
		assertEquals(1922, r, ACCEPTABLE_DISTANCE_PRECISION);
	}

	/**
	 * Test when a position is outside a given region consisting of several
	 * joining points. In this test the region is a roughly square region
	 * consisting of four segments or eight {@link Position}s. The test position
	 * is outside the region but within minimum distance in km.
	 */
	@Test
	public void testIsOutside1() {
		Position p = new Position(30, 19.99);

		assertFalse(p.isOutside(squareRegion, MIN_DISTANCE_KM));
	}

	/**
	 * Test when a position is outside a given region consisting of several
	 * joining points. In this test the region is a roughly square region
	 * consisting of four segments or eight {@link Position}s. The test position
	 * is outside both the region and the minimum distance in km.
	 */
	@Test
	public void testIsOutside2() {
		Position p = new Position(-10, 110);
		assertTrue(p.isOutside(squareRegion, MIN_DISTANCE_KM));
	}

	/**
	 * Test when a position is outside a given region consisting of several
	 * joining points. In this test the region is a roughly square region
	 * consisting of four segments or eight {@link Position}s. The test position
	 * is on the region path.
	 */
	@Test
	public void testIsOutside3() {
		Position p = new Position(20.07030897931526, 24.999999999999996);
		assertFalse(p.isOutside(squareRegion, MIN_DISTANCE_KM));
	}

	/**
	 * Test when a position is outside a given region consisting of several
	 * joining points. In this test the region is a roughly square region
	 * consisting of four segments or eight {@link Position}s. The test position
	 * is within the region path.
	 */
	@Test
	public void testIsOutside4() {
		Position p = new Position(30, 30);
		assertFalse(p.isOutside(squareRegion, MIN_DISTANCE_KM));
	}

	@Test
	public void testGetAltitude() {
		Position p = new Position(1, 2, 3);
		assertEquals(3.0, p.getAlt(), 0.00001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPredictThrowsExceptionIfAltitudeGreaterThanZero() {
		Position p = new Position(1, 2, 3);
		p.predict(100, 75);
	}

	@Test
	public void testModOnNegativeValues() {
		assertEquals(1, Position.mod(-3, 2), 0.0001);
	}

	@Test
	public void testHashCode() {
		assertTrue(new Position(1, 2).hashCode() > 0);
	}

	@Test
	public void testEquals() {
		Position p1 = new Position(1, 2);
		Position p2 = new Position(3, 4);
		assertFalse(p1.equals(null));
		assertFalse(p1.equals(p2));
		assertFalse(p2.equals(p1));
		assertTrue(p1.equals(p1));
		assertFalse(p1.equals("hello"));
	}
}
