package com.github.davidmoten.geo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * <p>
 * Utility functions for <a
 * href="http://en.wikipedia.org/wiki/Geohash">geohashing</a>. Core hashing code
 * based on Dave Troy's javascript implementation <a
 * href="https://github.com/davetroy/geohash-js">here</a>.
 * </p>
 * 
 * @author dxm
 * 
 */
public final class GeoHash {

	private static final int[] BITS = new int[] { 16, 8, 4, 2, 1 };
	private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
	private static final Map<Direction, Map<Parity, String>> NEIGHBOURS = createNeighbours();
	private static final Map<Direction, Map<Parity, String>> BORDERS = createBorders();
	private static final int MAX_HASH_LENGTH = 12;

	/**
	 * Private constructor. Instantiated only for test coverage purposes.
	 */
	private GeoHash() {
		// prevent instantiation
	}

	@VisibleForTesting
	static void instantiate() {
		// ensure full code coverage
		new GeoHash();
	}

	/**
	 * Returns a map to be used in hash border calculations.
	 * 
	 * @return
	 */
	private static Map<Direction, Map<Parity, String>> createBorders() {
		Map<Direction, Map<Parity, String>> m = createDirectionParityMap();

		m.get(Direction.RIGHT).put(Parity.EVEN, "bcfguvyz");
		m.get(Direction.LEFT).put(Parity.EVEN, "0145hjnp");
		m.get(Direction.TOP).put(Parity.EVEN, "prxz");
		m.get(Direction.BOTTOM).put(Parity.EVEN, "028b");

		addOddParityEntries(m);
		return m;
	}

	/**
	 * Returns a map to be used in adjacent hash calculations.
	 * 
	 * @return
	 */
	private static Map<Direction, Map<Parity, String>> createNeighbours() {
		Map<Direction, Map<Parity, String>> m = createDirectionParityMap();

		m.get(Direction.RIGHT).put(Parity.EVEN,
				"bc01fg45238967deuvhjyznpkmstqrwx");
		m.get(Direction.LEFT).put(Parity.EVEN,
				"238967debc01fg45kmstqrwxuvhjyznp");
		m.get(Direction.TOP).put(Parity.EVEN,
				"p0r21436x8zb9dcf5h7kjnmqesgutwvy");
		m.get(Direction.BOTTOM).put(Parity.EVEN,
				"14365h7k9dcfesgujnmqp0r2twvyx8zb");
		addOddParityEntries(m);

		return m;
	}

	/**
	 * Create a direction and parity map for use in adjacent hash calculations.
	 * 
	 * @return
	 */
	private static Map<Direction, Map<Parity, String>> createDirectionParityMap() {
		Map<Direction, Map<Parity, String>> m = Maps.newHashMap();
		m.put(Direction.BOTTOM, Maps.<Parity, String> newHashMap());
		m.put(Direction.TOP, Maps.<Parity, String> newHashMap());
		m.put(Direction.LEFT, Maps.<Parity, String> newHashMap());
		m.put(Direction.RIGHT, Maps.<Parity, String> newHashMap());
		return m;
	}

	/**
	 * Puts odd parity entries in the map m based purely on the even entries.
	 * 
	 * @param m
	 */
	private static void addOddParityEntries(
			Map<Direction, Map<Parity, String>> m) {
		m.get(Direction.BOTTOM).put(Parity.ODD,
				m.get(Direction.LEFT).get(Parity.EVEN));
		m.get(Direction.TOP).put(Parity.ODD,
				m.get(Direction.RIGHT).get(Parity.EVEN));
		m.get(Direction.LEFT).put(Parity.ODD,
				m.get(Direction.BOTTOM).get(Parity.EVEN));
		m.get(Direction.RIGHT).put(Parity.ODD,
				m.get(Direction.TOP).get(Parity.EVEN));
	}

