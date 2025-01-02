package org.baderlab.cy3d.internal.rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import org.baderlab.cy3d.internal.camera.CameraPosition;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.RenderColor;

public class RenderSelectionBoxProcedure implements GraphicsProcedure {
	
	private static final RenderColor DEFAULT_COLOR = new RenderColor(0.58, 0.68, 0.85);
	
	private float lineWidth;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		float ratio = graphicsData.getPixelConverter().getPixelsPerWindowUnitRatio();
		lineWidth = (float) Math.max(1.0, ratio);
	}

	/**
	 * Draw the drag selection box
	 * 
	 * @param gl
	 *            The {@link GL2} rendering object
	 * @param drawDistance
	 *            The distance from the camera to draw the box
	 */
	@Override
	public void execute(GraphicsData graphicsData) {
		GL2 gl = graphicsData.getGlContext();
		
		if (!graphicsData.getSelectionData().isDragSelectMode()) {
			return;
		}

		int selectTopLeftX = graphicsData.getSelectionData().getSelectTopLeftX();
		int selectTopLeftY = graphicsData.getSelectionData().getSelectTopLeftY();
		int selectBottomRightX = graphicsData.getSelectionData().getSelectBottomRightX();
		int selectBottomRightY = graphicsData.getSelectionData().getSelectBottomRightY();

		int screenWidth = graphicsData.getScreenWidth();
		int screenHeight = graphicsData.getScreenHeight();
		CameraPosition camera = graphicsData.getCamera();
		double drawDistance = graphicsData.getCamera().getDistance();
		

		Vector3 topLeft =    GeometryToolkit.convertScreenTo3d(selectTopLeftX, selectTopLeftY, screenWidth, screenHeight, drawDistance, camera);
		Vector3 bottomLeft = GeometryToolkit.convertScreenTo3d(selectTopLeftX, selectBottomRightY, screenWidth, screenHeight, drawDistance, camera);

		Vector3 topRight =    GeometryToolkit.convertScreenTo3d(selectBottomRightX, selectTopLeftY, screenWidth, screenHeight, drawDistance, camera);
		Vector3 bottomRight = GeometryToolkit.convertScreenTo3d(selectBottomRightX, selectBottomRightY, screenWidth, screenHeight, drawDistance, camera);

		/**
		 * // Below uses older cylinder approach drawSingleSelectEdge(gl,
		 * topLeft, topRight); drawSingleSelectEdge(gl, topLeft, bottomLeft);
		 * 
		 * drawSingleSelectEdge(gl, topRight, bottomRight);
		 * drawSingleSelectEdge(gl, bottomLeft, bottomRight);
		 **/

		boolean disabledLighting = false;
		if (gl.glIsEnabled(GL2.GL_LIGHTING)) {
			gl.glDisable(GL2.GL_LIGHTING);
			disabledLighting = true;
		}
		gl.glDisable(GL.GL_DEPTH_TEST);
		RenderColor.setNonAlphaColors(gl, DEFAULT_COLOR);

		// Below uses converted 3D coordinates
		float[] prev = new float[1];
		gl.glGetFloatv(GL.GL_LINE_WIDTH, prev, 0);
		gl.glLineWidth(lineWidth);
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(topLeft.x(), topLeft.y(), topLeft.z());
		gl.glVertex3d(bottomLeft.x(), bottomLeft.y(), bottomLeft.z());
		gl.glVertex3d(bottomRight.x(), bottomRight.y(), bottomRight.z());
		gl.glVertex3d(topRight.x(), topRight.y(), topRight.z());
		gl.glEnd();
		
		gl.glLineWidth(prev[0]);

		// Below uses raw 2d coordinates
		// gl.glBegin(GL2.GL_LINE_LOOP);
		// gl.glVertex2i(selectTopLeftX, selectTopLeftY);
		// gl.glVertex2i(selectTopLeftX, selectBottomRightY);
		// gl.glVertex2i(selectBottomRightX, selectBottomRightY);
		// gl.glVertex2i(selectBottomRightX, selectTopLeftY);
		// gl.glEnd();

		gl.glEnable(GL.GL_DEPTH_TEST);
		if (disabledLighting) {
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

}
