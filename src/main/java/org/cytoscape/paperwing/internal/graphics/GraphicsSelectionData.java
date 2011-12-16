package org.cytoscape.paperwing.internal.graphics;

import java.util.LinkedHashSet;
import java.util.Set;

import org.cytoscape.paperwing.internal.Vector3;

public class GraphicsSelectionData {
	
	/** The set of indices for nodes that are selected */
	private Set<Integer> selectedNodeIndices;
	
	/** The set of indices for edges that are selected */
	private Set<Integer> selectedEdgeIndices;
	
	/** The top left x position for the selection border */
	private int selectTopLeftX;
	
	/** The top left y position for the selection border */
	private int selectTopLeftY;
	
	/** The bottom right x position for the selection border */
	private int selectBottomRightX;
	
	/** The bottom right y position for the selection border */
	private int selectBottomRightY;
	
	/** A flag for whether drag selection mode is currently active */
	private boolean dragSelectMode;
	
	/** A projection of the current mouse position into 3D coordinates to be used 
	 * for mouse drag movement of certain objects */
	private Vector3 currentSelectedProjection;
	
	/** A projection of the mouse position into 3D coordinates to be used 
	 * for mouse drag movement of certain objects */
	private Vector3 previousSelectedProjection;
	
	/** The distance from the projected point to the screen */
	private double selectProjectionDistance;
	
	public GraphicsSelectionData() {
		selectedNodeIndices = new LinkedHashSet<Integer>();
		selectedEdgeIndices = new LinkedHashSet<Integer>();
	}
	
	public Set<Integer> getSelectedNodeIndices() {
		return selectedNodeIndices;
	}

	public void setSelectedNodeIndices(Set<Integer> selectedNodeIndices) {
		this.selectedNodeIndices = selectedNodeIndices;
	}

	public Set<Integer> getSelectedEdgeIndices() {
		return selectedEdgeIndices;
	}

	public void setSelectedEdgeIndices(Set<Integer> selectedEdgeIndices) {
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
}
