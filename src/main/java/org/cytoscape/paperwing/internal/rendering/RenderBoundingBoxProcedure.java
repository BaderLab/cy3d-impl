package org.cytoscape.paperwing.internal.rendering;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.RenderColor;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;

public class RenderBoundingBoxProcedure implements ReadOnlyGraphicsProcedure {

	private static final RenderColor DEFAULT_COLOR = 
		new RenderColor(0.27, 0.27, 0.27);
	
	@Override
	public void initialize(GraphicsData graphicsData) {
	}

	@Override
	public void execute(GraphicsData graphicsData) {
//		drawFullBox(graphicsData);
		
		if (graphicsData.getCoordinatorData().isInitialBoundsMatched()) {
			drawHalfBox(graphicsData);
		}
	}
	
	private void drawHalfBox(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		Quadrilateral bounds = graphicsData.getCoordinatorData().getBounds();
		double fraction = 0.25;
		
		Vector3 topLeft = bounds.getTopLeft();
		Vector3 topRight = bounds.getTopRight();
		
		Vector3 bottomLeft = bounds.getBottomLeft();
		Vector3 bottomRight = bounds.getBottomRight();
		
		Vector3 topLeftDown = topLeft.towards(bottomLeft, fraction);
		Vector3 topLeftRight = topLeft.towards(topRight, fraction);
		
		Vector3 topRightDown = topRight.towards(bottomRight, fraction);
		Vector3 topRightLeft = topRight.towards(topLeft, fraction);
		
		Vector3 bottomLeftUp = bottomLeft.towards(topLeft, fraction);
		Vector3 bottomLeftRight = bottomLeft.towards(bottomRight, fraction);
		
		Vector3 bottomRightUp = bottomRight.towards(topRight, fraction);
		Vector3 bottomRightLeft = bottomRight.towards(bottomLeft, fraction);
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		RenderColor.setNonAlphaColors(gl, DEFAULT_COLOR);
		
		// Below uses converted 3D coordinates
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3d(topLeftDown.x(), topLeftDown.y(), topLeftDown.z());
		gl.glVertex3d(topLeft.x(), topLeft.y(), topLeft.z());
		gl.glVertex3d(topLeftRight.x(), topLeftRight.y(), topLeftRight.z());
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3d(bottomLeftUp.x(), bottomLeftUp.y(), bottomLeftUp.z());
		gl.glVertex3d(bottomLeft.x(), bottomLeft.y(), bottomLeft.z());
		gl.glVertex3d(bottomLeftRight.x(), bottomLeftRight.y(), bottomLeftRight.z());
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3d(bottomRightUp.x(), bottomRightUp.y(), bottomRightUp.z());
		gl.glVertex3d(bottomRight.x(), bottomRight.y(), bottomRight.z());
		gl.glVertex3d(bottomRightLeft.x(), bottomRightLeft.y(), bottomRightLeft.z());
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3d(topRightDown.x(), topRightDown.y(), topRightDown.z());
		gl.glVertex3d(topRight.x(), topRight.y(), topRight.z());
		gl.glVertex3d(topRightLeft.x(), topRightLeft.y(), topRightLeft.z());
		gl.glEnd();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LIGHTING);
	}

	private void drawFullBox(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
	
		Quadrilateral bounds = graphicsData.getCoordinatorData().getBounds();

		Vector3 topLeft = bounds.getTopLeft();
		Vector3 topRight = bounds.getTopRight();
		
		Vector3 bottomLeft = bounds.getBottomLeft();
		Vector3 bottomRight = bounds.getBottomRight();
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		
		// Below uses converted 3D coordinates
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(topLeft.x(), topLeft.y(), topLeft.z());
		gl.glVertex3d(bottomLeft.x(), bottomLeft.y(), bottomLeft.z());
		gl.glVertex3d(bottomRight.x(), bottomRight.y(), bottomRight.z());
		gl.glVertex3d(topRight.x(), topRight.y(), topRight.z());
		gl.glEnd();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LIGHTING);
	}
}