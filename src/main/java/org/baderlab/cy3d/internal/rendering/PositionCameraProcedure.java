package org.baderlab.cy3d.internal.rendering;

import javax.media.opengl.glu.GLU;

import org.baderlab.cy3d.internal.camera.CameraPosition;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;

public class PositionCameraProcedure implements ReadOnlyGraphicsProcedure {

	GLU glu;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		glu = GLU.createGLU(graphicsData.getGlContext());
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		CameraPosition camera = graphicsData.getCamera();
		
		Vector3 position = camera.getPosition();
		Vector3 target = camera.getTarget();
		Vector3 up = camera.getUp();
		
		glu.gluLookAt(
				position.x(), position.y(), position.z(), 
				target.x(), target.y(), target.z(), 
				up.x(), up.y(), up.z());
	}
}