	/**
	 * Returns the adjacent hash in given {@link Direction}. Based on
	 * https://github.com/davetroy/geohash-js/blob/master/geohash.js.
	 * 
	 * @param hash
	 * @param direction
	 * @return
	 */
	public static String adjacentHash(String hash, Direction direction) {
		checkHash(hash);
		String source = hash.toLowerCase();
		char lastChar = source.charAt(source.length() - 1);
		Parity parity = (source.length() % 2 == 0) ? Parity.EVEN : Parity.ODD;
		String base = source.substring(0, source.length() - 1);
		if (BORDERS.get(direction).get(parity).indexOf(lastChar) != -1)
			base = adjacentHash(base, direction);
		return base
				+ BASE32.charAt(NEIGHBOURS.get(direction).get(parity)
						.indexOf(lastChar));
	}

	/**
	 * Throws an {@link IllegalArgumentException} if and only if the hash is
	 * null or blank.
	 * 
	 * @param hash
	 */
	private static void checkHash(String hash) {
		Preconditions.checkArgument(hash != null && hash.length() > 0,
				"hash must be non-null of length>1");
	}

	/**
	 * Returns the adjacent hash to the right.
	 * 
	 * @param hash
	 * @return
	 */
	public static String right(String hash) {
		return adjacentHash(hash, Direction.RIGHT);
	}

	/**
	 * Returns the adjacent hash to the left.
	 * 
	 * @param hash
	 * @return
	 */
	public static String left(String hash) {
		return adjacentHash(hash, Direction.LEFT);
	}

	/**
	 * Returns the adjacent hash to the top.
	 * 
	 * @param hash
	 * @return
	 */
	public static String top(String hash) {
		return adjacentHash(hash, Direction.TOP);
	}

	/**
	 * Returns the adjacent hash to the bottom.
	 * 
	 * @param hash
	 * @return
	 */
	public static String bottom(String hash) {
		return adjacentHash(hash, Direction.BOTTOM);
	}

	/**
	 * Returns the adjacent hash N steps in the given {@link Direction}. A
	 * negative N will use the opposite {@link Direction}.
	 * 
	 * @param hash
	 * @param direction
	 * @param steps
	 * @return
	 */
	public static String adjacentHash(String hash, Direction direction,
			int steps) {
		if (steps < 0)
			return adjacentHash(hash, direction.opposite(), Math.abs(steps));
		else {
			String h = hash;
			for (int i = 0; i < steps; i++)
				h = adjacentHash(h, direction);
			return h;
		}
	}

	/**
	 * Returns a list of the 8 surrounding hashes for a given hash in order
	 * left,right,top,bottom,left-top,left-bottom,right-top,right-bottom.
	 * 
	 * @param hash
	 * @return
	 */
	public static List<String> neighbours(String hash) {
		List<String> list = Lists.newArrayList();
		String left = adjacentHash(hash, Direction.LEFT);
		String right = adjacentHash(hash, Direction.RIGHT);
		list.add(left);
		list.add(right);
		list.add(adjacentHash(hash, Direction.TOP));
		list.add(adjacentHash(hash, Direction.BOTTOM));
		list.add(adjacentHash(left, Direction.TOP));
		list.add(adjacentHash(left, Direction.BOTTOM));
		list.add(adjacentHash(right, Direction.TOP));
		list.add(adjacentHash(right, Direction.BOTTOM));
		return list;
	}

	/**
	 * Returns a geohash of length 12 for the given WGS84 point
	 * (latitude,longitude).
	 * 
	 * @param latitude
	 *            in decimal degrees (WGS84)
	 * @param longitude
	 *            in decimal degrees (WGS84)
	 * @return
	 */
	public static String encodeHash(double latitude, double longitude) {
		return encodeHash(latitude, longitude, 12);
	}

	/**
	 * Returns a geohash of given length for the given WGS84 point.
	 * 
	 * @param p
	 * @param length
	 * @return
	 */
	public static String encodeHash(LatLong p, int length) {
		return encodeHash(p.getLat(), p.getLon(), length);
	}

