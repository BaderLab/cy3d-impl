package org.baderlab.cy3d.internal.tools;

public class SUIDToolkit {


	/**
	 * Splits a 64-bit long into two 32-bit ints.
	 * @return A 2 element int array, index 0 is the upper part of the long, index 1 is the lower part.
	 */
	public static int[] splitLong(final long l) {
		int upper = (int) (l >>> 32);
		int lower = (int) (l);
		return new int[] { upper, lower };
	}
	
	/**
	 * Returns the upper 32 bits of a long.
	 */
	public static int upperInt(final long l) {
		return (int) (l >>> 32);
	}
	
	/**
	 * Returns the lower 32 bits of a long.
	 */
	public static int lowerInt(final long l) {
		return (int) l;
	}
	
	/**
	 * Combines two 32-bit ints into a 64-bit long.
	 */
	public static long combineInts(final int upper, final int lower) {
		long r = upper;
		r <<= 32;
		r |= (long)lower & 0xFFFFFFFFL;
		return r;
	}
	
}
