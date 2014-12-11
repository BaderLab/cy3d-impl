package org.baderlab.cy3d.internal.input;

import java.util.Set;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.jogamp.newt.event.KeyEvent;

/**
 * {@link InputHandler} object used to handle debug-related input
 */
public class DebugInputHandler implements InputHandler {

	// 1) KeyEvent constants are shorts and must be boxed as Integers for Set.contains() method.
	// 2) Avoid boxing these values on every event callback.
	
	private final Integer K = Integer.valueOf(KeyEvent.VK_K);
	private final Integer M = Integer.valueOf(KeyEvent.VK_M);
	private final Integer L = Integer.valueOf(KeyEvent.VK_L);
	private final Integer C = Integer.valueOf(KeyEvent.VK_C);
	
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		
		Set<Integer> pressed = keys.getPressed();
		
		if (pressed.contains(M)) {
			graphicsData.setUpdateScene(true);
		}
		
		// Toggle FPS display
		if (pressed.contains(K)) {
			graphicsData.setShowFPS(!graphicsData.getShowFPS());
		}
		
		// Toggle displaying all node labels or only for the selected/hovered nodes
		if (pressed.contains(L)) {
			graphicsData.setShowAllNodeLabels(!graphicsData.getShowAllNodeLabels());
		}
		
		// Reset node z-coordinate values to default
		if (pressed.contains(C)) {
			for (View<CyNode> nodeView : graphicsData.getNetworkView().getNodeViews()) {
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, 0.0);
			}
		}
	}

}
