package com.github.davidmoten.geo.mem;

import static com.google.common.base.Optional.of;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.github.davidmoten.geo.Base32;
import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Provides fast concurrent querying using in memory
 * {@link ConcurrentSkipListMap}s and geohash to store data with time and
 * position. Depends on guava library.
 * 
 * @param <T>
 *            The type of the record with position and time.
 * @param <R>
 *            The type of the id of the record with position and time.
 */
public class Geomem<T, R> {

    /**
     * Maps from base32 geohash (long) to a map of time in epoch ms to
     * {@link Info}.
     */
    private final Map<Long, SortedMap<Long, Info<T, R>>> mapByGeoHash = Maps.newConcurrentMap();

    /**
     * Records a mapByGeoHash as above for each id of type R.
     */
    private final Map<R, Map<Long, SortedMap<Long, Info<T, R>>>> mapById = Maps.newConcurrentMap();

    /**
     * Returns as an {@link Iterable} the results of a search within the
     * bounding box given and where start &lt;=time &lt; finish.
     * 
     * @param topLeftLat
     *            latitude of top left point (north west)
     * @param topLeftLon
     *            longitude of top left point (north west)
     * @param bottomRightLat
     *            latitude of bottom right point (south east)
     * @param bottomRightLon
     *            longitude of bottom right point (south east)
     * @param start
     *            start time inclusive
     * @param finish
     *            finish time exclusive
     * @return info records
     */
    public Iterable<Info<T, R>> find(double topLeftLat, double topLeftLon, double bottomRightLat,
            double bottomRightLon, long start, long finish) {

        Coverage cover = GeoHash.coverBoundingBox(topLeftLat, topLeftLon, bottomRightLat,
                bottomRightLon);
        Iterable<Info<T, R>> it = Collections.emptyList();
        for (String hash : cover.getHashes()) {
            it = Iterables.concat(it, find(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon,
                    start, finish, hash));
        }
        return it;
    }

    /**
     * Returns an {@link Iterable} of {@link Info} being those records within
     * the bounding box, start<=time < finish and inside the geoHash withinHash.
     * 
     * Filters first on withinHash, then time, then bounding box.
     * 
     * @param topLeftLat
     *            latitude of top left point (north west)
     * @param topLeftLon
     *            longitude of top left point (north west)
     * @param bottomRightLat
     *            latitude of bottom right point (south east)
     * @param bottomRightLon
     *            longitude of bottom right point (south east)
     * @param start
     *            start time inclusive
     * @param finish
     *            finish time exclusive
     * @param withinHash
     *            items returned are within this hash
     * @return Info records
     */
    private Iterable<Info<T, R>> find(final double topLeftLat, final double topLeftLon,
            final double bottomRightLat, final double bottomRightLon, long start, long finish,
            String withinHash) {

        Iterable<Info<T, R>> it = find(start, finish, withinHash);
        return Iterables.filter(it,
                createRegionFilter(topLeftLat, topLeftLon, bottomRightLat, bottomRightLon));
    }

    /**
     * Returns a {@link Predicate} that returns true if and only if a point is
     * within the bounding box, exclusive of the top (north) and left (west)
     * edges.
     * 
     * @param topLeftLat
     *            latitude of top left point (north west)
     * @param topLeftLon
     *            longitude of top left point (north west)
     * @param bottomRightLat
     *            latitude of bottom right point (south east)
     * @param bottomRightLon
     *            longitude of bottom right point (south east)
     * @return predicate
     */
    @VisibleForTesting
    Predicate<Info<T, R>> createRegionFilter(final double topLeftLat, final double topLeftLon,
            final double bottomRightLat, final double bottomRightLon) {
        return new Predicate<Info<T, R>>() {

            @Override
            public boolean apply(Info<T, R> info) {
                return info.lat() >= bottomRightLat && info.lat() < topLeftLat
                        && info.lon() > topLeftLon && info.lon() <= bottomRightLon;
            }
        };
    }

    /**
     * Returns the {@link Info}s where start<=time <finish and position is
     * inside the geohash withinHash.
     * 
     * @param start
     *            start time inclusive
     * @param finish
     *            finish time exclusive
     * @param withinHash
     *            returned records are within hash
     * @return iterable
     */
    private Iterable<Info<T, R>> find(long start, long finish, String withinHash) {
        long key = Base32.decodeBase32(withinHash);
        SortedMap<Long, Info<T, R>> sortedByTime = mapByGeoHash.get(key);
        if (sortedByTime == null)
            return Collections.emptyList();
        else
            return sortedByTime.subMap(start, finish).values();
    }

    /**
     * Adds a record to the in-memory store with the given position and time. Id
     * is same as t.
     * 
     * @param lat
     *            latitiude
     * @param lon
     *            longitude
     * @param time
     *            time in epoch ms
     * @param t
     *            object
     */
    @SuppressWarnings("unchecked")
    public void add(double lat, double lon, long time, T t) {
        add(lat, lon, time, t, of((R) t));
    }

    /**
     * * Adds a record to the in-memory store with the given position and time
     * and id.
     * 
     * @param lat
     *            latitude
     * @param lon
     *            longitude
     * @param time
     *            time in epoch ms
     * @param t
     *            object
     * @param id
     *            identifier
     */
    public void add(double lat, double lon, long time, T t, R id) {
        add(lat, lon, time, t, of(id));
    }

    /**
     * Adds a record to the in-memory store with the given position and time and
     * id.
     * 
     * @param lat
     *            latitude
     * @param lon
     *            longitude
     * @param time
     *            time in epoch ms
     * @param t
     *            object
     * @param id
     *            identifier
     */
    public void add(double lat, double lon, long time, T t, Optional<R> id) {
        Info<T, R> info = new Info<T, R>(lat, lon, time, t, id);
        add(info);
    }

    /**
     * Adds a record to the in-memory store with the given position, time and
     * id.
     * 
     * @param info
     *            info record to add
     */
    public void add(Info<T, R> info) {
        String hash = GeoHash.encodeHash(info.lat(), info.lon());

        addToMap(mapByGeoHash, info, hash);
        addToMapById(mapById, info, hash);
    }

    private void addToMapById(Map<R, Map<Long, SortedMap<Long, Info<T, R>>>> mapById,
            Info<T, R> info, String hash) {
        if (info.id().isPresent()) {
            Map<Long, SortedMap<Long, Info<T, R>>> m = mapById.get(info.id().get());
            synchronized (mapByGeoHash) {
                if (m == null) {
                    m = Maps.newConcurrentMap();
                    mapById.put(info.id().get(), m);
                }
            }
            addToMap(m, info, hash);
        }
    }

    private void addToMap(Map<Long, SortedMap<Long, Info<T, R>>> map, Info<T, R> info,
            String hash) {

        // full hash length is 12 so this will insert 12 entries
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
