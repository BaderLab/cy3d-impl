package org.cytoscape.paperwing.internal.input;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.jogamp.newt.event.KeyEvent;

/**
 * {@link InputHandler} object used to handle debug-related input
 */
public class DebugInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		Set<Integer> pressed = keys.getPressed();
		
		if (pressed.contains(KeyEvent.VK_M)) {
			graphicsData.setUpdateScene(true);
		}
		
		// Toggle FPS display
		if (pressed.contains(KeyEvent.VK_K)) {
			graphicsData.setShowFPS(!graphicsData.getShowFPS());
		}
		
		// Toggle displaying all node labels or only for the selected/hovered nodes
		if (pressed.contains(KeyEvent.VK_L)) {
			graphicsData.setShowAllNodeLabels(!graphicsData.getShowAllNodeLabels());
		}
		
		// Reset node z-coordinate values to default
		if (pressed.contains(KeyEvent.VK_C)) {
			for (View<CyNode> nodeView : graphicsData.getNetworkView().getNodeViews()) {
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, 0.0);
			}
		}
	}

}
