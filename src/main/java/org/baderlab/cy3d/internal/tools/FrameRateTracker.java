package org.baderlab.cy3d.internal.tools;

import javax.media.opengl.FPSCounter;

/**
 * This class represents a framerate tracker, which is capable of keeping track
 * of a set of frame times in order to calculate the average framerate.
 */
public class FrameRateTracker {
	
	// Calcualtes the average FPS every 30 frames.
	private static final int FRAME_INTERVAL = 30;
	
	private final FPSCounter animator;
	
	
	public FrameRateTracker(FPSCounter animator) {
		this.animator = animator;
	}

	/**
	 * Return the current frames per second.
	 */
	public float getFPS() {
		return animator.getLastFPS();
	}
	
	/**
	 * Returns the total number of frames that were rendered since the animator was started.
	 */
	public int getTotalFrames() {
		return animator.getTotalFPSFrames();
	}
	
	/**
	 * Starts tracking FPS. Note that total frames is always tracked even if this method isn't called.
	 */
	public void startTrackingFPS() {
		animator.setUpdateFPSFrames(FRAME_INTERVAL, null);
	}
	
	/**
	 * Stops tracking FPS. Note that total frames will still be tracked.
	 */
	public void stopTrackingFPS() {
		animator.setUpdateFPSFrames(0, null);
	}
 
	
//	public static int DEFAULT_FRAMES_TRACKED = 30;
//	
//	private int framesTracked = DEFAULT_FRAMES_TRACKED;
//
//	private Queue<Long> frameTimes;
//	
//	public FrameRateTracker() {
//		frameTimes = new LinkedList<Long>();
//	}
//	
//	/**
//	 * Set the number of frames used to perform the frames-per-second calculation.
//	 * @param framesTracked The number of frames before the current frame to use for calculating framerate.
//	 */
//	public void setFramesTracked(int framesTracked) {
//		this.framesTracked = framesTracked;
//		
//		while (frameTimes.size() > framesTracked) {
//			frameTimes.remove();
//		}
//	}
//	
//	public void advanceFrame() {
//		frameTimes.add(System.nanoTime());
//		
//		while (frameTimes.size() > framesTracked) {
//			frameTimes.remove();
//		}
//	}
//	
//	/**
//	 * Return the average framerate over the tracked number of frames.
//	 * @return The average framerate over the currently tracked number of frames.
//	 */
//	public double getFrameRate() {
//		Long startTime = frameTimes.peek();
//		Long endTime = ((LinkedList<Long>) frameTimes).peekLast();
//
//		if (startTime == endTime) {
//			// Prevent division by 0
//			return 0;
//		} else {
//			return frameTimes.size() / ((double) (endTime - startTime) / 1000000000);
//		}
//	}
}
