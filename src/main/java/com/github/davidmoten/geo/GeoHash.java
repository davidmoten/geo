package com.github.davidmoten.geo;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Utility functions for geohashing. See http://en.wikipedia.org/wiki/Geohash.
 * Majority of code here based on javascript implementation at
 * https://github.com/davetroy/geohash-js.
 * 
 * @author dxm
 * 
 */
public class GeoHash {

	private static final int[] BITS = new int[] { 16, 8, 4, 2, 1 };
	private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
	private static final Map<Direction, Map<Parity, String>> NEIGHBOURS = createNeighbours();
	private static final Map<Direction, Map<Parity, String>> BORDERS = createBorders();

	/**
	 * Returns a map to be used in border calculations for hashes.
	 * 
	 * @return
	 */
	private static Map<Direction, Map<Parity, String>> createBorders() {
		Map<Direction, Map<Parity, String>> m = Maps.newHashMap();
		m.put(Direction.BOTTOM, Maps.<Parity, String> newHashMap());
		m.put(Direction.TOP, Maps.<Parity, String> newHashMap());
		m.put(Direction.LEFT, Maps.<Parity, String> newHashMap());
		m.put(Direction.RIGHT, Maps.<Parity, String> newHashMap());

		m.get(Direction.RIGHT).put(Parity.EVEN, "bcfguvyz");
		m.get(Direction.LEFT).put(Parity.EVEN, "0145hjnp");
		m.get(Direction.TOP).put(Parity.EVEN, "prxz");
		m.get(Direction.BOTTOM).put(Parity.EVEN, "028b");

		addOddParityEntries(m);
		return m;
	}

	/**
	 * Returns a map to be used in neighbour calculations for hashes.
	 * 
	 * @return
	 */
	private static Map<Direction, Map<Parity, String>> createNeighbours() {
		Map<Direction, Map<Parity, String>> m = Maps.newHashMap();
		m.put(Direction.BOTTOM, Maps.<Parity, String> newHashMap());
		m.put(Direction.TOP, Maps.<Parity, String> newHashMap());
		m.put(Direction.LEFT, Maps.<Parity, String> newHashMap());
		m.put(Direction.RIGHT, Maps.<Parity, String> newHashMap());

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
	 * Returns a geohash of given length for the given WGS84 point
	 * (latitude,longitude).
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

	private static void refineInterval(double[] interval, int cd, int mask) {
		if ((cd & mask) != 0)
			interval[0] = (interval[0] + interval[1]) / 2;
		else
			interval[1] = (interval[0] + interval[1]) / 2;
	}

}
