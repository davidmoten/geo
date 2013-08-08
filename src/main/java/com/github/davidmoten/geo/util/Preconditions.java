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

    public static void checkNotNull(Object object, String s) {
        if (object == null) {
            throw new NullPointerException(s);
        }
    }
}
