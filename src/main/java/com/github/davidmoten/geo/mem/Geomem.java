package com.github.davidmoten.geo.mem;

import static com.google.common.base.Optional.of;

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
 * Provides fast concurrent querying using in memory
 * {@link ConcurrentSkipListMap}s and geohash to store data with time and
 * position.
 * 
 * @author dxm
 * 
 * @param <T>
 */
public class Geomem<T, R> {

    private final Map<Long, SortedMap<Long, Info<T, R>>> mapByGeoHash = Maps
            .newConcurrentMap();

    private final Map<R, Map<Long, SortedMap<Long, Info<T, R>>>> mapById = Maps
            .newConcurrentMap();

    public Iterable<Info<T, R>> find(double topLeftLat, double topLeftLong,
            double bottomRightLat, double bottomRightLong, long start,
            long finish) {

        Coverage cover = GeoHash.coverBoundingBox(topLeftLat, topLeftLong,
                bottomRightLat, bottomRightLong);
        Iterable<Info<T, R>> it = Collections.emptyList();
        for (String hash : cover.getHashes()) {
            it = Iterables.concat(
                    it,
                    find(topLeftLat, topLeftLong, bottomRightLat,
                            bottomRightLong, start, finish, hash));
        }
        return it;
    }

    private Iterable<Info<T, R>> find(final double topLeftLat,
            final double topLeftLong, final double bottomRightLat,
            final double bottomRightLong, long start, long finish,
            String withinHash) {

        Iterable<Info<T, R>> it = find(start, finish, withinHash);
        return Iterables.filter(it, new Predicate<Info<T, R>>() {

            @Override
            public boolean apply(Info<T, R> info) {
                return info.lat() >= bottomRightLat && info.lat() <= topLeftLat
                        && info.lon() >= topLeftLong
                        && info.lon() <= bottomRightLong;
            }
        });
    }

    private Iterable<Info<T, R>> find(long start, long finish, String withinHash) {
        long key = Base32.decodeBase32(withinHash);
        SortedMap<Long, Info<T, R>> sortedByTime = mapByGeoHash.get(key);
        if (sortedByTime == null)
            return Collections.emptyList();
        else
            return sortedByTime.subMap(start, finish).values();
    }

    @SuppressWarnings("unchecked")
    public void add(double lat, double lon, long time, T t) {
        add(lat, lon, time, t, of((R) t));
    }

    public void add(double lat, double lon, long time, T t, R id) {
        add(lat, lon, time, t, of(id));
    }

    private void add(double lat, double lon, long time, T t, Optional<R> id) {
        String hash = GeoHash.encodeHash(lat, lon);
        // full hash length is 12 so this will insert 12 entries
        Info<T, R> info = new Info<T, R>(lat, lon, time, t, id);

        addToMap(mapByGeoHash, info, hash);
        addToMapById(mapById, info, hash);
    }

    private void addToMapById(
            Map<R, Map<Long, SortedMap<Long, Info<T, R>>>> mapById,
            Info<T, R> info, String hash) {
        if (info.id().isPresent()) {
            Map<Long, SortedMap<Long, Info<T, R>>> m = mapById.get(info.id()
                    .get());
            synchronized (mapByGeoHash) {
                if (m == null) {
                    m = Maps.newConcurrentMap();
                    mapById.put(info.id().get(), m);
                }
            }
            addToMap(m, info, hash);
        }
    }

    private void addToMap(Map<Long, SortedMap<Long, Info<T, R>>> map,
            Info<T, R> info, String hash) {

        for (int i = 1; i <= hash.length(); i++) {
            long key = Base32.decodeBase32(hash.substring(0, i));
            synchronized (map) {
                if (map.get(key) == null) {
                    map.put(key, new ConcurrentSkipListMap<Long, Info<T, R>>());
                }
            }
            map.get(key).put(info.time(), info);
        }
    }

}
