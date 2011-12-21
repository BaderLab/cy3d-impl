package org.cytoscape.paperwing.internal.graphics;

import org.cytoscape.paperwing.internal.Vector3;

public class Quadrilateral {
	private Vector3 topLeft;
	private Vector3 topRight;
	private Vector3 bottomLeft;
	private Vector3 bottomRight;
	
	public Quadrilateral(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight) {
		this.topLeft = topLeft.copy();
		this.topRight = topRight.copy();
		this.bottomLeft = bottomLeft.copy();
		this.bottomRight = bottomRight.copy();
	}
	
	public Quadrilateral() {
		this(new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0));
	}
	
	public Vector3 getTopLeft() {
		return topLeft.copy();
	}
	public void setTopLeft(double x, double y, double z) {
		topLeft.set(x, y, z);
	}
	public Vector3 getTopRight() {
		return topRight.copy();
	}
	public void setTopRight(double x, double y, double z) {
		topRight.set(x, y, z);
	}
	public Vector3 getBottomLeft() {
		return bottomLeft.copy();
	}
	public void setBottomLeft(double x, double y, double z) {
		bottomLeft.set(x, y, z);
	}
	public Vector3 getBottomRight() {
		return bottomRight.copy();
	}
	public void setBottomRight(double x, double y, double z) {
		bottomRight.set(x, y, z);
	}
	
	public void set(Quadrilateral other) {
		topLeft.set(other.topLeft);
		topRight.set(other.topRight);
		bottomLeft.set(other.bottomLeft);
		bottomRight.set(other.bottomRight);
	}

	public Quadrilateral copy() {
		return new Quadrilateral(topLeft, topRight, bottomLeft, bottomRight);
	}
	
	// Return average point
	public Vector3 getCenterPoint() {
		Vector3 center = new Vector3();
		
		center.addLocal(topLeft);
		center.addLocal(topRight);
		center.addLocal(bottomLeft);
		center.addLocal(bottomRight);
		center.divideLocal(4.0);
		
		return center;
	}
}
