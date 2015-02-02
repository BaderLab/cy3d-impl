package org.baderlab.cy3d.internal.data;

import java.awt.Component;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.camera.OriginOrbitCamera;
import org.baderlab.cy3d.internal.cytoscape.edges.EdgeAnalyser;
import org.baderlab.cy3d.internal.geometric.ViewingVolume;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import com.google.common.eventbus.EventBus;

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

	/**
	 * This value controls distance scaling when converting from Cytoscape
	 * coordinates (such as from Ding) to the renderer's 3D coordinates
	 */
	public static final float DISTANCE_SCALE = 180.0f; 
	public static final float VERTICAL_VOF = 45.0f;
	public static final float NEAR_Z = 0.2f;
	public static final float FAR_Z = 500f;
	
	
	private final CyNetworkView networkView;
	private final EventBus eventBus;
	private final OriginOrbitCamera camera;
	private GL2 glContext;
	private final VisualLexicon visualLexicon;
	
	private ViewingVolume viewingVolume;
	
	private int mouseCurrentX;
	private int mouseCurrentY;
	private int screenHeight;
	private int screenWidth;
	
	private Component container;
	
	private TaskFactoryListener taskFactoryListener;
	private DialogTaskManager taskManager;
	private EdgeAnalyser edgeAnalyser;
	private PixelConverter pixelConverter;
	private GraphicsSelectionData selectionData;
	private PickingData pickingData;
	private LightingData lightingData;
	
	
	
	public GraphicsData(CyNetworkView networkView, VisualLexicon visualLexicon, EventBus eventBus) {
		this.networkView = networkView;
		this.eventBus = eventBus;
		this.visualLexicon = visualLexicon;
		
		selectionData = new GraphicsSelectionData();
		pickingData = new PickingData();
		lightingData = new LightingData();
		camera = new OriginOrbitCamera();
		viewingVolume = new ViewingVolume();
		edgeAnalyser = new EdgeAnalyser();
	}
	
	
	public void setGlContext(GL2 glContext) {
		this.glContext = glContext;
	}
	
	public void setContainer(Component container) {
		this.container = container;
	}
	
	public void setPixelConverter(PixelConverter pixelConverter) {
		this.pixelConverter = pixelConverter;
	}
	
	public void setTaskFactoryListener(TaskFactoryListener taskFactoryListener) {
		this.taskFactoryListener = taskFactoryListener;
	}
	
	public void setTaskManager(DialogTaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	
	public CyNetworkView getNetworkView() {
		return networkView;
	}

	public OriginOrbitCamera getCamera() {
		return camera;
	}
	
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
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

	public GL2 getGlContext() {
		return glContext;
	}

	public GraphicsSelectionData getSelectionData() {
		return selectionData;
	}
	
	public PickingData getPickingData() {
		return pickingData;
	}

	public ViewingVolume getViewingVolume() {
		return viewingVolume;
	}

	public LightingData getLightingData() {
		return lightingData;
	}

	public Component getContainer() {
		return container;
	}

	public TaskFactoryListener getTaskFactoryListener() {
		return taskFactoryListener;
	}

	public DialogTaskManager getTaskManager() {
		return taskManager;
	}

	public EdgeAnalyser getEdgeAnalyser() {
		return edgeAnalyser;
	}

	public PixelConverter getPixelConverter() {
		return pixelConverter;
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
	
	public EventBus getEventBus() {
		return eventBus;
	}
}
