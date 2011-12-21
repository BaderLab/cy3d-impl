package org.cytoscape.paperwing.internal.input;

import java.awt.event.KeyEvent;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;
import org.cytoscape.paperwing.internal.graphics.GraphicsUtility;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class NetworkChangeInputProcessor implements InputProcessor {
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		Set<Integer> pressed = keys.getPressed();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
	}
	
	public static void processNetworkChangeInput() {
		
	}
	
	private static void processCreateNode(Set<Integer> pressed, CyNetworkView networkView) {
		 = graphicsData.getNetworkView();
		
		// Project mouse coordinates into 3d space to determine where to put the node
		Vector3 projection = GraphicsUtility.projectScreenCoordinates(
				mouse.x(), mouse.y(), graphicsData.getScreenWidth(),
				graphicsData.getScreenHeight(), camera.getDistance(), camera);
		
		// Create a new node
		if (pressed.contains(KeyEvent.VK_N)) {
			CyNode added = networkView.getModel().addNode();
			networkView.updateView();

			View<CyNode> viewAdded = networkView.getNodeView(added);

			double distanceScale = graphicsData.getDistanceScale();
			
			// TODO: Maybe throw an exception if viewAdded is null
			if (viewAdded != null) {
				viewAdded.setVisualProperty(
						RichVisualLexicon.NODE_X_LOCATION, projection.x()
								* distanceScale);
				viewAdded.setVisualProperty(
						RichVisualLexicon.NODE_Y_LOCATION, projection.y()
								* distanceScale);
				viewAdded.setVisualProperty(
						RichVisualLexicon.NODE_Z_LOCATION, projection.z()
								* distanceScale);

				// Set the node to be hovered
				// TODO: This might not be needed if the node were added
				// through some way other than the mouse
				graphicsData.setHoverNodeIndex(added.getIndex());
			}
		}
	}
	
	private static void processCreateEdge(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
	
	private static void processDeleteNode(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
	
	private void processDeleteEdge(Set<Integer> pressed, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		
	}
}
