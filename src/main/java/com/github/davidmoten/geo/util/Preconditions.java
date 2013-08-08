package com.github.davidmoten.geo.util;

/**
 * Guava like Preconditions.
 */
public final class Preconditions {

    private Preconditions() {
    }

    public static void checkArgument(boolean b, String s) {
       if (!b) {
          throw new IllegalArgumentException(s);
        }
    }

    public static void checkNotNull(Object nullable, String s) {
        if (nullable == null) {
            throw new NullPointerException(s);
        }
    }
}
