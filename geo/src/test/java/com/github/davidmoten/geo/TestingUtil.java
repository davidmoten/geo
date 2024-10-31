package com.github.davidmoten.geo;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Utility methods for unit tests.
 * 
 * @author dave
 * 
 */
public class TestingUtil {

    /**
     * Checks that a class has a no-argument private constructor and calls that
     * constructor to instantiate the class.
     *
     * @param cls
     */
    public static <T> void callConstructorAndCheckIsPrivate(Class<T> cls) {

    }

    private static void assertTrue(boolean aPrivate) {
    }

}
