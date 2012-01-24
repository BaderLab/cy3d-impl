package org.cytoscape.paperwing.internal;

import java.util.Map;

import org.cytoscape.paperwing.internal.coordinator.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
import org.cytoscape.paperwing.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.input.InputProcessor;
import org.cytoscape.paperwing.internal.picking.ShapePickingProcessor;
import org.cytoscape.paperwing.internal.rendering.ReadOnlyGraphicsProcedure;

// The GraphicsHandler is read-only for given GraphicsData objects
public interface GraphicsHandler {
	
	public InputProcessor getInputProcessor();
	public ShapePickingProcessor getShapePickingProcessor();
	public ViewingCoordinator getCoordinator(GraphicsData graphicsData);
	public CoordinatorProcessor getCoordinatorProcessor();
	public CytoscapeDataProcessor getCytoscapeDataProcessor();
	
	// Call procedures' init methods
	public void initializeGraphicsProcedures(GraphicsData graphicsData);
	
	// Draw the scene
	public void drawScene(GraphicsData graphicsData);

	// Setup lighting
	public void setupLighting(GraphicsData graphicsData);
}


