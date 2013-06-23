package com.github.davidmoten.geo;

/**
 * A lat, long pair (WGS84).
 * 
 * @author dave
 * 
 */
public class LatLong {

	private final double lat;
	private final double lon;

	/**
	 * Constructor.
	 * 
	 * @param lat
	 * @param lon
	 */
	public LatLong(double lat, double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * Returns the latitude in decimal degrees.
	 * 
	 * @return
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * Returns the longitude in decimal degrees.
	 * 
	 * @return
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * Returns a new {@link LatLong} object with lat, lon increased by deltaLat,
	 * deltaLon.
	 * 
	 * @param deltaLat
	 * @param deltaLon
	 * @return
	 */
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
