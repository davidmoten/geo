package com.github.davidmoten.geo.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Guava like.
 */
public final class Maps {

    private Maps() {
    }

    public static <T, D> Map<T, D> newHashMap() {
       return new HashMap<T, D>();
    }
}
