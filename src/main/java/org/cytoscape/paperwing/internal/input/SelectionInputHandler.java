package org.cytoscape.paperwing.internal.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Set;

import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;
import org.cytoscape.paperwing.internal.graphics.GraphicsSelectionData;

public class SelectionInputHandler implements InputHandler {

	public static int NULL_COORDINATE = Integer.MIN_VALUE;
	public static int NO_INDEX = -1; 
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		
		processDeselectOther(keys, mouse, graphicsData);
		processSingleSelection(keys, mouse, graphicsData);
		processDragSelection(keys, mouse, graphicsData);
		
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
		
		if (!selectionData.isDragSelectMode()) {
				
			if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
				
				if (newHoverNodeIndex != NO_INDEX) {
					
					if (selectedNodeIndices.contains(newHoverNodeIndex)) {
						selectedNodeIndices.remove(newHoverNodeIndex);
					} else {
						selectedNodeIndices.add(newHoverNodeIndex);
					}
					
				} else if (newHoverEdgeIndex != NO_INDEX) {
		
					if (selectedEdgeIndices.contains(newHoverEdgeIndex)) {
						selectedEdgeIndices.remove(newHoverEdgeIndex);
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
		
		if (mouse.getPressed().contains(MouseEvent.BUTTON1)) { 
			if (!selectionData.isDragSelectMode() 
					&& !keys.getHeld().contains(KeyEvent.VK_SHIFT)
					&& !keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
					
					//&& graphicsData.getPickingData().getClosestPickedNodeIndex() == NO_INDEX
					//&& graphicsData.getPickingData().getClosestPickedEdgeIndex() == NO_INDEX) {
				
				selectionData.getSelectedNodeIndices().clear();
				selectionData.getSelectedEdgeIndices().clear();
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
	
	

}
