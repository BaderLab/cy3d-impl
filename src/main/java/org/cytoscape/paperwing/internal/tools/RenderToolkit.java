package org.cytoscape.paperwing.internal.tools;

import javax.media.opengl.GL2;

import org.cytoscape.paperwing.internal.geometric.Vector3;

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
		
		gl.glRotatef((float) Math.toDegrees(direction.angle(current)), 
				(float) rotateAxis.x(),
				(float) rotateAxis.y(),
				(float) rotateAxis.z());
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
}
