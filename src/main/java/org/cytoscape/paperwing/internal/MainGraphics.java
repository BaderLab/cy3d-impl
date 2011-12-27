package org.cytoscape.paperwing.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.cytoscape.paperwing.internal.graphics.MainInputProcessor;
import org.cytoscape.paperwing.internal.graphics.ViewingCoordinator;
import org.cytoscape.paperwing.internal.graphics.CoordinatorProcessor;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;
import org.cytoscape.paperwing.internal.graphics.InputProcessor;
import org.cytoscape.paperwing.internal.graphics.MainCoordinatorProcessor;
import org.cytoscape.paperwing.internal.graphics.ReadOnlyGraphicsProcedure;
import org.cytoscape.paperwing.internal.graphics.RenderEdgesProcedure;
import org.cytoscape.paperwing.internal.graphics.RenderNodesProcedure;
import org.cytoscape.paperwing.internal.graphics.RenderSelectionBoxProcedure;
import org.cytoscape.paperwing.internal.graphics.ResetSceneProcedure;
import org.cytoscape.paperwing.internal.graphics.ShapePicker;
import org.cytoscape.paperwing.internal.graphics.ShapePickingProcessor;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;

public class MainGraphics implements GraphicsHandler {

	private Map<String, ReadOnlyGraphicsProcedure> renderProcedures;
	
	public MainGraphics() {
		renderProcedures = new LinkedHashMap<String, ReadOnlyGraphicsProcedure>();
		
		renderProcedures.put("nodes", new RenderNodesProcedure());
		renderProcedures.put("edges", new RenderEdgesProcedure());
		renderProcedures.put("selectionBox", new RenderSelectionBoxProcedure());
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
	}

	@Override
	public void setupLighting(GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ShapePickingProcessor getShapePickingProcessor() {
		return new ShapePickingProcessor(new RenderNodesProcedure(), new RenderEdgesProcedure());
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
	public void initializeGraphicsProcedures(GraphicsData graphicsData) {
		for (ReadOnlyGraphicsProcedure renderProcedure : renderProcedures.values()) {
			renderProcedure.initialize(graphicsData);
		}
	}

	
}