package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.data.GraphicsData;

public interface InputHandler {
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData);
}