	/**
	 * Returns a geohash of given length for the given WGS84 point
	 * (latitude,longitude). If latitude is not between -90 and 90 throws an
	 * {@link IllegalArgumentException}.
	 * 
	 * @param latitude
	 *            in decimal degrees (WGS84)
	 * @param longitude
	 *            in decimal degrees (WGS84)
	 * @return
	 */
	public static String encodeHash(double latitude, double longitude,
			int length) {
		Preconditions.checkArgument(length > 0,
				"length must be greater than zero");
		Preconditions.checkArgument(latitude >= -90 && latitude <= 90,
				"latitude must be between -90 and 90 inclusive");
		longitude = to180(longitude);

		boolean isEven = true;
		double[] lat = new double[2];
		double[] lon = new double[2];
		int bit = 0;
		int ch = 0;
		StringBuilder geohash = new StringBuilder();

		lat[0] = -90.0;
		lat[1] = 90.0;
		lon[0] = -180.0;
		lon[1] = 180.0;

		while (geohash.length() < length) {
			if (isEven) {
				double mid = (lon[0] + lon[1]) / 2;
				if (longitude > mid) {
					ch |= BITS[bit];
					lon[0] = mid;
				} else
					lon[1] = mid;
			} else {
				double mid = (lat[0] + lat[1]) / 2;
				if (latitude > mid) {
					ch |= BITS[bit];
					lat[0] = mid;
				} else
					lat[1] = mid;
			}

			isEven = !isEven;
			if (bit < 4)
				bit++;
			else {
				geohash.append(BASE32.charAt(ch));
				bit = 0;
				ch = 0;
			}
		}
		return geohash.toString();
	}

	/**
	 * Returns a latitude,longitude pair as the centre of the given geohash.
	 * Latitude will be between -90 and 90 and longitude between -180 and 180.
	 * 
	 * @param geohash
	 * @return
	 */
	public static LatLong decodeHash(String geohash) {
		Preconditions.checkNotNull(geohash, "geohash cannot be null");
		boolean isEven = true;
		double[] lat = new double[2];
		double[] lon = new double[2];
		lat[0] = -90.0;
		lat[1] = 90.0;
		lon[0] = -180.0;
		lon[1] = 180.0;

		for (int i = 0; i < geohash.length(); i++) {
			char c = geohash.charAt(i);
			int cd = BASE32.indexOf(c);
			for (int j = 0; j < 5; j++) {
				int mask = BITS[j];
				if (isEven) {
					refineInterval(lon, cd, mask);
				} else {
					refineInterval(lat, cd, mask);
				}
				isEven = !isEven;
			}
		}
		double resultLat = (lat[0] + lat[1]) / 2;
		double resultLon = (lon[0] + lon[1]) / 2;

		return new LatLong(resultLat, resultLon);
	}

	/**
	 * Refines interval by a factor or 2 in either the 0 or 1 ordinate.
	 * 
	 * @param interval
	 * @param cd
	 * @param mask
	 */
	private static void refineInterval(double[] interval, int cd, int mask) {
		if ((cd & mask) != 0)
			interval[0] = (interval[0] + interval[1]) / 2;
		else
			interval[1] = (interval[0] + interval[1]) / 2;
	}

	/**
	 * Returns the maximum length of hash that encloses the bounding box. If no
	 * hash can enclose the bounding box then 0 is returned.
	 * 
	 * @param topLeftLat
	 * @param topLeftLon
	 * @param bottomRightLat
	 * @param bottomRightLon
	 * @return
	 */
	public static int hashLengthToEncloseBoundingBox(double topLeftLat,
			double topLeftLon, double bottomRightLat, double bottomRightLon) {
		for (int i = MAX_HASH_LENGTH; i >= 1; i--) {
			String hash = encodeHash(topLeftLat, topLeftLon, i);
			if (hashContains(hash, bottomRightLat, bottomRightLon))
				return i;
		}
		return 0;
	}

