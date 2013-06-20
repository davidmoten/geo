package com.github.davidmoten.geo;

import java.util.Map;

import com.google.common.collect.Maps;

public class GeoHash {

	public static enum Direction {
		BOTTOM, TOP, LEFT, RIGHT;
	}

	public static enum Parity {
		EVEN, ODD;
	}

	private static final int[] BITS = new int[] { 16, 8, 4, 2, 1 };
	private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
	private static final Map<Direction, Map<Parity, String>> NEIGHBOURS = createNeighbours();
	private static final Map<Direction, Map<Parity, String>> BORDERS = createBorders();

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

		m.get(Direction.BOTTOM).put(Parity.ODD,
				m.get(Direction.LEFT).get(Parity.EVEN));
		m.get(Direction.TOP).put(Parity.ODD,
				m.get(Direction.RIGHT).get(Parity.EVEN));
		m.get(Direction.LEFT).put(Parity.ODD,
				m.get(Direction.BOTTOM).get(Parity.EVEN));
		m.get(Direction.RIGHT).put(Parity.ODD,
				m.get(Direction.TOP).get(Parity.EVEN));
		return m;
	}

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
		m.get(Direction.BOTTOM).put(Parity.ODD,
				m.get(Direction.LEFT).get(Parity.EVEN));
		m.get(Direction.TOP).put(Parity.ODD,
				m.get(Direction.RIGHT).get(Parity.EVEN));
		m.get(Direction.LEFT).put(Parity.ODD,
				m.get(Direction.BOTTOM).get(Parity.EVEN));
		m.get(Direction.RIGHT).put(Parity.ODD,
				m.get(Direction.TOP).get(Parity.EVEN));

		return m;
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

	public static String encodeHash(double latitude, double longitude) {
		return encodeHash(latitude, longitude, 12);
	}

	public static String encodeHash(double latitude, double longitude,
			int length) {
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

	public static LatLong decodeHash(String geohash) {
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
					refine_interval(lon, cd, mask);
				} else {
					refine_interval(lat, cd, mask);
				}
				isEven = !isEven;
			}
		}
		double resultLat = (lat[0] + lat[1]) / 2;
		double resultLon = (lon[0] + lon[1]) / 2;

		return new LatLong(resultLat, resultLon);
	}

	private static void refine_interval(double[] interval, int cd, int mask) {
		if ((cd & mask) != 0)
			interval[0] = (interval[0] + interval[1]) / 2;
		else
			interval[1] = (interval[0] + interval[1]) / 2;
	}

}
