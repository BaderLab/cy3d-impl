package org.baderlab.cy3d.internal.data;

import java.awt.Component;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;

import org.baderlab.cy3d.internal.AnimatorController;
import org.baderlab.cy3d.internal.Graphics;
import org.baderlab.cy3d.internal.coordinator.ViewingCoordinator;
import org.baderlab.cy3d.internal.cytoscape.edges.EdgeAnalyser;
import org.baderlab.cy3d.internal.geometric.ViewingVolume;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.baderlab.cy3d.internal.tools.FrameRateTracker;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
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
	
	/** The height of the screen */
	private int screenHeight;
	
	/** The width of the screen */
	private int screenWidth;
	
	/** Start time used for FPS timing */
	private long startTime;
	
	/** Number of frames elapsed */
	private long framesElapsed = 0;
	
	/** End time used for FPS timing */
	private long endTime;
	
	/** A boolean to disable real-time shape picking to improve framerate */
	private boolean disableHovering;
	
	/** Whether the scene has been updated and needs to be redrawn */
	private boolean updateScene = false;
	
	/** Whether to display the current frames per second */
	private boolean showFPS = false;
	
	/** Whether to display all node labels */
	private boolean showAllNodeLabels = true;
	
	private Component container;
	
	/** A framerate tracker used to calculate the current number of frames per second. */
	private FrameRateTracker frameRateTracker;
	
	/** A {@link TaskFactoryListener} object that can be used to obtain the current set of task factories */
	private TaskFactoryListener taskFactoryListener;
	
	/** A task manager that can be used to execute tasks created by TaskFactory objects */
	private DialogTaskManager taskManager;
	
	private EdgeAnalyser edgeAnalyser;
	
	/** The {@link GLAnimatorControl} object used to control the animator which automatically makes calls to redraw the scene */
	private GLAnimatorControl animatorControl;
	
	private AnimatorController animatorController;
	
	/** 
	 * A {@link GraphicsSelectionData} object which is responsible for 
	 * storing all data related to selection of objects in the network, such
	 * as indices of currently selected nodes, or the coordinates of the current
	 * selection box.
	 * */
	private GraphicsSelectionData selectionData;
	
	/**
	 * A {@link CoordinatorData} object responsible for storing data related to
	 * the relevant {@link ViewingCoordinator}, ie. it stores the data related
	 * to coordination with another {@link Graphics} object, such as in the
	 * relationship between bird's eye and main window rendering objects.
	 */
	private CoordinatorData coordinatorData;
	
	/**
	 * A {@link PickingData} object responsible for storing data related to the 
	 * current picking state of the renderer, such as the index and type
	 * of the object that was found under the current mouse cursor.
	 */
	private PickingData pickingData;
	
	/**
	 * A {@link LightingData} object responsible for storing data related to
	 * lighting and lights, such as the states of lights and their color
	 * values.
	 */
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
		frameRateTracker = new FrameRateTracker();
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

	public void setFramesElapsed(long framesElapsed) {
		this.framesElapsed = framesElapsed;
	}

	public long getFramesElapsed() {
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

	public void setUpdateScene(boolean updateScene) {
		this.updateScene = updateScene;
	}

	public boolean getUpdateScene() {
		return updateScene;
	}

	public void setShowFPS(boolean showFPS) {
		this.showFPS = showFPS;
	}

	public boolean getShowFPS() {
		return showFPS;
	}
	
	public void setShowAllNodeLabels(boolean showAllNodeLabels) {
		this.showAllNodeLabels = showAllNodeLabels;
	}

	public boolean getShowAllNodeLabels() {
		return showAllNodeLabels;
	}

	public FrameRateTracker getFrameRateTracker() {
		return frameRateTracker;
	}

	public void setEdgeAnalyser(EdgeAnalyser edgeAnalyser) {
		this.edgeAnalyser = edgeAnalyser;
	}

	public EdgeAnalyser getEdgeAnalyser() {
		return edgeAnalyser;
	}

	public void setAnimatorControl(GLAnimatorControl animatorControl) {
		this.animatorControl = animatorControl;
	}

	public GLAnimatorControl getAnimatorControl() {
		return animatorControl;
	}

	public void setAnimatorController(AnimatorController animatorController) {
		this.animatorController = animatorController;
	}

	public AnimatorController getAnimatorController() {
		return animatorController;
	}
}
