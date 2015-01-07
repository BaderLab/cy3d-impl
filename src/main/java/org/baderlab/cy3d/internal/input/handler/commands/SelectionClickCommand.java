package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.input.handler.MouseButton;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;

public class SelectionClickCommand implements MouseCommand {

	private final GraphicsData graphicsData;
	
	public SelectionClickCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Override
	public void command(MouseButton button, int x, int y) {
		if(button == MouseButton.BUTTON_1) {
			CyNetworkView networkView = graphicsData.getNetworkView();
			long newHoverNodeIndex = graphicsData.getPickingData().getClosestPickedNodeIndex();
			long newHoverEdgeIndex = graphicsData.getPickingData().getClosestPickedEdgeIndex();
			
			GraphicsSelectionData selectionData = graphicsData.getSelectionData();
	
			selectionData.setHoverNodeIndex(newHoverNodeIndex);
			selectionData.setHoverEdgeIndex(newHoverEdgeIndex);
			
			if (!selectionData.isDragSelectMode()) {
				if (newHoverNodeIndex != -1) {
					if (NetworkToolkit.checkNodeSelected(newHoverNodeIndex, networkView)) {
						// Deselect the node if it was already selected
						NetworkToolkit.setNodeSelected(newHoverNodeIndex, networkView, false);
					} else {
						// Select the node if it was not selected
						NetworkToolkit.setNodeSelected(newHoverNodeIndex, networkView, true);
					}
					
				} else if (newHoverEdgeIndex != -1) {
					if (NetworkToolkit.checkEdgeSelected(newHoverEdgeIndex, networkView)) {
						// Deselect the edge if it was already selected
						NetworkToolkit.setEdgeSelected(newHoverEdgeIndex, networkView, false);
					} else {
						// Select the edge if it was not selected
						NetworkToolkit.setEdgeSelected(newHoverEdgeIndex, networkView, true);
					}
				}
			}
		}
	}

}
