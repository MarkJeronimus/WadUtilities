package wadapi.structure;

/**
 * Subsector, as generated by BSP.
 *
 * @author Zom-B
 */
// Created 2011-08-15
public class Subsector {
	private int numSegs;
	// Index of first seg, segs are stored sequentially.
	private int firstSeg;

	public Subsector(int numSegs, int firstSeg) {
		this.numSegs = numSegs;
		this.firstSeg = firstSeg;
	}

	public int getNumSegs() {
		return numSegs;
	}

	public int getFirstSeg() {
		return firstSeg;
	}

	@Override
	public String toString() {
		return "[" + numSegs + ", " + firstSeg + ']';
	}
}
