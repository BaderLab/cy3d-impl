package org.cytoscape.paperwing.internal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.coordinator.BirdsEyeCoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.MainCoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
import org.cytoscape.paperwing.internal.cytoscape.processing.BirdsEyeCytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.input.BirdsEyeInputProcessor;
import org.cytoscape.paperwing.internal.input.InputProcessor;
import org.cytoscape.paperwing.internal.input.KeyboardMonitor;
import org.cytoscape.paperwing.internal.input.MainInputProcessor;
import org.cytoscape.paperwing.internal.input.MouseMonitor;
import org.cytoscape.paperwing.internal.lighting.DefaultLightingProcessor;
import org.cytoscape.paperwing.internal.lighting.LightingProcessor;
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.rendering.PositionCameraProcedure;
import org.cytoscape.paperwing.internal.rendering.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderArcEdgesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderBoundingBoxProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderNodesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderSelectionBoxProcedure;
import org.cytoscape.paperwing.internal.rendering.ResetSceneProcedure;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;

/**
 * An implementation of the {@link GraphicsHandler} interface used
 * for bird's eye view rendering objects. This {@link GraphicsHandler}
 * implementation thusly will not provide support for picking and
 * advanced input handling, such as node and edge creation via the
 * keyboard.
 * 
 * @author Yue Dong
 */
public class BirdsEyeGraphicsHandler implements GraphicsHandler {

	/**
	 * The list of {@link ReadOnlyGraphicsProcedure} objects, or 
	 * rendering routines, that this {@link GraphicsHandler} employs.
	 * It may contain modified routines that specifically lower polygon
	 * detail for nodes or edges that are far away.
	 */
	private List<ReadOnlyGraphicsProcedure> renderProcedures;
	
	/**
	 * A Map containing rendering procedures that have re-usable display lists.
	 */
	Map<Class<? extends ReadOnlyGraphicsProcedure>, Integer> renderProcedureLists;
	
	public BirdsEyeGraphicsHandler() {

		// Populate the list of rendering routines employed by this handler.
		renderProcedures = new LinkedList<ReadOnlyGraphicsProcedure>();

		renderProcedures.add(new ResetSceneProcedure());
		renderProcedures.add(new PositionCameraProcedure());
		renderProcedures.add(new RenderNodesProcedure());
		renderProcedures.add(new RenderArcEdgesProcedure());
		renderProcedures.add(new RenderBoundingBoxProcedure());	
		
		// Initialize the dictionary of display lists to be used for rendering procedures that can be
		// compiled into a re-usable display list
		renderProcedureLists = new HashMap<Class<? extends ReadOnlyGraphicsProcedure>, Integer>();
		
		renderProcedureLists.put(RenderNodesProcedure.class, null);
		renderProcedureLists.put(RenderArcEdgesProcedure.class, null);
	}
	
	@Override
	public InputProcessor getInputProcessor() {
		return new BirdsEyeInputProcessor();
	}

	@Override
	public void drawScene(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
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
		
//		System.out.println(graphicsData.getFramesElapsed());
	}

	@Override
	public void setupLighting(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Because this handler is designed for bird's eye view {@link Graphics}
	 * objects, it returns a dummy {@link ShapePickingProcessor} and does not
	 * perform any shape picking data processing.
	 * 
	 * @return A dummy {@link ShapePickingProcessor} object (no processing is done).
	 */
	@Override
	public ShapePickingProcessor getShapePickingProcessor() {
		
		// Return a ShapePickingProcessor that does not do any processing
		return new ShapePickingProcessor() {

			@Override
			public void initialize(GraphicsData graphicsData) {
			}

			@Override
			public void processPicking(MouseMonitor mouse,
					KeyboardMonitor keys, GraphicsData graphicsData) {
			}
			
		};
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
		return new BirdsEyeCoordinatorProcessor();
	}
	
	@Override
	public CytoscapeDataProcessor getCytoscapeDataProcessor() {
		
		return new BirdsEyeCytoscapeDataProcessor();
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
