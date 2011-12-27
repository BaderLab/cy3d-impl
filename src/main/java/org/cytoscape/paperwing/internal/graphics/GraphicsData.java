package org.cytoscape.paperwing.internal.graphics;

import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.cytoscape.paperwing.internal.Graphics;
import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;

public class GraphicsData {

	/** The network view to be rendered */
	private CyNetworkView networkView;
	
	/**
	 * This value controls distance scaling when converting from node
	 * coordinates to drawing coordinates
	 */
	private float distanceScale = 178.0f; 
	
	private float verticalFov = 45.0f;
	
	/** The camera to use for transformation of 3D scene */
	private SimpleCamera camera;
	
	/** The height of the screen */
	private int screenHeight;
	
	/** The width of the screen */
	private int screenWidth;
	
	/** Start time used for FPS timing */
	private long startTime;
	
	/** Number of frames elapsed */
	private int framesElapsed = 0;
	
	/** End time used for FPS timing */
	private long endTime;
	
	/** A boolean to disable real-time shape picking to improve framerate */
	private boolean disableHovering;
	
	private GraphicsSelectionData selectionData;
	private CoordinatorData coordinatorData;
	private PickingData pickingData;
	
	private GL2 glContext;
	
	private VisualLexicon visualLexicon;
	
	public GraphicsData() {
		selectionData = new GraphicsSelectionData();
		coordinatorData = new CoordinatorData();
		pickingData = new PickingData();
		
		camera = new SimpleCamera();
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

	public void setVisualLexicon(VisualLexicon visualLexicon) {
		this.visualLexicon = visualLexicon;
	}

	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}

	public void setFramesElapsed(int framesElapsed) {
		this.framesElapsed = framesElapsed;
	}

	public int getFramesElapsed() {
		return framesElapsed;
	}

	public void setCoordinatorData(CoordinatorData coordinatorData) {
		this.coordinatorData = coordinatorData;
	}

	public CoordinatorData getCoordinatorData() {
		return coordinatorData;
	}

	public float getVerticalFov() {
		return verticalFov;
	}

	public void setVerticalFov(float verticalFov) {
		this.verticalFov = verticalFov;
	}

	public PickingData getPickingData() {
		return pickingData;
	}

	public void setPickingData(PickingData pickingData) {
		this.pickingData = pickingData;
	}
}
