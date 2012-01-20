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

//		System.out.println(position);
//		System.out.println(target);
//		System.out.println(up);
		
		glu.gluLookAt(
				position.x(), position.y(), position.z(), 
				target.x(), target.y(), target.z(), 
				up.x(), up.y(), up.z());
	}

}
