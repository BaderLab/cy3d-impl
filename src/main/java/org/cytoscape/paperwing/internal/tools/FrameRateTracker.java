package org.cytoscape.paperwing.internal.tools;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a framerate tracker, which is capable of keeping track
 * of a set of frame times in order to calculate the average framerate.
 */
public class FrameRateTracker {
	
	public static int DEFAULT_FRAMES_TRACKED = 30;
	
	private int framesTracked = DEFAULT_FRAMES_TRACKED;

	private Queue<Long> frameTimes;
	
	public FrameRateTracker() {
		frameTimes = new LinkedList<Long>();
	}
	
	/**
	 * Set the number of frames used to perform the frames-per-second calculation.
	 * @param framesTracked The number of frames before the current frame to use for calculating framerate.
	 */
	public void setFramesTracked(int framesTracked) {
		this.framesTracked = framesTracked;
		
		while (frameTimes.size() > framesTracked) {
			frameTimes.remove();
		}
	}
	
	public void advanceFrame() {
		frameTimes.add(System.nanoTime());
		
		while (frameTimes.size() > framesTracked) {
			frameTimes.remove();
		}
	}
	
	/**
	 * Return the average framerate over the tracked number of frames.
	 * @return The average framerate over the currently tracked number of frames.
	 */
	public double getFrameRate() {
		Long startTime = frameTimes.peek();
		Long endTime = ((LinkedList<Long>) frameTimes).peekLast();

		if (startTime == endTime) {
			// Prevent division by 0
			return 0;
		} else {
			return frameTimes.size() / ((double) (endTime - startTime) / 1000000000);
		}
	}
}
