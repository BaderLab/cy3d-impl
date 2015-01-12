package org.baderlab.cy3d.internal.data;

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

}
