package org.cytoscape.paperwing.internal;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.MainCoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
import org.cytoscape.paperwing.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.cytoscape.view.WindNetworkView;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.LightingData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.input.InputProcessor;
import org.cytoscape.paperwing.internal.input.MainInputProcessor;
import org.cytoscape.paperwing.internal.lighting.DefaultLightingProcessor;
import org.cytoscape.paperwing.internal.lighting.Light;
import org.cytoscape.paperwing.internal.lighting.LightingProcessor;
import org.cytoscape.paperwing.internal.picking.DefaultShapePickingProcessor;
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.rendering.PositionCameraProcedure;
import org.cytoscape.paperwing.internal.rendering.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderArcEdgesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderLightsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderNodeLabelsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderNodesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderSelectionBoxProcedure;
import org.cytoscape.paperwing.internal.rendering.ResetSceneProcedure;
import org.cytoscape.paperwing.internal.rendering.text.StringRenderer;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.view.model.CyNetworkView;

/**
 * An implementation for the {@link GraphicsHandler} interface to be used
 * for main rendering windows. That is, this handler fully supports keyboard
 * and mouse input, as well as selection and picking.
 * 
 * @author Yue Dong
 */
public class MainGraphicsHandler implements GraphicsHandler {

	/**
	 * The list of {@link ReadOnlyGraphicsProcedure} objects, or
	 * rendering routines, that this {@link GraphicsHandler} uses.
	 */
	private List<ReadOnlyGraphicsProcedure> renderProcedures;
	
	/**
	 * A Map containing rendering procedures that have re-usable display lists.
	 */
	Map<Class<? extends ReadOnlyGraphicsProcedure>, Integer> renderProcedureLists;
	
	public MainGraphicsHandler() {
		
		// Populate the list of rendering routines that this GraphicsHandler
		// uses. 
		// The routines will be executed in the order that they are added.
		renderProcedures = new LinkedList<ReadOnlyGraphicsProcedure>();
		renderProcedures.add(new ResetSceneProcedure());
		renderProcedures.add(new PositionCameraProcedure());
		
		renderProcedures.add(new RenderNodesProcedure());
		renderProcedures.add(new RenderArcEdgesProcedure());
		renderProcedures.add(new RenderSelectionBoxProcedure());
		
		renderProcedures.add(new RenderNodeLabelsProcedure());
		renderProcedures.add(new RenderLightsProcedure());
		
		// Initialize the dictionary of display lists to be used for rendering procedures that can be
		// compiled into a re-usable display list
		renderProcedureLists = new HashMap<Class<? extends ReadOnlyGraphicsProcedure>, Integer>();
		
//		renderProcedureLists.put(RenderNodesProcedure.class, null);
//		renderProcedureLists.put(RenderArcEdgesProcedure.class, null);
	}
	
	@Override
	public InputProcessor getInputProcessor() {
		return new MainInputProcessor();
	}

	@Override
	public void drawScene(GraphicsData graphicsData) {
		
		// Control light positioning
		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
		
		// Code below toggles the light following the camera
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
		// FloatBuffer.wrap(lightPosition));

		GL2 gl = graphicsData.getGlContext();
		
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures) {
			// Does this rendering procedure have a re-usable display list?
			if (renderProcedureLists.get(renderProcedure.getClass()) != null) {
				
				// Is it time to update the display list?
				if (graphicsData.getUpdateScene() || graphicsData.getFramesElapsed() <= 2) {
					gl.glNewList(renderProcedureLists.get(renderProcedure.getClass()), GL2.GL_COMPILE_AND_EXECUTE);
					renderProcedure.execute(graphicsData);
					gl.glEndList();
					
					graphicsData.setUpdateScene(false);
				// If not, call the current list
				} else {
					gl.glCallList(renderProcedureLists.get(renderProcedure.getClass()));
				}
			} else {
				renderProcedure.execute(graphicsData);
			}
			
//			renderProcedure.execute(graphicsData);
		}
	}

	@Override
	public void setupLighting(GraphicsData graphicsData) {
		
		GL2 gl = graphicsData.getGlContext();
		LightingData lightingData = graphicsData.getLightingData();
	
		Light light0 = lightingData.getLight(0);
		light0.setAmbient(0.4f, 0.4f, 0.4f, 1.0f);
		light0.setDiffuse(0.57f, 0.57f, 0.57f, 1.0f);
		light0.setSpecular(0.79f, 0.79f, 0.79f, 1.0f);
		light0.setPosition(-4.0f, 4.0f, 6.0f, 1.0f);
		light0.setTurnedOn(true);
		
		for (int i = 0; i < LightingData.NUM_LIGHTS; i++) {
			Light light = lightingData.getLight(i);
		
			if (light.isTurnedOn()) {
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(light.getAmbient()));
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(light.getDiffuse()));
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(light.getSpecular()));
				gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(light.getPosition()));
	
				gl.glEnable(GL2.GL_LIGHT0 + i);
			}
		}
	}

	@Override
	public ShapePickingProcessor getShapePickingProcessor() {
		return new DefaultShapePickingProcessor(new RenderNodesProcedure(), new RenderArcEdgesProcedure());
	}

	@Override
	public ViewingCoordinator getCoordinator(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		if (ViewingCoordinator.getCoordinator(networkView) != null) {
			return ViewingCoordinator.getCoordinator(networkView);
		} else {
			return ViewingCoordinator.createCoordinator(networkView);
		}
	}

	@Override
	public CoordinatorProcessor getCoordinatorProcessor() {
		return new MainCoordinatorProcessor();
	}


	@Override
	public CytoscapeDataProcessor getCytoscapeDataProcessor() {
		return new MainCytoscapeDataProcessor();
	}
	
	@Override
	public LightingProcessor getLightingProcessor() {
		return new DefaultLightingProcessor();
	}
	
	@Override
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures) {
			renderProcedure.initialize(graphicsData);
		}
		
		for (Entry<Class<? extends ReadOnlyGraphicsProcedure>, Integer> entry : renderProcedureLists.entrySet()) {
			renderProcedureLists.put(entry.getKey(), gl.glGenLists(1));
		}
	}

	@Override
	public void dispose(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		for (Integer list : renderProcedureLists.values()) {
			gl.glDeleteLists(list, 1);
		}
	}

	
}