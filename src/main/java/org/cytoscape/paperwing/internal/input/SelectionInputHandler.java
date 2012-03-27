package org.cytoscape.paperwing.internal.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.layouts.SphericalLayoutAlgorithmTask;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.view.model.CyNetworkView;

public class SelectionInputHandler implements InputHandler {

	public static int NULL_COORDINATE = Integer.MIN_VALUE;
	public static int NO_INDEX = -1; 
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		//TODO: Check whether to have this method here or in MainCytoscapeDataProcessor
		processClearToBeDeselected(selectionData);
		
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
	private void processSingleSelection(KeyboardMonitor keys,
			MouseMonitor mouse, GraphicsData graphicsData) {
		int newHoverNodeIndex = graphicsData.getPickingData().getClosestPickedNodeIndex();
		int newHoverEdgeIndex = graphicsData.getPickingData().getClosestPickedEdgeIndex();
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();

		selectionData.setHoverNodeIndex(newHoverNodeIndex);
		selectionData.setHoverEdgeIndex(newHoverEdgeIndex);
		
		Set<Integer> selectedNodeIndices = selectionData.getSelectedNodeIndices();
		Set<Integer> selectedEdgeIndices = selectionData.getSelectedEdgeIndices();
	
		// These are needed to keep track of to-be removed objects for faster Cytoscape data processing
		Set<Integer> toBeDeselectedNodeIndices = selectionData.getToBeDeselectedNodeIndices();
		Set<Integer> toBeDeselectedEdgeIndices = selectionData.getToBeDeselectedEdgeIndices();
		
		if (!selectionData.isDragSelectMode()
				&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
				
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
				
				if (newHoverNodeIndex != NO_INDEX) {
					
					if (selectedNodeIndices.contains(newHoverNodeIndex)) {
						selectedNodeIndices.remove(newHoverNodeIndex);
						toBeDeselectedNodeIndices.add(newHoverNodeIndex);
						
					} else {
						selectedNodeIndices.add(newHoverNodeIndex);
						
						// Debug: Find size of biggest clique containing this node
						
						/*
						System.out.println("Size of clique: " + SphericalLayoutAlgorithmTask.findCliques(
								graphicsData.getNetworkView().getModel()).get(
										graphicsData.getNetworkView().getModel().getNode(newHoverNodeIndex)).size());
						*/
					}
					
				} else if (newHoverEdgeIndex != NO_INDEX) {
		
					if (selectedEdgeIndices.contains(newHoverEdgeIndex)) {
						selectedEdgeIndices.remove(newHoverEdgeIndex);
						toBeDeselectedEdgeIndices.add(newHoverEdgeIndex);
					} else {
						selectedEdgeIndices.add(newHoverEdgeIndex);
					}
				}
			}
		}
	}
	
	private void processDeselectOther(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		Set<Integer> selectedNodeIndices = selectionData.getSelectedNodeIndices();
		Set<Integer> selectedEdgeIndices = selectionData.getSelectedEdgeIndices();
		
		if (mouse.getPressed().contains(MouseEvent.BUTTON1)) { 
			if (!selectionData.isDragSelectMode() 
					&& !keys.getHeld().contains(KeyEvent.VK_SHIFT)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
					
					//&& graphicsData.getPickingData().getClosestPickedNodeIndex() == NO_INDEX
					//&& graphicsData.getPickingData().getClosestPickedEdgeIndex() == NO_INDEX) {
				
				selectionData.getToBeDeselectedNodeIndices().addAll(selectedNodeIndices);
				selectionData.getToBeDeselectedEdgeIndices().addAll(selectedEdgeIndices);
				
				selectedNodeIndices.clear();
				selectedEdgeIndices.clear();
			}
		}
		
	}


	private void processDragSelection(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		if (!keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
		
			// If the left button was clicked, prepare to select nodes/edges
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
				
				selectionData.setSelectTopLeftX(mouse.x());
				selectionData.setSelectTopLeftY(mouse.y());
				
				selectionData.setSelectTopLeftFound(true);
			}
		
			// Dragging
			if (mouse.getHeld().contains(MouseEvent.BUTTON1)
					&& selectionData.isSelectTopLeftFound()) {
				selectionData.setSelectBottomRightX(mouse.x());
				selectionData.setSelectBottomRightY(mouse.y());
				
				if (Math.abs(selectionData.getSelectTopLeftX() - mouse.x()) >= 1
						&& Math.abs(selectionData.getSelectTopLeftY() - mouse.y()) >= 1) {
					selectionData.setDragSelectMode(true);
				}
			}
			
			// Disable drag selection upon mouse release
			if (mouse.getReleased().contains(MouseEvent.BUTTON1)
					&& selectionData.isDragSelectMode()) {
				selectionData.setDragSelectMode(false);
				selectionData.setSelectTopLeftFound(false);
				
				selectionData.getSelectedNodeIndices().addAll(graphicsData.getPickingData().getPickedNodeIndices());
				selectionData.getSelectedEdgeIndices().addAll(graphicsData.getPickingData().getPickedEdgeIndices());
				
				
			}
		}
	}
	
	// Stop hovering if mouse exited
	private void processClearHover(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		if (mouse.hasExited()) {
			selectionData.setHoverNodeIndex(NO_INDEX);
			selectionData.setHoverEdgeIndex(NO_INDEX);
		}
	}
	
	

}
