package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.input.handler.MouseButton;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;

public class SelectionReleaseCommand implements MouseCommand {
	
	private final GraphicsData graphicsData;
	
	public SelectionReleaseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Override
	public void command(MouseButton button, int x, int y) {
		if(button == MouseButton.BUTTON_1) {
			GraphicsSelectionData selectionData = graphicsData.getSelectionData();
			CyNetworkView networkView = graphicsData.getNetworkView();
			selectionData.setDragSelectMode(false);
			selectionData.setSelectTopLeftFound(false);
			
			for (long index : graphicsData.getPickingData().getPickedNodeIndices()) {
				NetworkToolkit.setNodeSelected(index, networkView, true);
			}
			
			for (long index : graphicsData.getPickingData().getPickedEdgeIndices()) {
				NetworkToolkit.setEdgeSelected(index, networkView, true);
			}
			
			/*
			selectionData.getSelectedNodeIndices().addAll(graphicsData.getPickingData().getPickedNodeIndices());
			selectionData.getSelectedEdgeIndices().addAll(graphicsData.getPickingData().getPickedEdgeIndices());
			*/
		}
	}

}
