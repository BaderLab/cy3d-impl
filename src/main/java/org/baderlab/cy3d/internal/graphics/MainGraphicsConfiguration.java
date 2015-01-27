package org.baderlab.cy3d.internal.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.coordinator.CoordinatorProcessor;
import org.baderlab.cy3d.internal.coordinator.MainCoordinatorProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.LightingData;
import org.baderlab.cy3d.internal.input.handler.MainInputEventListener;
import org.baderlab.cy3d.internal.input.handler.MouseMode;
import org.baderlab.cy3d.internal.input.handler.ToolPanel;
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
 * An implementation for the {@link GraphicsConfiguration} interface to be used
 * for main rendering windows. That is, this handler fully supports keyboard
 * and mouse input, as well as selection and picking.
 * 
 */
public class MainGraphicsConfiguration extends AbstractGraphicsConfiguration implements ToolPanel.ToolPanelListener {
	
	private MainInputEventListener inputHandler;
	private RenderNodeLabelsProcedure renderNodeLabelsProcedure;
	
	public MainGraphicsConfiguration() {
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
		add(new RenderSelectionBoxProcedure());
		add(new RenderLightsProcedure());
		
		// Make label rendering the last element in the list so that adding and removing it doesn't change the order
		// of the other elements in the list. 
		// (Note, if more complex enabling/disabling of render procedures is needed in the future then AbstractGraphicsConfiguration
		// should be changed to have a more sophisticated ordering mechanism.)
		add(renderNodeLabelsProcedure = new RenderNodeLabelsProcedure());
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
	
	@Override
	public void trackInput(JComponent component, GraphicsData graphicsData) {
		inputHandler = MainInputEventListener.attach(component, graphicsData);
	}
	
	@Override
	public void setUpContainer(JComponent container) {
		if(container instanceof JInternalFrame) {
			JInternalFrame frame = (JInternalFrame) container;
			ToolPanel toolPanel = new ToolPanel(frame);
			toolPanel.addToolbarListener(inputHandler);
			toolPanel.addToolbarListener(this);
		}
	}
	
	@Override
	public void dispose(GraphicsData gd) {
		inputHandler.dispose();
	}
	
	@Override
	public void showLabelsChanged(boolean showLabels) {
		if(showLabels) {
			add(renderNodeLabelsProcedure);
		} else {
			remove(renderNodeLabelsProcedure);
		}
		inputHandler.touch();
	}
	
	@Override
	public void mouseModeChanged(MouseMode mouseMode) {
	}
	
}