package org.baderlab.cy3d.internal.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.coordinator.CoordinatorProcessor;
import org.baderlab.cy3d.internal.coordinator.MainCoordinatorProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.LightingData;
import org.baderlab.cy3d.internal.input.InputProcessor;
import org.baderlab.cy3d.internal.input.MainInputProcessor;
import org.baderlab.cy3d.internal.lighting.Light;
import org.baderlab.cy3d.internal.picking.DefaultShapePickingProcessor;
import org.baderlab.cy3d.internal.picking.ShapePickingProcessor;
import org.baderlab.cy3d.internal.rendering.PositionCameraProcedure;
import org.baderlab.cy3d.internal.rendering.RenderArcEdgesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderLightsProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodeLabelsProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderSelectionBoxProcedure;
import org.baderlab.cy3d.internal.rendering.ResetSceneProcedure;

/**
 * An implementation for the {@link GraphicsHandler} interface to be used
 * for main rendering windows. That is, this handler fully supports keyboard
 * and mouse input, as well as selection and picking.
 * 
 */
public class MainGraphicsHandler extends AbstractGraphicsHandler {
	
	public MainGraphicsHandler() {
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
		add(new RenderSelectionBoxProcedure());
		
		add(new RenderNodeLabelsProcedure());
		add(new RenderLightsProcedure());
	}
	
	@Override
	public InputProcessor getInputProcessor() {
		return new MainInputProcessor();
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
	public CoordinatorProcessor getCoordinatorProcessor() {
		return new MainCoordinatorProcessor();
	}

	@Override
	public CytoscapeDataProcessor getCytoscapeDataProcessor() {
		return new MainCytoscapeDataProcessor();
	}
	
	@Override
	public String toString() {
		return "MainGraphicsHandler";
	}
	
}