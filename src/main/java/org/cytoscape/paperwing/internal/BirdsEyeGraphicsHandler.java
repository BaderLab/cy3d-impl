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
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.rendering.PositionCameraProcedure;
import org.cytoscape.paperwing.internal.rendering.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderArcEdgesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderBoundingBoxProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderEdgesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderNodesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderSelectionBoxProcedure;
import org.cytoscape.paperwing.internal.rendering.ResetSceneProcedure;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;

public class BirdsEyeGraphicsHandler implements GraphicsHandler {

private List<ReadOnlyGraphicsProcedure> renderProcedures;

	public BirdsEyeGraphicsHandler() {
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
	}

	@Override
	public void setupLighting(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ShapePickingProcessor getShapePickingProcessor() {
		return new ShapePickingProcessor() {

			@Override
			public void initialize(GraphicsData graphicsData) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void processPicking(MouseMonitor mouse,
					KeyboardMonitor keys, GraphicsData graphicsData) {
				// TODO Auto-generated method stub
				
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
		return new CytoscapeDataProcessor() {

			@Override
			public void processCytoscapeData(GraphicsData graphicsData) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures) {
			renderProcedure.initialize(graphicsData);
		}
	}


}
