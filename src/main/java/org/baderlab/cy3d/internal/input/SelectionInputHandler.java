package org.baderlab.cy3d.internal.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

public class SelectionInputHandler implements InputHandler {

	public static int NULL_COORDINATE = Integer.MIN_VALUE;
	public static int NO_INDEX = -1; 
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		//TODO: Check whether to have this method here or in MainCytoscapeDataProcessor
		// processClearToBeDeselected(selectionData);
		
		processDeselectOther(keys, mouse, graphicsData);
		processSingleSelection(keys, mouse, graphicsData);
		processDragSelection(keys, mouse, graphicsData);
		processClearHover(keys, mouse, graphicsData);
	}

	// Clear the set of to-be deselected nodes and edges
	private void processClearToBeDeselected(GraphicsSelectionData selectionData) {
		selectionData.getToBeDeselectedNodeIndices().clear();
		selectionData.getToBeDeselectedEdgeIndices().clear();
	}

	// Performs single selection, and deselection of previously selected objects
	private void processSingleSelection(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		long newHoverNodeIndex = graphicsData.getPickingData().getClosestPickedNodeIndex();
		long newHoverEdgeIndex = graphicsData.getPickingData().getClosestPickedEdgeIndex();
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();

		selectionData.setHoverNodeIndex(newHoverNodeIndex);
		selectionData.setHoverEdgeIndex(newHoverEdgeIndex);
		
		if (!selectionData.isDragSelectMode() && !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
				
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
				
				if (newHoverNodeIndex != NO_INDEX) {
					
					if (NetworkToolkit.checkNodeSelected(newHoverNodeIndex, networkView)) {
						// Deselect the node if it was already selected
						NetworkToolkit.setNodeSelected(newHoverNodeIndex, networkView, false);
					} else {
						// Select the node if it was not selected
						NetworkToolkit.setNodeSelected(newHoverNodeIndex, networkView, true);
					}
					
				} else if (newHoverEdgeIndex != NO_INDEX) {
		
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
	
	private void processDeselectOther(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();

		if (mouse.getPressed().contains(MouseEvent.BUTTON1)) { 
			if (!selectionData.isDragSelectMode() 
					&& !keys.getHeld().contains(KeyEvent.VK_SHIFT)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
					
				// Deselect currently selected nodes
				List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
				for (CyNode node : selectedNodes) {
					NetworkToolkit.setNodeSelected(node.getSUID(), networkView, false);
				}

				// Deselect currently selected edges
				List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(networkView.getModel(), "selected", true);
				for (CyEdge edge : selectedEdges) {
					NetworkToolkit.setEdgeSelected(edge.getSUID(), networkView, false);
				}
			}
		}
		
	}


	private void processDragSelection(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		if(!graphicsData.getSettingsData().isSelectMode())
			return;
		
		if (!keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
		
			// If the left button was clicked, prepare to select nodes/edges
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
				selectionData.setSelectTopLeftX(mouse.x());
				selectionData.setSelectTopLeftY(mouse.y());
				selectionData.setSelectTopLeftFound(true);
			}
		
			// Dragging
			if (mouse.getHeld().contains(MouseEvent.BUTTON1) && selectionData.isSelectTopLeftFound()) {
				selectionData.setSelectBottomRightX(mouse.x());
				selectionData.setSelectBottomRightY(mouse.y());
				
				if (Math.abs(selectionData.getSelectTopLeftX() - mouse.x()) >= 1 && Math.abs(selectionData.getSelectTopLeftY() - mouse.y()) >= 1) {
					selectionData.setDragSelectMode(true);
				}
			}
			
			// Disable drag selection upon mouse release
			if (mouse.getReleased().contains(MouseEvent.BUTTON1) && selectionData.isDragSelectMode()) {
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
	
	// Stop hovering if mouse exited
	private void processClearHover(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		if (mouse.hasExited()) {
			selectionData.setHoverNodeIndex(NO_INDEX);
			selectionData.setHoverEdgeIndex(NO_INDEX);
		}
	}
	
	

}
