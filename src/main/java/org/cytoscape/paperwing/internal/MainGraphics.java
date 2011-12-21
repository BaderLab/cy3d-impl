package org.cytoscape.paperwing.internal;

import java.util.Map;

import org.cytoscape.paperwing.internal.graphics.BirdsEyeCoordinator;
import org.cytoscape.paperwing.internal.graphics.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;
import org.cytoscape.paperwing.internal.graphics.InputProcessor;
import org.cytoscape.paperwing.internal.graphics.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.graphics.RenderEdgesProcedure;
import org.cytoscape.paperwing.internal.graphics.RenderNodesProcedure;
import org.cytoscape.paperwing.internal.graphics.ShapePicker;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;

public class MainGraphics implements GraphicsHandler {

	@Override
	public InputProcessor getInputProcessor() {
		return new InputProcessor();
	}

	@Override
	public void resetSceneForDrawing(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawScene(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
		// Control light positioning
		float[] lightPosition = { -4.0f, 4.0f, 6.0f, 1.0f };
		
		// Code below toggles the light following the camera
		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
		// FloatBuffer.wrap(lightPosition));
		
	}

	@Override
	public void setupLighting(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ShapePicker getShapePicker() {
		return new ShapePicker(new RenderNodesProcedure(), new RenderEdgesProcedure());
	}

	@Override
	public BirdsEyeCoordinator getCoordinator(GraphicsData graphicsData) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		if (BirdsEyeCoordinator.getCoordinator(networkView) != null) {
			return BirdsEyeCoordinator.getCoordinator(networkView);
		} else {
			return BirdsEyeCoordinator.createCoordinator(networkView);
		}
	}

	@Override
	public CoordinatorProcessor getCoordinatorProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
	}

	
}