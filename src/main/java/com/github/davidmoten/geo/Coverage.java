package com.github.davidmoten.geo;

import java.util.Set;
import java.util.TreeSet;

/**
 * A set of hashes and a measure of how well those hashes cover a region.
 * Immutable.
 * 
 * @author dave
 * 
 */
public class Coverage {

    /**
     * The hashes providing the coverage.
     */
    private final Set<String> hashes;

    /**
     * How well the coverage is covered by the hashes. Will be >=1. Closer to 1
     * the close the coverage is to the region in question.
     */
    private final double ratio;

    /**
     * Constructor.
     * 
     * @param hashes
     *            set of hashes comprising the coverage
     * @param ratio
     *            ratio of area of hashes to the area of target region
     */
    public Coverage(Set<String> hashes, double ratio) {
        super();
        this.hashes = hashes;
        this.ratio = ratio;
    }

    Coverage(CoverageLongs coverage) {
        this.ratio = coverage.getRatio();
        this.hashes = new TreeSet<String>();
        for(Long l : coverage.getHashes()) {
            hashes.add(GeoHash.fromLongToString(l));
        }
    }

/**
     * Returns the hashes which are expected to be all of the same length.
     * 
     * @return set of hashes
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
     * @return ratio of area of hashes to area of target region.
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Returns the length in characters of the first hash returned by an
     * iterator on the hash set. All hashes should be of the same length in this
     * coverage.
     * 
     * @return length of the hash
     */
    public int getHashLength() {
        if (hashes.size() == 0)
            return 0;
        else
            return hashes.iterator().next().length();
    }

    @Override
    public String toString() {
        return "Coverage [hashes=" + hashes + ", ratio=" + ratio + "]";
    }
}
