package org.cytoscape.paperwing.internal.cytoscape.processing;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class CameraCytoscapeDataSubprocessor implements
		CytoscapeDataSubprocessor {

	@Override
	public void processCytoscapeData(GraphicsData graphicsData) {
		// Check if the network's scale factor was changed. If so, update the camera's position
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		double minDifference = 1e-15;
		double scaleFactor = networkView.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR);
		
		if (scaleFactor - minDifference > 1) {
			graphicsData.getCamera().zoomIn();
			
		} else if (scaleFactor + minDifference < 1) {
			graphicsData.getCamera().zoomOut();
		}
		
		networkView.setVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR, 1.0);
	}

	
}
