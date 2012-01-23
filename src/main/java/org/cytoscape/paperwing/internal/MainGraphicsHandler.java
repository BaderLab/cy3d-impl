package org.cytoscape.paperwing.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.MainCoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
import org.cytoscape.paperwing.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.input.InputProcessor;
import org.cytoscape.paperwing.internal.input.MainInputProcessor;
import org.cytoscape.paperwing.internal.picking.DefaultShapePickingProcessor;
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.rendering.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderArcEdgesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderEdgesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderLabelsProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderNodesProcedure;
import org.cytoscape.paperwing.internal.rendering.RenderSelectionBoxProcedure;
import org.cytoscape.paperwing.internal.rendering.ResetSceneProcedure;
import org.cytoscape.paperwing.internal.rendering.text.StringRenderer;
import org.cytoscape.view.model.CyNetworkView;

public class MainGraphicsHandler implements GraphicsHandler {

	private Map<String, ReadOnlyGraphicsProcedure> renderProcedures;
	
	public MainGraphicsHandler() {
		renderProcedures = new LinkedHashMap<String, ReadOnlyGraphicsProcedure>();
		
		renderProcedures.put("nodes", new RenderNodesProcedure());
		renderProcedures.put("edges", new RenderArcEdgesProcedure());
		renderProcedures.put("selectionBox", new RenderSelectionBoxProcedure());
		renderProcedures.put("labels", new RenderLabelsProcedure());
		
		renderProcedures.put("resetScene", new ResetSceneProcedure());
	}
	
	@Override
	public InputProcessor getInputProcessor() {
		return new MainInputProcessor();
	}

	@Override
	public void resetSceneForDrawing(GraphicsData graphicsData) {
		renderProcedures.get("resetScene").execute(graphicsData);
	}

	@Override
	public void drawScene(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
		// Control light positioning
		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
		
		// Code below toggles the light following the camera
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
		// FloatBuffer.wrap(lightPosition));
		
		renderProcedures.get("edges").execute(graphicsData);
		renderProcedures.get("nodes").execute(graphicsData);
		renderProcedures.get("selectionBox").execute(graphicsData);
		renderProcedures.get("labels").execute(graphicsData);
		
	}

	@Override
	public void setupLighting(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
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
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures.values()) {
			renderProcedure.initialize(graphicsData);
		}
	}
	
}