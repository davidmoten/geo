package com.github.davidmoten.geo;

import java.util.Set;

/**
 * A set of hashes and a measure of how well those hashes cover a region.
 * Immutable.
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
	 * Returns the hashes which are expected to be all of the same length.
	 * 
	 * @return
	 */
	public Set<String> getHashes() {
		return hashes;
	}

	/**
	 * Returns the measure of how well the hashes cover a region. The ratio is
	 * the total area of hashes divided by the area of the bounding box in
	 * degrees squared. The closer the ratio is to 1 the better the more closely
	 * the hashes approximate the bounding box.
	 * 
	 * @return
	 */
	public double getRatio() {
		return ratio;
	}

	/**
	 * Returns the length in characters of the first hash returned by an
	 * iterator on the hash set. All hashes should be of the same length in this
	 * coverage.
	 * 
	 * @return
	 */
	public int getHashLength() {
		if (hashes.size() == 0)
			return 0;
		else
			return hashes.iterator().next().length();
	}
}
