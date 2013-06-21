package com.github.davidmoten.geo;

public enum Direction {
	BOTTOM, TOP, LEFT, RIGHT;

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