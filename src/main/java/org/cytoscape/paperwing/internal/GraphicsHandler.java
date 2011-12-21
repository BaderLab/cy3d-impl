package org.cytoscape.paperwing.internal;

import java.util.Map;

import org.cytoscape.paperwing.internal.graphics.BirdsEyeCoordinator;
import org.cytoscape.paperwing.internal.graphics.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;
import org.cytoscape.paperwing.internal.graphics.InputProcessor;
import org.cytoscape.paperwing.internal.graphics.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.graphics.ShapePicker;

// The GraphicsHandler is read-only for given GraphicsData objects
public interface GraphicsHandler {
	
	public InputProcessor getInputProcessor();
	public ShapePicker getShapePicker();
	public BirdsEyeCoordinator getCoordinator(GraphicsData graphicsData);
	public CoordinatorProcessor getCoordinatorProcessor();
	
	// Call procedures' init methods
	public void initializeGraphicsProcedures(GraphicsData graphicsData);
	
	// Clear the scene
	public void resetSceneForDrawing(GraphicsData graphicsData);
	
	// Draw the scene
	public void drawScene(GraphicsData graphicsData);

	// Setup lighting
	public void setupLighting(GraphicsData graphicsData);
}


