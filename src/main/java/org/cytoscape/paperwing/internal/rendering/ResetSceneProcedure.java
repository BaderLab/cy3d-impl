package org.cytoscape.paperwing.internal.rendering;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;

public class ResetSceneProcedure implements ReadOnlyGraphicsProcedure {

	GLU glu;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		glu = GLU.createGLU(graphicsData.getGlContext());
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		SimpleCamera camera = graphicsData.getCamera();
		
		// Reset scene
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();

		glu.gluLookAt((float) position.x(), (float) position.y(), (float) position.z(), 
				(float) target.x(), (float) target.y(), (float) target.z(), 
				(float) up.x(), (float) up.y(), (float) up.z());
	}

}
