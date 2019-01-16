package org.baderlab.cy3d.internal.data;

import javax.media.opengl.GL2;
import javax.swing.JComponent;

import org.baderlab.cy3d.internal.camera.OriginOrbitCamera;
import org.baderlab.cy3d.internal.cytoscape.edges.EdgeAnalyser;
import org.baderlab.cy3d.internal.geometric.ViewingVolume;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
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
	
	
	
	private final EventBus eventBus;
	private final OriginOrbitCamera camera;
	private final VisualLexicon visualLexicon;
	
	// updated on every frame
	private GL2 glContext;
	private CyNetworkView networkSnapshot;
	
	private ViewingVolume viewingVolume;
	
	private int mouseCurrentX;
	private int mouseCurrentY;
	private int screenHeight;
	private int screenWidth;
	
	private final JComponent container;
	private final JComponent inputComponent;
	
	private TaskFactoryListener taskFactoryListener;
	private DialogTaskManager taskManager;
	private EdgeAnalyser edgeAnalyser;
	private PixelConverter pixelConverter;
	private GraphicsSelectionData selectionData;
	private PickingData pickingData;
	
	private boolean showLabels = false;
	
	
	public GraphicsData(VisualLexicon visualLexicon, EventBus eventBus, JComponent container, JComponent inputComponent) {
		this.eventBus = eventBus;
		this.visualLexicon = visualLexicon;
		this.container = container;
		this.inputComponent = inputComponent;
		
		selectionData = new GraphicsSelectionData();
		pickingData = new PickingData();
		camera = new OriginOrbitCamera();
		viewingVolume = new ViewingVolume();
		edgeAnalyser = new EdgeAnalyser();
	}
	
	
	public void setGlContext(GL2 glContext) {
		this.glContext = glContext;
	}
	
	public void setNetworkSnapshot(CyNetworkView networkView) {
		this.networkSnapshot = networkView;
		
		for(View<CyNode> node : networkView.getNodeViews()) {
			Double x = node.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			System.out.println("node:" + node.getSUID() + " x' = " + x);
		}
		
		
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
	
	public CyNetworkView getNetworkSnapshot() {
		return networkSnapshot;
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

	public JComponent getContainer() {
		return container;
	}

	public JComponent getInputComponent() {
		return inputComponent;
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


	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}
	
	public boolean getShowLabels() {
		return showLabels;
	}
}
