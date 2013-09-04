package com.github.davidmoten.geo.mem;

import java.util.Map;
import java.util.SortedMap;

import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class Geomem<T> {

	private final Map<String, SortedMap<Long, T>> map = Maps.newConcurrentMap();

	public Geomem(Optional<Integer> maxSize) {

	}

	public Iterable<T> find(double topLeftLat, double topLeftLong,
			double bottomRightLat, double bottomRightLong, long start,
			long finish) {

		Coverage cover = GeoHash.coverBoundingBox(topLeftLat, topLeftLong,
				bottomRightLat, bottomRightLong);
		cover.getHashes();
		return null;
	}

	public void add(double lat, double lon, long time, T t, long expiryTime) {
		String hash = GeoHash.encodeHash(lat, lon);
		for (int i = 1; i <= hash.length(); i++) {
			String key = hash.substring(0, i);
			if (map.get(key) == null) {
				map.put(key, Maps.<Long, T> newTreeMap());
			}
			map.get(key).put(time, t);
		}
	}
}
