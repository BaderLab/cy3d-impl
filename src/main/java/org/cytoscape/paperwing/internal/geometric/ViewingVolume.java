package org.cytoscape.paperwing.internal.geometric;

/**
 * This class represents the 6-sided viewing volume of the camera.
 */
public class ViewingVolume {
	
	/**
	 * A class representing a plane, stored in the format Ax + By + Cz + D = 0.
	 * The parameters A, B, C are represented by the normal vector.
	 */
	private class Plane {
		Vector3 normal = new Vector3(0, 0, 0);
		double parameterD = 0;
		
		/**
		 * Sets the plane's normal and D parameter to the given values
		 * @param normal The plane's new normal
		 * @param parameterD The plane's new D parameter in the equation Ax + By + Cz + D = 0
		 */
		public void set(Vector3 normal, double parameterD) {
			this.normal.set(normal);
			this.parameterD = parameterD;
		}
	}
	
	// The 6 planes that bound the viewing volume. The normals of the plane face outward.
	private Plane near, far;
	private Plane top, bottom;
	private Plane left, right;
	
	/**
	 * Tests if a given point is inside the viewing volume.
	 * 
	 * @param point
	 * @return <code>true</code> if the point is inside the volume, <code>false</code> otherwise.
	 */
	public boolean inside(Vector3 point) {
		if (isInsidePlane(point, near) &&
			isInsidePlane(point, far) &&
			isInsidePlane(point, top) &&
			isInsidePlane(point, bottom) &&
			isInsidePlane(point, left) &&
			isInsidePlane(point, right)) {
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if a point is inside the given plane, that is, it lies on the opposite side of the normal.
	 * 
	 * @param point The test point
	 * @param plane The plane to test against
	 * @return <code>true</code> if the point lies on the opposite side of the plane's normal, <code>false</code> otherwise.
	 */
	private boolean isInsidePlane(Vector3 point, Plane plane) {
		// Determine which side the point lies on using the distance formula
		// For information see http://www.lighthouse3d.com/tutorials/maths/plane/
		
		double signedDistance = plane.normal.dot(point) + plane.parameterD;
		
		if (signedDistance > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Calculate the boundaries of the viewing volume given the camera orientation, the distance to the near and far clipping planes,
	 * and the vertical and horizontal fields of view.
	 * 
	 * @param cameraPosition The position of the camera or eye
	 * @param cameraDirection The camera's direction vector
	 * @param cameraUp The camera's up vector
	 * @param zNear The distance between the camera and the near clipping plane
	 * @param zFar The distance between the camera and the far clipping plane
	 * @param verticalFieldOfView The vertical field of view, in degrees
	 * @param horizontalFieldOfView The horizontal field of view, in degrees
	 */
	public void calculateViewingVolume(Vector3 cameraPosition, Vector3 cameraDirection, Vector3 cameraUp, 
			double zNear, double zFar, double verticalFieldOfView, double horizontalFieldOfView) {
		
		// Calculate the camera's left vector
		Vector3 cameraLeft = cameraUp.cross(cameraDirection);
		
		// Calculate near z plane
		Vector3 nearNormal = cameraDirection.invert();
		nearNormal.normalize();
		
		double nearParameterD = -nearNormal.dot(cameraPosition.plus(cameraDirection.multiply(zNear)));
		near.set(nearNormal, nearParameterD);
		
		// Calculate far z plane
		Vector3 farNormal = nearNormal.invert();
		
		double farParameterD = -farNormal.dot(cameraPosition.plus(cameraDirection.multiply(zFar)));
		far.set(farNormal, farParameterD);
		
		// Calculate left plane
		
		// Rotate 90 degrees past the left plane to obtain the normal
		Vector3 leftNormal = cameraDirection.rotate(cameraUp, Math.toRadians(horizontalFieldOfView / 2 + 90));
		
		// Find a point on the plane to find the D parameter
		
	}

}
