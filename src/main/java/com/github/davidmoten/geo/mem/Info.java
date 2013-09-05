package com.github.davidmoten.geo.mem;

import com.google.common.base.Optional;

public class Info<T, R> {

    private final double lat;
    private final double lon;
    private final long time;
    private final T value;
    private final Optional<R> id;

    public Info(double lat, double lon, long time, T value, Optional<R> id) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.value = value;
        this.id = id;
    }

    public Optional<R> id() {
        return id;
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }

    public long time() {
        return time;
    }

    public T value() {
        return value;
    }

    @Override
    public String toString() {
        return "Info [lat=" + lat + ", lon=" + lon + ", time=" + time
                + ", value=" + value + ", id=" + id + "]";
    }

}