	/**
	 * Returns true if and only if the bounding box corresponding to the hash
	 * contains the given lat and long.
	 * 
	 * @param hash
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static boolean hashContains(String hash, double lat, double lon) {
		LatLong centre = decodeHash(hash);
		// TODO normalize longitudes?
		return Math.abs(centre.getLat() - lat) <= heightDegrees(hash.length()) / 2
				&& Math.abs(centre.getLon() - lon) <= widthDegrees(hash
						.length()) / 2;
	}

	/**
	 * Returns the hashes that are required to cover the given bounding box.
	 * Hash length is chosen as the length for a single hash to cover the
	 * bounding box +1.
	 * 
	 * @param topLeftLat
	 * @param topLeftLon
	 * @param bottomRightLat
	 * @param bottomRightLon
	 * @return
	 */
	public static Coverage coverBoundingBox(double topLeftLat,
			final double topLeftLon, final double bottomRightLat,
			final double bottomRightLon) {
		int length = hashLengthToEncloseBoundingBox(topLeftLat, topLeftLon,
				bottomRightLat, bottomRightLon) + 1;
		return coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat,
				bottomRightLon, length);
	}

	/**
	 * Returns the hashes of given length that are required to cover the given
	 * bounding box.
	 * 
	 * @param topLeftLat
	 * @param topLeftLon
	 * @param bottomRightLat
	 * @param bottomRightLon
	 * @param length
	 * @return
	 */
	public static Coverage coverBoundingBox(double topLeftLat,
			final double topLeftLon, final double bottomRightLat,
			final double bottomRightLon, final int length) {
		Preconditions.checkArgument(length > 0,
				"length must be greater than zero");
		final double actualWidthDegreesPerHash = widthDegrees(length);
		final double actualHeightDegreesPerHash = heightDegrees(length);

		Set<String> hashes = Sets.newTreeSet();
		double diff = longitudeDiff(bottomRightLon, topLeftLon);
		double maxLon = topLeftLon + diff;

		for (double lat = bottomRightLat; lat <= topLeftLat; lat += actualHeightDegreesPerHash) {
			for (double lon = topLeftLon; lon <= maxLon; lon += actualWidthDegreesPerHash) {
				addHash(hashes, lat, lon, length);
			}
		}
		// ensure have the borders covered
		for (double lat = bottomRightLat; lat <= topLeftLat; lat += actualHeightDegreesPerHash) {
			addHash(hashes, lat, maxLon, length);
		}
		for (double lon = topLeftLon; lon <= maxLon; lon += actualWidthDegreesPerHash) {
			addHash(hashes, topLeftLat, lon, length);
		}
		// ensure that the topRight corner is covered
		addHash(hashes, topLeftLat, maxLon, length);

		double areaDegrees = diff * (topLeftLat - bottomRightLat);
		double coverageAreaDegrees = hashes.size() * widthDegrees(length)
				* heightDegrees(length);
		double ratio = areaDegrees / coverageAreaDegrees;
		return new Coverage(hashes, ratio);
	}

	/**
	 * Add hash of the given length for a lat long point to a set.
	 * 
	 * @param hashes
	 * @param lat
	 * @param lon
	 * @param length
	 */
	private static void addHash(Set<String> hashes, double lat, double lon,
			int length) {
		hashes.add(encodeHash(lat, lon, length));
	}

	/**
	 * Returns the difference between two longitude values. The returned value
	 * is always >=0.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	@VisibleForTesting
	static double longitudeDiff(double a, double b) {
		a = to180(a);
		b = to180(b);
		if (a < b)
			return a - b + 360;
		else
			return a - b;
	}

	/**
	 * Converts an angle in degrees to range -180< x <= 180.
	 * 
	 * @param d
	 * @return
	 */
	@VisibleForTesting
	static double to180(double d) {
		if (d < 0)
			return -to180(Math.abs(d));
		else {
			if (d > 180) {
				long n = Math.round(Math.floor((d + 180) / 360.0));
				return d - n * 360;
			} else
				return d;
		}
	}

	private static Map<Integer, Double> hashHeightCache = Maps.newHashMap();

	/**
	 * Returns height in degrees of all geohashes of length n. Results are
	 * deterministic and cached to increase performance.
	 * 
	 * @param n
	 * @return
	 */
	public static double heightDegrees(int n) {
		if (hashHeightCache.get(n) == null) {
			double a;
			if (n % 2 == 0)
				a = -1;
			else
				a = -0.5;
			double result = 90 / Math.pow(2, 2.5 * n + a);
			hashHeightCache.put(n, result);
		}
		return hashHeightCache.get(n);
	}

	private static Map<Integer, Double> hashWidthCache = Maps.newHashMap();

	/**
	 * Returns width in degrees of all geohashes of length n. Results are
	 * deterministic and cached to increase performance.
	 * 
	 * @param n
	 * @return
	 */
	public static double widthDegrees(int n) {
		if (hashWidthCache.get(n) == null) {
			double a;
			if (n % 2 == 0)
				a = -1;
			else
				a = -0.5;
			double result = 180 / Math.pow(2, 2.5 * n + a);
			hashWidthCache.put(n, result);
		}
		return hashWidthCache.get(n);
	}

	/**
	 * Returns a String of lines of hashes to represent the relative positions
	 * of hashes on a map. The grid is of height and width 2*size centred around
	 * the given hash. Highlighted hashes are displayed in upper case.
	 * 
	 * @param hash
	 * @param size
	 * @param highlightThese
	 * @return
	 */
	public static String gridToString(String hash, int size,
			Set<String> highlightThese) {
		return gridToString(hash, -size, -size, size, size, highlightThese);
	}

	/**
	 * Returns a String of lines of hashes to represent the relative positions
	 * of hashes on a map.
	 * 
	 * @param hash
	 * @param fromRight
	 *            top left of the grid in hashes to the right (can be negative).
	 * @param fromBottom
	 *            top left of the grid in hashes to the bottom (can be
	 *            negative).
	 * @param toRight
	 *            bottom righth of the grid in hashes to the bottom (can be
	 *            negative).
	 * @param toBottom
	 *            bottom right of the grid in hashes to the bottom (can be
	 *            negative).
	 * @return
	 */
	public static String gridToString(String hash, int fromRight,
			int fromBottom, int toRight, int toBottom) {
		return gridToString(hash, fromRight, fromBottom, toRight, toBottom,
				Collections.<String> emptySet());
	}

	/**
	 * Returns a String of lines of hashes to represent the relative positions
	 * of hashes on a map. Highlighted hashes are displayed in upper case.
	 * 
	 * @param hash
	 * @param fromRight
	 *            top left of the grid in hashes to the right (can be negative).
	 * @param fromBottom
	 *            top left of the grid in hashes to the bottom (can be
	 *            negative).
	 * @param toRight
	 *            bottom righth of the grid in hashes to the bottom (can be
	 *            negative).
	 * @param toBottom
	 *            bottom right of the grid in hashes to the bottom (can be
	 *            negative).
	 * @param highlightThese
	 * @return
	 */
	public static String gridToString(String hash, int fromRight,
			int fromBottom, int toRight, int toBottom,
			Set<String> highlightThese) {
		StringBuilder s = new StringBuilder();
		for (int bottom = fromBottom; bottom <= toBottom; bottom++) {
			for (int right = fromRight; right <= toRight; right++) {
				String h = adjacentHash(hash, Direction.RIGHT, right);
				h = adjacentHash(h, Direction.BOTTOM, bottom);
				if (highlightThese.contains(h))
					h = h.toUpperCase();
				s.append(h).append(" ");
			}
			s.append("\n");
		}
		return s.toString();
	}

}
