package com.github.davidmoten.geo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class Benchmarks {

    private final LatLong centre = GeoHash.decodeHash("dre7");

    @Benchmark
    public void hashContains() {
        GeoHash.hashContains("dre7", centre.getLat(), centre.getLon());
    }
    
    @Benchmark
    public void decodeHash() {
        GeoHash.decodeHash("dre7");
    }
    
}
