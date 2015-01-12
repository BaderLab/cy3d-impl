package org.baderlab.cy3d.internal.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.baderlab.cy3d.internal.geometric.Vector3;

/** 
 * A {@link GraphicsSelectionData} object which is responsible for 
 * storing all data related to selection of objects in the network, such
 * as indices of currently selected nodes, or the coordinates of the current
 * selection box.
 * */
public class GraphicsSelectionData {
	
	/** The set of indices for nodes that are selected */
	private Set<Long> selectedNodeIndices;
	
	/** The set of indices for edges that are selected */
	private Set<Long> selectedEdgeIndices;
	
	private Set<Long> toBeDeselectedNodeIndices; 
	
	private Set<Long> toBeDeselectedEdgeIndices; 	
	
	
	/** The top left x position for the selection border */
	private int selectTopLeftX;
	
	/** The top left y position for the selection border */
	private int selectTopLeftY;
	
	/** The bottom right x position for the selection border */
	private int selectBottomRightX;
	
	/** The bottom right y position for the selection border */
	private int selectBottomRightY;
	
	/** The index of the node currently being hovered over */
	private long hoverNodeIndex = -1;
	
	/** The index of the edge currently being hovered over */
	private long hoverEdgeIndex = -1;
	
	/** A flag for whether drag selection mode is currently active */
	private boolean dragSelectMode;
	
	// Whether we have a valid top left corner for the selection box
	private boolean selectTopLeftFound;
	
	/** A projection of the current mouse position into 3D coordinates to be used 
	 * for mouse drag movement of certain objects */
	private Vector3 currentSelectedProjection;
	
	/** A projection of the mouse position into 3D coordinates to be used 
	 * for mouse drag movement of certain objects */
	private Vector3 previousSelectedProjection;
	
	/** The distance from the projected point to the screen */
	private double selectProjectionDistance;
	
	public GraphicsSelectionData() {
		selectedNodeIndices = new LinkedHashSet<Long>();
		selectedEdgeIndices = new LinkedHashSet<Long>();
		
		toBeDeselectedNodeIndices = new LinkedHashSet<Long>();
		toBeDeselectedEdgeIndices = new LinkedHashSet<Long>();
	}
	
	public Set<Long> getSelectedNodeIndices() {
		return selectedNodeIndices;
	}

	public void setSelectedNodeIndices(Set<Long> selectedNodeIndices) {
		this.selectedNodeIndices = selectedNodeIndices;
	}

	public Set<Long> getSelectedEdgeIndices() {
		return selectedEdgeIndices;
	}

	public void setSelectedEdgeIndices(Set<Long> selectedEdgeIndices) {
		this.selectedEdgeIndices = selectedEdgeIndices;
	}

	public int getSelectTopLeftX() {
		return selectTopLeftX;
	}

	public void setSelectTopLeftX(int selectTopLeftX) {
		this.selectTopLeftX = selectTopLeftX;
	}

	public int getSelectTopLeftY() {
		return selectTopLeftY;
	}

	public void setSelectTopLeftY(int selectTopLeftY) {
		this.selectTopLeftY = selectTopLeftY;
	}

	public int getSelectBottomRightX() {
		return selectBottomRightX;
	}

	public void setSelectBottomRightX(int selectBottomRightX) {
		this.selectBottomRightX = selectBottomRightX;
	}

	public int getSelectBottomRightY() {
		return selectBottomRightY;
	}

	public void setSelectBottomRightY(int selectBottomRightY) {
		this.selectBottomRightY = selectBottomRightY;
	}

	public boolean isDragSelectMode() {
		return dragSelectMode;
	}

	public void setDragSelectMode(boolean dragSelectMode) {
		this.dragSelectMode = dragSelectMode;
	}

	public Vector3 getCurrentSelectedProjection() {
		return currentSelectedProjection;
	}

	public void setCurrentSelectedProjection(Vector3 currentSelectedProjection) {
		this.currentSelectedProjection = currentSelectedProjection;
	}

	public Vector3 getPreviousSelectedProjection() {
		return previousSelectedProjection;
	}

	public void setPreviousSelectedProjection(Vector3 previousSelectedProjection) {
		this.previousSelectedProjection = previousSelectedProjection;
	}

	public double getSelectProjectionDistance() {
		return selectProjectionDistance;
	}

	public void setSelectProjectionDistance(double selectProjectionDistance) {
		this.selectProjectionDistance = selectProjectionDistance;
	}

	public void setHoverNodeIndex(long hoverNodeIndex) {
		this.hoverNodeIndex = hoverNodeIndex;
	}

	public long getHoverNodeIndex() {
		return hoverNodeIndex;
	}

	public void setHoverEdgeIndex(long hoverEdgeIndex) {
		this.hoverEdgeIndex = hoverEdgeIndex;
	}

	public long getHoverEdgeIndex() {
		return hoverEdgeIndex;
	}

	public void setSelectTopLeftFound(boolean selectTopLeftFound) {
		this.selectTopLeftFound = selectTopLeftFound;
	}

	public boolean isSelectTopLeftFound() {
		return selectTopLeftFound;
	}

	public void setToBeDeselectedNodeIndices(Set<Long> toBeDeselectedNodeIndices) {
		this.toBeDeselectedNodeIndices = toBeDeselectedNodeIndices;
	}

	public Set<Long> getToBeDeselectedNodeIndices() {
		return toBeDeselectedNodeIndices;
	}

	public void setToBeDeselectedEdgeIndices(Set<Long> toBeDeselectedEdgeIndices) {
		this.toBeDeselectedEdgeIndices = toBeDeselectedEdgeIndices;
	}

	public Set<Long> getToBeDeselectedEdgeIndices() {
		return toBeDeselectedEdgeIndices;
	}
}
