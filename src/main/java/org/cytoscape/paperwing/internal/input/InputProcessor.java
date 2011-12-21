package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;

public interface InputProcessor {
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData);
}
