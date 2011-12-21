package org.cytoscape.paperwing.internal.graphics;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;

public class RenderBoundingBoxProcedure implements ReadOnlyGraphicsProcedure {

	@Override
	public void initialize(GraphicsData graphicsData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		Vector3 topLeft = graphicsData.getCoordinatorData().getTopLeftBound();
		Vector3 topRight = graphicsData.getCoordinatorData().getTopRightBound();
		
		Vector3 bottomLeft = graphicsData.getCoordinatorData().getBottomLeftBound();
		Vector3 bottomRight = graphicsData.getCoordinatorData().getBottomRightBound();
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glColor3f(0.7f, 0.7f, 0.6f);
		
		// Below uses converted 3D coordinates
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(topLeft.x(), topLeft.y(), topLeft.z());
		gl.glVertex3d(bottomLeft.x(), bottomLeft.y(), bottomLeft.z());
		gl.glVertex3d(bottomRight.x(), bottomRight.y(), bottomRight.z());
		gl.glVertex3d(topRight.x(), topRight.y(), topRight.z());
		gl.glEnd();
		
		gl.glEnable(GL2.GL_LIGHTING);
	}

}
