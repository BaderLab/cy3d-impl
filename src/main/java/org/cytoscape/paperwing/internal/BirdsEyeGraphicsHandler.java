package org.cytoscape.paperwing.internal;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cytoscape.paperwing.internal.coordinator.BirdsEyeCoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.MainCoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
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

	public BirdsEyeGraphicsHandler() {

		// Populate the list of rendering routines employed by this handler.
		renderProcedures = new LinkedList<ReadOnlyGraphicsProcedure>();

		renderProcedures.add(new ResetSceneProcedure());
		renderProcedures.add(new PositionCameraProcedure());
		renderProcedures.add(new RenderNodesProcedure());
		renderProcedures.add(new RenderArcEdgesProcedure());
		renderProcedures.add(new RenderBoundingBoxProcedure());	
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
		
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures) {
			renderProcedure.execute(graphicsData);
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
		
		// Return a CytoscapeDataProcessor that does not do any processing,
		// because the bird's eye rendering object will not need to access Cytoscape
		// data.
		return new CytoscapeDataProcessor() {

			@Override
			public void processCytoscapeData(GraphicsData graphicsData) {

			}
		};
	}
	
	@Override
	public LightingProcessor getLightingProcessor() {
		return new DefaultLightingProcessor();
	}

	@Override
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures) {
			renderProcedure.initialize(graphicsData);
		}
	}


}
