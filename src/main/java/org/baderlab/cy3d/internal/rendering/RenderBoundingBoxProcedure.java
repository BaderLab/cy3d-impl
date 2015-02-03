package org.baderlab.cy3d.internal.rendering;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.baderlab.cy3d.internal.camera.CameraPosition;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Quadrilateral;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.RenderColor;
import org.baderlab.cy3d.internal.tools.RenderToolkit;

public class RenderBoundingBoxProcedure implements ReadOnlyGraphicsProcedure {

	private static final RenderColor DEFAULT_COLOR = new RenderColor(0.3, 0.3, 0.3);
	
	// Distance between bounds and camera position
	private static final double NEAR_BOUNDS_DISTANCE = 0.9;
	// Distance between the back bounds and the camera
	private static final double FAR_BOUNDS_DISTANCE = 1.1;
	
	private GraphicsData graphicsData;
	private Quadrilateral nearBounds;
//	private Quadrilateral farBounds;
	
	private float lineWidth;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		float ratio = graphicsData.getPixelConverter().getPixelsPerWindowUnitRatio();
		lineWidth = 1.5f * (float) Math.max(1.0, ratio);
	}

	@Override
	public void execute(GraphicsData graphicsData) {
		drawFullBox(graphicsData);
		//drawViewingVolumePortion(graphicsData);
		//drawHalfBox(graphicsData);
	}
	
	
	/**
	 * The argument must be the camera from the main renderer.
	 * @param mainCamera
	 */
	public void updateBounds(CameraPosition mainCamera) {
		double aspectRatio = ((double)graphicsData.getScreenWidth()) / Math.max(1, graphicsData.getScreenHeight());
		
		nearBounds = GeometryToolkit.generateViewingBounds(
						mainCamera.getPosition(), mainCamera.getDirection(), mainCamera.getUp(),
						NEAR_BOUNDS_DISTANCE, GraphicsData.VERTICAL_VOF, aspectRatio);
		
//		farBounds = GeometryToolkit.generateViewingBounds(
//						mainCamera.getPosition(), mainCamera.getDirection(), mainCamera.getUp(),
//						FAR_BOUNDS_DISTANCE, GraphicsData.VERTICAL_VOF, aspectRatio);
	}
	
	
	private void drawHalfBox(GraphicsData graphicsData) {
		if(nearBounds == null)
			return;
		
		GL2 gl = graphicsData.getGlContext();
		
		Quadrilateral bounds = nearBounds;
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
		
		boolean disabledLighting = false;
		if (gl.glIsEnabled(GL2.GL_LIGHTING)) {
			gl.glDisable(GL2.GL_LIGHTING);
			disabledLighting = true;
		}
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		RenderColor.setNonAlphaColors(gl, DEFAULT_COLOR);
		
		float[] prev = new float[1];
		gl.glGetFloatv(GL.GL_LINE_WIDTH, prev, 0);
		gl.glLineWidth(lineWidth);
		
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
		
		gl.glLineWidth(prev[0]);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		if (disabledLighting) {
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

	private void drawFullBox(GraphicsData graphicsData) {
		if(nearBounds == null)
			return;
		
		GL2 gl = graphicsData.getGlContext();
	
		Quadrilateral bounds = nearBounds;

		Vector3 topLeft = bounds.getTopLeft();
		Vector3 topRight = bounds.getTopRight();
		
		Vector3 bottomLeft = bounds.getBottomLeft();
		Vector3 bottomRight = bounds.getBottomRight();
		
		boolean disabledLighting = false;
		if (gl.glIsEnabled(GL2.GL_LIGHTING)) {
			gl.glDisable(GL2.GL_LIGHTING);
			disabledLighting = true;
		}
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		RenderColor.setNonAlphaColors(gl, DEFAULT_COLOR);
		
		float[] prev = new float[1];
		gl.glGetFloatv(GL.GL_LINE_WIDTH, prev, 0);
		gl.glLineWidth(lineWidth);
		
		// Below uses converted 3D coordinates
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(topLeft.x(), topLeft.y(), topLeft.z());
		gl.glVertex3d(bottomLeft.x(), bottomLeft.y(), bottomLeft.z());
		gl.glVertex3d(bottomRight.x(), bottomRight.y(), bottomRight.z());
		gl.glVertex3d(topRight.x(), topRight.y(), topRight.z());
		gl.glEnd();
		
		gl.glLineWidth(prev[0]);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		if (disabledLighting) {
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}
	
	/** Draws a portion of the viewing volume.
	 */
	private void drawViewingVolumePortion(GraphicsData graphicsData) {
		if(nearBounds == null /* || farBounds == null */)
			return;
		
		GL2 gl = graphicsData.getGlContext();
		
		Quadrilateral frontFace = nearBounds;
		Quadrilateral backFace  = null /* farBounds */;

		boolean disabledLighting = false;
		if (gl.glIsEnabled(GL2.GL_LIGHTING)) {
			gl.glDisable(GL2.GL_LIGHTING);
			disabledLighting = true;
		}
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		
		gl.glBegin(GL2.GL_LINE_STRIP);
		
		// Front face
		RenderToolkit.drawPoint(gl, frontFace.getTopLeft());
		RenderToolkit.drawPoint(gl, frontFace.getTopRight());
		RenderToolkit.drawPoint(gl, frontFace.getBottomRight());
		RenderToolkit.drawPoint(gl, frontFace.getBottomLeft());
		RenderToolkit.drawPoint(gl, frontFace.getTopLeft());
		
		// Back face (includes top left edge)
		RenderToolkit.drawPoint(gl, backFace.getTopLeft());
		RenderToolkit.drawPoint(gl, backFace.getBottomLeft());
		RenderToolkit.drawPoint(gl, backFace.getBottomRight());
		RenderToolkit.drawPoint(gl, backFace.getTopRight());
		RenderToolkit.drawPoint(gl, backFace.getTopLeft());
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINES);
		
		// Bottom left edge
		RenderToolkit.drawPoint(gl, frontFace.getBottomLeft());
		RenderToolkit.drawPoint(gl, backFace.getBottomLeft());
		
		// Bottom right edge
		RenderToolkit.drawPoint(gl, frontFace.getBottomRight());
		RenderToolkit.drawPoint(gl, backFace.getBottomRight());
		
		// Top right edge
		RenderToolkit.drawPoint(gl, frontFace.getTopRight());
		RenderToolkit.drawPoint(gl, backFace.getTopRight());
		
		gl.glEnd();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		if (disabledLighting) {
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}
}
