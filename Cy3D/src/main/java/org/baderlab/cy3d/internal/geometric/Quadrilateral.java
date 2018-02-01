package org.baderlab.cy3d.internal.geometric;



public class Quadrilateral {
	private static double MINIMUM_DIVISOR = Double.MIN_NORMAL;
	
	private Vector3 topLeftOffset;
	private Vector3 topRightOffset;
	private Vector3 bottomLeftOffset;
	private Vector3 bottomRightOffset;
	
	private Vector3 centerPoint;
	
	public Quadrilateral(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight) {
		centerPoint = findCenter(topLeft, topRight, bottomLeft, bottomRight);
		
		topLeftOffset = topLeft.subtract(centerPoint);
		topRightOffset = topRight.subtract(centerPoint);
		bottomLeftOffset = bottomLeft.subtract(centerPoint);
		bottomRightOffset = bottomRight.subtract(centerPoint);
	}
	
	private Vector3 findCenter(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight) {
		Vector3 center = new Vector3();
		
		center.addLocal(topLeft);
		center.addLocal(topRight);
		center.addLocal(bottomLeft);
		center.addLocal(bottomRight);
		center.divideLocal(4.0);
		
		return center;
	}
	
	public Quadrilateral() {
		this(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0));
	}
	
	public Vector3 getTopLeft() {
		return centerPoint.plus(topLeftOffset);
	}

	public Vector3 getTopRight() {
		return centerPoint.plus(topRightOffset);
	}

	public Vector3 getBottomLeft() {
		return centerPoint.plus(bottomLeftOffset);
	}

	public Vector3 getBottomRight() {
		return centerPoint.plus(bottomRightOffset);
	}

	
	public void set(Quadrilateral other) {
		centerPoint.set(other.centerPoint);
		
		topLeftOffset.set(other.topLeftOffset);
		topRightOffset.set(other.topRightOffset);
		bottomLeftOffset.set(other.bottomLeftOffset);
		bottomRightOffset.set(other.bottomRightOffset);
	}

	public Quadrilateral copy() {
		return new Quadrilateral(getTopLeft(), getTopRight(), getBottomLeft(), getBottomRight());
	}
	
	// Return average point
	public Vector3 getCenterPoint() {
		return centerPoint.copy();
	}
	
	public void moveTo(Vector3 newCenter) {
		centerPoint.set(newCenter);
	}
	
	public String toString() {
		String result = "";
		result += "Top Left: " + getTopLeft() + ", ";
		result += "Top Right: " + getTopRight() + ", ";
		result += "Bottom Left: " + getBottomLeft() + ", ";
		result += "Bottom Right: " + getBottomRight();
		
		return result;
	}
	
//	// Project the quadrilateral onto a plane
//	public Quadrilateral projectOntoPlane(Vector3 sourcePoint, double newDistanceToCenter) {
//
//		double currentDistance = sourcePoint.distance(getCenterPoint());
//		double distanceRatio = newDistanceToCenter / Math.max(currentDistance, MINIMUM_DIVISOR);
//	
//		Vector3 newTopLeft = topLeft.subtract(sourcePoint);
//		newTopLeft.multiplyLocal(distanceRatio);
//		newTopLeft.addLocal(sourcePoint);
//		
//		Vector3 newTopRight = topRight.subtract(sourcePoint);
//		newTopRight.multiplyLocal(distanceRatio);
//		newTopRight.addLocal(sourcePoint);
//	
//		Vector3 newBottomLeft = bottomLeft.subtract(sourcePoint);
//		newBottomLeft.multiplyLocal(distanceRatio);
//		newBottomLeft.addLocal(sourcePoint);
//
//		Vector3 newBottomRight = bottomRight.subtract(sourcePoint);
//		newBottomRight.multiplyLocal(distanceRatio);
//		newBottomRight.addLocal(sourcePoint);
//
//		return new Quadrilateral(newTopLeft, newTopRight, newBottomLeft, newBottomRight);
//	}
}
