package com.github.davidmoten.geo;

/**
 * Directions on a WGS84 projection. top = north, bottom = south, left = west,
 * right = east.
 * 
 * @author dave
 * 
 */
public enum Direction {
    BOTTOM, TOP, LEFT, RIGHT;

    /**
     * Returns the opposite direction. For example LEFT.opposite() == RIGHT.
     * 
     * @return the opposite direction
     */
    public Direction opposite() {
        if (this == BOTTOM)
            return TOP;
        else if (this == TOP)
            return BOTTOM;
        else if (this == LEFT)
            return RIGHT;
        else
            return LEFT;
    }
}