package org.baderlab.cy3d.internal.data;

import java.awt.Component;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.camera.SimpleCamera;
import org.baderlab.cy3d.internal.cytoscape.edges.EdgeAnalyser;
import org.baderlab.cy3d.internal.geometric.ViewingVolume;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * This class represents a data object in which data relevant to the renderer,
 * such as the position of the camera, the number of frames elapsed, the coordinates
 * of the current drag selection box, is stored. Information about the current state 
 * of the network is also stored in this object. 
 * 
 * This class is mostly responsible for storing the renderer's data, and allowing 
 * access to the data through getter and setter objects.
 * 
 * @author Paperwing (Yue Dong)
 */
public class GraphicsData {

	/** The network view to be rendered */
	private CyNetworkView networkView;
	
	/**
	 * This value controls distance scaling when converting from Cytoscape
	 * coordinates (such as from Ding) to the renderer's 3D coordinates
	 */
	private float distanceScale = 180.0f; 

	private float verticalFov = 45.0f;
	
	private ViewingVolume viewingVolume;
	
	/** Distance from eye to the near clipping plane */
	private float nearZ = 0.2f;
	
	/** Distance from eye to the far clipping plane */
	private float farZ = 500f;
	
	/** The camera to use for transformation of 3D scene */
	private SimpleCamera camera;
	
	private int mouseCurrentX;
	
	private int mouseCurrentY;
	
	/** The height of the screen */
	private int screenHeight;
	
	/** The width of the screen */
	private int screenWidth;
	
	/** A boolean to disable real-time shape picking to improve framerate */
	private boolean disableHovering;
	
	/** Whether the scene has been updated and needs to be redrawn */
	private boolean updateScene = false;
	
	/** Whether to display the current frames per second */
	private boolean showFPS = false;
	
	/** Whether to display all node labels */
	private boolean showAllNodeLabels = true;
	
	private Component container;
	
	/** A {@link TaskFactoryListener} object that can be used to obtain the current set of task factories */
	private TaskFactoryListener taskFactoryListener;
	
	/** A task manager that can be used to execute tasks created by TaskFactory objects */
	private DialogTaskManager taskManager;
	
	private EdgeAnalyser edgeAnalyser;
	
	private PixelConverter pixelConverter;

	private GraphicsSelectionData selectionData;
	
	private CoordinatorData coordinatorData;
	
	private PickingData pickingData;
	
	private LightingData lightingData;
	
	
	private GL2 glContext;
	
	private VisualLexicon visualLexicon;
	
	public GraphicsData() {
		selectionData = new GraphicsSelectionData();
		coordinatorData = new CoordinatorData();
		pickingData = new PickingData();
		lightingData = new LightingData();
		
		camera = new SimpleCamera();
		viewingVolume = new ViewingVolume();
		edgeAnalyser = new EdgeAnalyser();
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
	
	public float getNearZ() {
		return nearZ;
	}

	public void setNearZ(float nearZ) {
		this.nearZ = nearZ;
	}

	public float getFarZ() {
		return farZ;
	}

	public void setFarZ(float farZ) {
		this.farZ = farZ;
	}

	public PickingData getPickingData() {
		return pickingData;
	}

	public void setPickingData(PickingData pickingData) {
		this.pickingData = pickingData;
	}

	public ViewingVolume getViewingVolume() {
		return viewingVolume;
	}

	public void setViewingVolume(ViewingVolume viewingVolume) {
		this.viewingVolume = viewingVolume;
	}

	public LightingData getLightingData() {
		return lightingData;
	}

	public void setLightingData(LightingData lightingData) {
		this.lightingData = lightingData;
	}

	public void setContainer(Component container) {
		this.container = container;
	}

	public Component getContainer() {
		return container;
	}

	public TaskFactoryListener getTaskFactoryListener() {
		return taskFactoryListener;
	}

	public void setTaskFactoryListener(TaskFactoryListener taskFactoryListener) {
		this.taskFactoryListener = taskFactoryListener;
	}

	public DialogTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(DialogTaskManager taskManager) {
		this.taskManager = taskManager;
	}

//	public void setShowFPS(boolean showFPS) {
//		if(showFPS)
//			this.frameRateTracker.startTrackingFPS();
//		else
//			this.frameRateTracker.stopTrackingFPS();
//		this.showFPS = showFPS;
//	}
//
//	public boolean getShowFPS() {
//		return showFPS;
//	}
	
	public void setShowAllNodeLabels(boolean showAllNodeLabels) {
		this.showAllNodeLabels = showAllNodeLabels;
	}

	public boolean getShowAllNodeLabels() {
		return showAllNodeLabels;
	}

	public void setEdgeAnalyser(EdgeAnalyser edgeAnalyser) {
		this.edgeAnalyser = edgeAnalyser;
	}

	public EdgeAnalyser getEdgeAnalyser() {
		return edgeAnalyser;
	}

	public PixelConverter getPixelConverter() {
		return pixelConverter;
	}

	public void setPixelConverter(PixelConverter pixelConverter) {
		this.pixelConverter = pixelConverter;
	}

	public void setMouseCurrentX(int x) {
		this.mouseCurrentX = x;
	}
	
	public int getMouseCurrentX() {
		return mouseCurrentX;
	}
	
	public void setMouseCurrentY(int y) {
		this.mouseCurrentY = y;
	}
	
	public int getMouseCurrentY() {
		return mouseCurrentY;
	}
}
