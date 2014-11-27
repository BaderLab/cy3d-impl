package org.baderlab.cy3d.internal.input;

import org.baderlab.cy3d.internal.data.GraphicsData;

/**
 * An InputHandler is capable of processing a given {@link GraphicsData} object using the given keyboard
 * and mouse states. The processing done by an InputHandler is usually specific to a certain kind of input,
 * such as input relating to camera movement or node selectioni.
 */
public interface InputHandler {
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData);
}
