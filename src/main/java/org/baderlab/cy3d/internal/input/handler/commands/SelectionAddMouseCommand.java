package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;

public class SelectionAddMouseCommand implements MouseCommand {

	private final GraphicsData graphicsData;
	
	public SelectionAddMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Override
	public void clicked(int x, int y) {
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
	
	
	// Drag movement
	
	@Override
	public void pressed(int x, int y) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		selectionData.setSelectTopLeftX(x);
		selectionData.setSelectTopLeftY(y);
		selectionData.setSelectTopLeftFound(true);
	}

	@Override
	public void dragged(int x, int y) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		selectionData.setSelectBottomRightX(x);
		selectionData.setSelectBottomRightY(y);
		
		if (Math.abs(selectionData.getSelectTopLeftX() - x) >= 1 && Math.abs(selectionData.getSelectTopLeftY() - y) >= 1) {
			selectionData.setDragSelectMode(true);
		}
	}

	@Override
	public void released(int x, int y) {
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
	
	@Override
	public MouseCommand modify() {
		return this;
	}

}
