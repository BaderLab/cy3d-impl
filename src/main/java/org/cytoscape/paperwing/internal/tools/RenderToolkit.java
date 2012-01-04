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
		gl.glTranslated(location.x(), location.y(), location.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 rotateAxis = current.cross(direction);
		
		gl.glRotated(Math.toDegrees(direction.angle(current)), rotateAxis.x(), rotateAxis.y(),
				rotateAxis.z());
	}

}
