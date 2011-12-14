package org.cytoscape.paperwing.internal.graphics;

import java.util.Set;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.view.model.CyNetworkView;

public class GraphicsData {

	/** The network view to be rendered */
	private CyNetworkView networkView;
	
	/**
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private float distanceScale = 178.0f; 
	
	/** The index of the node currently being hovered over */
	private int hoverNodeIndex;
	
	/** The index of the edge currently being hovered over */
	private int hoverEdgeIndex;
	
	/** The camera to use for transformation of 3D scene */
	private SimpleCamera camera;
	
	/** The height of the screen */
	private int screenHeight;
	
	/** The width of the screen */
	private int screenWidth;
	
	/** Start time used for FPS timing */
	private long startTime;
	
	/** End time used for FPS timing */
	private long endTime;
	
	/** A boolean to disable real-time shape picking to improve framerate */
	private boolean disableHovering;
	
	private GraphicsSelectionData selectionData;
	
	private GL2 glContext;
	
	public GraphicsData() {
		selectionData = new GraphicsSelectionData();
	}
	
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

	public void setDisableHovering(boolean disableHovering) {
		this.disableHovering = disableHovering;
	}

	public boolean isDisableHovering() {
		return disableHovering;
	}

	public void setGlContext(GL2 glContext) {
		this.glContext = glContext;
	}

	public GL2 getGlContext() {
		return glContext;
	}

	public GraphicsSelectionData getSelectionData() {
		return selectionData;
	}

	public void setSelectionData(GraphicsSelectionData selectionData) {
		this.selectionData = selectionData;
	}
}
