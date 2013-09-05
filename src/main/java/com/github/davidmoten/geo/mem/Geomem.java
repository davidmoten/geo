package com.github.davidmoten.geo.mem;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.github.davidmoten.geo.Base32;
import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Provides fast concurrent querying using in memory Concurrent data with time
 * and position.
 * 
 * @author dxm
 * 
 * @param <T>
 */
public class Geomem<T> {

    private final Map<Long, SortedMap<Long, Info<T>>> map = Maps
            .newConcurrentMap();

    public Geomem(Optional<Integer> maxSize) {
        // TODO implement maxSize
    }

    public Iterable<Info<T>> find(double topLeftLat, double topLeftLong,
            double bottomRightLat, double bottomRightLong, long start,
            long finish) {

        Coverage cover = GeoHash.coverBoundingBox(topLeftLat, topLeftLong,
                bottomRightLat, bottomRightLong);
        Iterable<Info<T>> it = Collections.emptyList();
        for (String hash : cover.getHashes()) {
            it = Iterables.concat(
                    it,
                    find(topLeftLat, topLeftLong, bottomRightLat,
                            bottomRightLong, start, finish, hash));
        }
        return it;
    }

    public Iterable<Info<T>> find(final double topLeftLat,
            final double topLeftLong, final double bottomRightLat,
            final double bottomRightLong, long start, long finish,
            String withinHash) {

        Iterable<Info<T>> it = find(start, finish, withinHash);
        return Iterables.filter(it, new Predicate<Info<T>>() {

            @Override
            public boolean apply(Info<T> info) {
                return info.lat() >= bottomRightLat && info.lat() <= topLeftLat
                        && info.lon() >= topLeftLong
                        && info.lon() <= bottomRightLong;
            }
        });
    }

    public Iterable<Info<T>> find(long start, long finish, String withinHash) {
        long key = Base32.decodeBase32(withinHash);
        SortedMap<Long, Info<T>> sortedByTime = map.get(key);
        return sortedByTime.subMap(start, finish).values();
    }

    public void add(double lat, double lon, long time, T t, long expiryTime) {
        String hash = GeoHash.encodeHash(lat, lon);
        // full hash length is 12 so this will insert 12 entries
        for (int i = 1; i <= hash.length(); i++) {
            long key = Base32.decodeBase32(hash.substring(0, i));
            if (map.get(key) == null) {
                map.put(key, new ConcurrentSkipListMap<Long, Info<T>>());
            }
            map.get(key).put(time, new Info<T>(key, lat, lon, time, t));
        }
    }

}
