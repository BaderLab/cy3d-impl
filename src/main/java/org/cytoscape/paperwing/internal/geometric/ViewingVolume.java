package org.cytoscape.paperwing.internal.geometric;

/**
 * This class represents the 6-sided viewing volume of the camera.
 */
public class ViewingVolume {
	
	/**
	 * A class representing a plane, stored using its parameters A, B, C, D in the equation Ax + By + Cz + D = 0.
	 */
	private class Plane {
		public double a, b, c, d = 0;
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
		
		double signedDistance = point.x() * plane.a + point.y() * plane.b + point.z() * plane.c + plane.d;
		
		if (signedDistance > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Calculate the boundaries of the viewing volume given the camera orientation, the distance to the near and far clipping planes,
	 * and the vertical and horizontal fields of view.
	 */
	public void calculateViewingVolume(Vector3 cameraPosition, Vector3 cameraDirection, Vector3 cameraUp, 
			double zNear, double zFar, double verticalFieldOfView, double horizontalFieldOfView) {
		
		
	}

}
