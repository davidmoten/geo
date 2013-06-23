package com.github.davidmoten.geo;

import java.util.Set;

/**
 * A set of hashes and a measure of how well those hashes cover a region.
 * 
 * @author dave
 * 
 */
public class Coverage {

	private final Set<String> hashes;
	private final double ratio;

	/**
	 * Constructor.
	 * 
	 * @param hashes
	 * @param ratio
	 */
	public Coverage(Set<String> hashes, double ratio) {
		super();
		this.hashes = hashes;
		this.ratio = ratio;
	}

	/**
	 * Returns the hashes.
	 * 
	 * @return
	 */
	public Set<String> getHashes() {
		return hashes;
	}

	/**
	 * Returns the measure of how well the hashes cover a region.
	 * 
	 * @return
	 */
	public double getRatio() {
		return ratio;
	}

}
