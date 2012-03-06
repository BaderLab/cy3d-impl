package org.cytoscape.paperwing.internal.tools;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.cytoscape.paperwing.internal.data.LightingData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.lighting.Light;

public class RenderToolkit {

	
	/** Set up matrix transformations such that the position is 
	 * equal to the location vector and the z-axis is in the direction 
	 * of the given direction
	 * 
	 * @param gl The {@link GL2} rendering object
	 * @param location The desired position
	 * @param direction The desired direction, does not have to be a 
	 * unit vector
	 * 			
	 */
	public static void setUpFacingTransformation(GL2 gl, Vector3 location, Vector3 direction) {
		gl.glTranslatef((float) location.x(), (float) location.y(), (float) location.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 rotateAxis = current.cross(direction);
		
		// Make sure the given normal has nonzero length
		if (direction.magnitudeSquared() > Double.MIN_NORMAL) {
			gl.glRotatef((float) Math.toDegrees(direction.angle(current)), 
					(float) rotateAxis.x(),
					(float) rotateAxis.y(),
					(float) rotateAxis.z());
		}
	}
	
	public static void drawPoint(GL2 gl, Vector3 point) {
		gl.glVertex3f((float) point.x(), 
				(float) point.y(), 
				(float) point.z());
	}

	public static void setNormal(GL2 gl, Vector3 normal) {
		Vector3 normalized = normal.normalize();
		
		gl.glNormal3f((float) normalized.x(),
				(float) normalized.y(),
				(float) normalized.z());
	}
	
	public static Vector3 convert3dToScreen(GL2 gl, Vector3 position) {
		GLU glu = GLU.createGLU(gl);
		
		double modelView[] = new double[16];
		double projection[] = new double[16];
		int viewPort[] = new int[4];
		
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelView, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
        
        double result[] = new double[4];
        
		glu.gluProject(position.x(), position.y(), position.z(), 
				modelView, 0, projection, 0, viewPort, 0, result, 0);
		
		return new Vector3(result[0], result[1], result[2]);
	}
	
	public static Vector3 convert3dToScreen(GL2 gl, Vector3 position, double[] modelView, double[] projection, int[] viewPort) {
		GLU glu = GLU.createGLU(gl);
		
        double result[] = new double[4];
        
		glu.gluProject(position.x(), position.y(), position.z(), 
				modelView, 0, projection, 0, viewPort, 0, result, 0);
		
		return new Vector3(result[0],
				result[1],
				result[2]);
	}
	
	public static Vector3 convertScreenTo3d(GL2 gl, int x, int y) {
		GLU glu = GLU.createGLU(gl);
		
		double modelView[] = new double[16];
		double projection[] = new double[16];
		int viewPort[] = new int[4];
		
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelView, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
		
		double result[] = new double[4];
		
		glu.gluUnProject(x, y, 0, modelView, 0, projection, 0, viewPort, 0, result, 0);
		
		return new Vector3(result[0], result[1], result[2]);
	}
}

