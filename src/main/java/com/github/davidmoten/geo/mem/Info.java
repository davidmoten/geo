package com.github.davidmoten.geo.mem;

public class Info<T> {

    private final long hash;
    private final double lat;
    private final double lon;
    private final long time;
    private final T value;

    public Info(long hash, double lat, double lon, long time, T value) {
        super();
        this.hash = hash;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.value = value;
    }

    public long getHash() {
        return hash;
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
        return "Info [hash=" + hash + ", lat=" + lat + ", lon=" + lon
                + ", time=" + time + ", value=" + value + "]";
    }

}
