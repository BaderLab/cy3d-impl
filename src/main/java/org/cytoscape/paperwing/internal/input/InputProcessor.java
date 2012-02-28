package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.data.GraphicsData;

/**
 * An InputProcessor is capable of using the given keyboard and mouse states to process
 * data in the given {@link GraphicsData} object for each frame of rendering. An
 * InputProcessor is responsible for all the input processing per frame.
 */
public interface InputProcessor {
	
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData);

}
