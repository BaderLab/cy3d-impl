package org.baderlab.cy3d.internal.graphics;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.swing.JComponent;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.rendering.GraphicsProcedure;

public abstract class AbstractGraphicsConfiguration implements GraphicsConfiguration {

	private List<GraphicsProcedure> renderProcedures = new LinkedList<GraphicsProcedure>();
	
	protected GraphicsData graphicsData;
	
	
	protected void add(GraphicsProcedure procedure) {
		if(!renderProcedures.contains(procedure))
			renderProcedures.add(procedure);
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		for (GraphicsProcedure proc : renderProcedures) {
			proc.initialize(graphicsData);
		}
		initLighting(graphicsData.getGlContext());
	}
	
	
	private void initLighting(GL2 gl) {
		float[] global = { 0.4f, 0.4f, 0.4f, 1.0f };

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global));
		gl.glShadeModel(GL2.GL_SMOOTH);

		float[] diffuse = { 0.7f, 0.7f, 0.7f, 0.7f };
		float[] position = { -4.0f, 4.0f, 6.0f, 0.0f };
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));
		gl.glEnable(GL2.GL_LIGHT0);
		
		
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
	}
	
	
	@Override
	public void drawScene() {
		for (GraphicsProcedure proc : renderProcedures) {
			proc.execute(graphicsData);
		}
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void initializeFrame(JComponent container, JComponent inputComponent) {
	}

}
