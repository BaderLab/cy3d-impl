package org.cytoscape.paperwing.internal.graphics;

import java.util.Set;

import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;

public class GraphicsData {
	/** The network view to be rendered */
	private CyNetworkView networkView;
	
	/**
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private float distanceScale = 178.0f; 

	/** The set of indices for nodes that are selected */
	private Set<Integer> selectedNodeIndices;
	
	/** The set of indices for edges that are selected */
	private Set<Integer> selectedEdgeIndices;
	
	/** The index of the node currently being hovered over */
	private int hoverNodeIndex;
	
	/** The index of the edge currently being hovered over */
	private int hoverEdgeIndex;
	
	/** The camera to use for transformation of 3D scene */
	private SimpleCamera camera;
	
	/** The top left x position for the selection border */
	private int selectTopLeftX;
	
	/** The top left y position for the selection border */
	private int selectTopLeftY;
	
	/** The bottom right x position for the selection border */
	private int selectBottomRightX;
	
	/** The bottom right y position for the selection border */
	private int selectBottomRightY;
	
	/** The height of the screen */
	private int screenHeight;
	
	/** The width of the screen */
	private int screenWidth;
	
	/** Start time used for FPS timing */
	private long startTime;
	
	/** End time used for FPS timing */
	private long endTime;
	
	public void setNetworkView(CyNetworkView networkView) {
		this.networkView = networkView;
	}

	public CyNetworkView getNetworkView() {
		return networkView;
	}

	public void setDistanceScale(float distanceScale) {
		this.distanceScale = distanceScale;
	}

	public float getDistanceScale() {
		return distanceScale;
	}

	public void setSelectedNodeIndices(Set<Integer> selectedNodeIndices) {
		this.selectedNodeIndices = selectedNodeIndices;
	}

	public Set<Integer> getSelectedNodeIndices() {
		return selectedNodeIndices;
	}

	public void setSelectedEdgeIndices(Set<Integer> selectedEdgeIndices) {
		this.selectedEdgeIndices = selectedEdgeIndices;
	}

	public Set<Integer> getSelectedEdgeIndices() {
		return selectedEdgeIndices;
	}

	public void setHoverNodeIndex(int hoverNodeIndex) {
		this.hoverNodeIndex = hoverNodeIndex;
	}

	public int getHoverNodeIndex() {
		return hoverNodeIndex;
	}

	public void setHoverEdgeIndex(int hoverEdgeIndex) {
		this.hoverEdgeIndex = hoverEdgeIndex;
	}

	public int getHoverEdgeIndex() {
		return hoverEdgeIndex;
	}

	public void setCamera(SimpleCamera camera) {
		this.camera = camera;
	}

	public SimpleCamera getCamera() {
		return camera;
	}

	public void setSelectTopLeftX(int selectTopLeftX) {
		this.selectTopLeftX = selectTopLeftX;
	}

	public int getSelectTopLeftX() {
		return selectTopLeftX;
	}

	public void setSelectTopLeftY(int selectTopLeftY) {
		this.selectTopLeftY = selectTopLeftY;
	}

	public int getSelectTopLeftY() {
		return selectTopLeftY;
	}

	public void setSelectBottomRightX(int selectBottomRightX) {
		this.selectBottomRightX = selectBottomRightX;
	}

	public int getSelectBottomRightX() {
		return selectBottomRightX;
	}

	public void setSelectBottomRightY(int selectBottomRightY) {
		this.selectBottomRightY = selectBottomRightY;
	}

	public int getSelectBottomRightY() {
		return selectBottomRightY;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}
	
	
	
}
