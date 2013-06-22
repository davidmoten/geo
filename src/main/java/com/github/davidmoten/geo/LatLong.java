package com.github.davidmoten.geo;

public class LatLong {

	private final double lat;
	private final double lon;

	public LatLong(double lat, double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public LatLong add(double deltaLat, double deltaLon) {
		return new LatLong(lat + deltaLat, lon + deltaLon);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LatLong [lat=");
		builder.append(lat);
		builder.append(", lon=");
		builder.append(lon);
		builder.append("]");
		return builder.toString();
	}

}
