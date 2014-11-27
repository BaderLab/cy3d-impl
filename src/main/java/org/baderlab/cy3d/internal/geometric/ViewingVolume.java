package org.baderlab.cy3d.internal.geometric;

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
	
	public ViewingVolume() {
		near = new Plane();
		far = new Plane();
		top = new Plane();
		bottom = new Plane();
		left = new Plane();
		right = new Plane();
	}
	
	/**
	 * Tests if a given point is inside the viewing volume.
	 * 
	 * @param point The test point
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
	 * Tests if a given point either inside the viewing volume or has a distance to the viewing volume
	 * not exceeding a certain given value.
	 * 
	 * @param point The test point
	 * @param distance The maximum distance between the point and the volume volume before the point is 
	 * considered to be outside the viewing volume.
	 * @return <code>true</code> If the point is inside or within a certain distance from 
	 */
	public boolean inside(Vector3 point, double distance) {
		if (isInsidePlane(point, near, distance) &&
				isInsidePlane(point, far, distance) &&
				isInsidePlane(point, top, distance) &&
				isInsidePlane(point, bottom, distance) &&
				isInsidePlane(point, left, distance) &&
				isInsidePlane(point, right, distance)) {
				
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
	 * Checks if a point is inside the given plane. If it is not, this method still returns <code>true</code>
	 * as long as the point is within a certain distance from the plane.
	 * 
	 * @param point The test point
	 * @param plane The plane to test against
	 * @param radius The maximum distance the point can be to the plane before it is considered to be outside the plane
	 * @return <code>true</code> if the point lies on the opposite side of the plane's normal, within the given distance.
	 * Returns <code>false</code> otherwise.
	 */
	private boolean isInsidePlane(Vector3 point, Plane plane, double distance) {
		double signedDistance = plane.normal.dot(point) + plane.parameterD;
		
		if (signedDistance > distance) {
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
		
		// Find the center point on the near plane for later use
		Vector3 nearCenterPoint = cameraPosition.plus(cameraDirection.multiply(zNear));
		
		// Calculate left plane
		
		// Rotate 90 degrees past the left plane to obtain the normal
		Vector3 leftNormal = cameraDirection.rotate(cameraUp, Math.toRadians(horizontalFieldOfView / 2 + 90));
		
		// Find a point on the plane to find the D parameter
		Vector3 leftSamplePosition = cameraLeft.multiply(Math.tan(Math.toRadians(horizontalFieldOfView) / 2) * zNear).plus(nearCenterPoint);
		double leftParameterD = -leftNormal.dot(leftSamplePosition);
		left.set(leftNormal, leftParameterD);
		
		// Calculate right plane
		
		// Rotate 90 degrees past the right plane to obtain the normal
		Vector3 rightNormal = cameraDirection.rotate(cameraUp, Math.toRadians(horizontalFieldOfView / 2 + 90));
		
		// Find a point on the plane to find the D parameter
		Vector3 rightSamplePosition = cameraLeft.multiply(Math.tan(Math.toRadians(horizontalFieldOfView) / 2) * -zNear).plus(nearCenterPoint);
		double rightParameterD = -rightNormal.dot(rightSamplePosition);
		right.set(rightNormal, rightParameterD);
		
		// Calculate top plane
		
		Vector3 topNormal = cameraDirection.rotate(cameraLeft, -Math.toRadians(verticalFieldOfView / 2 + 90));
		
		// Find a point on the plane
		Vector3 topSamplePosition = cameraUp.multiply(Math.tan(Math.toRadians(verticalFieldOfView) / 2) * zNear).plus(nearCenterPoint);
		double topParameterD = -topNormal.dot(topSamplePosition);
		top.set(topNormal, topParameterD);
		
		// Calculate bottom plane
		
		Vector3 bottomNormal = cameraDirection.rotate(cameraLeft, Math.toRadians(verticalFieldOfView / 2 + 90));
		
		// Find a point on the plane
		Vector3 bottomSamplePosition = cameraUp.multiply(Math.tan(Math.toRadians(verticalFieldOfView) / 2) * -zNear).plus(nearCenterPoint);
		double bottomParameterD = -bottomNormal.dot(bottomSamplePosition);
		bottom.set(bottomNormal, bottomParameterD);
	}

}
