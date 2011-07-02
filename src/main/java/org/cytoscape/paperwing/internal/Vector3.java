package org.cytoscape.paperwing.internal;

public class Vector3 {
	private double x;
	private double y;
	private double z;
	
	public Vector3() {
	}
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3(Vector3 other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	public double x() {
		return x;
	}
	
	public double y() {
		return y;
	}
	
	public double z() {
		return z;
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3 other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	public Vector3 add(Vector3 other) {
		return new Vector3(x + other.x, y + other.y, z + other.z);
	}
	
	public void addLocal(Vector3 other) {
		x += other.x;
		y += other.y;
		z += other.z;
	}
	
	public void addLocal(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public Vector3 subtract(Vector3 other) {
		return new Vector3(x - other.x, y - other.y, z - other.z);
	}
	
	public double angle(Vector3 other) {
		// TODO: Find a faster arccos and sqrt method
		
		double lengthSquare = x * x + y * y + z * z;
		double otherLengthSquare = other.x * other.x + other.y * other.y + other.z * other.z;
		
		// TODO: Check if alternative is needed to prevent NaN
		double cosArgument = (x * other.x + y * other.y + z * other.z)/Math.sqrt(lengthSquare * otherLengthSquare);
		
		if (Double.isNaN(cosArgument)) {
			System.out.println("cosArgument NaN");
			System.out.println("lengthSquare: " + lengthSquare);
			System.out.println("otherLengthSquare: " + otherLengthSquare);
			return 0;
		} else if (cosArgument >= 1) {
			return 0;
		} else if (cosArgument <= -1) {
			return Math.PI;
		} else {
			return Math.acos(cosArgument);
		}
	}
	
	public Vector3 cross(Vector3 other) {
		return new Vector3(y * other.z - z * other.y,
				z * other.x - x * other.z, x * other.y - y * other.x);
	}
	
	public Vector3 cross(double x, double y, double z) {
		return new Vector3(this.y * z - this.z * y,
				this.z * x - this.x * z, this.x * y - this.y * x);
	}
	
	public double dot(Vector3 other) {
		return (x * other.x + y * other.y + z * other.z);
	}
	
	public void crossLocal(Vector3 other) {
		set(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
	}
	
	public Vector3 multiply(double scalar) {
		return new Vector3(x * scalar, y * scalar, z * scalar);
	}
	
	public void multiplyLocal(double scalar) {
		x = scalar * x;
		y = scalar * y;
		z = scalar * z;
	}
	
	public void divideLocal(double scalar) {
		x = x / scalar;
		y = y / scalar;
		z = z / scalar;
	}
	
	public Vector3 normalize() {
		double length = Math.sqrt(x * x + y * y + z * z);
		if (length > Double.MIN_NORMAL) {
			return new Vector3(x / length, y / length, z / length);
		} else {
			return new Vector3(0, 0, 0);
		}
	}
	
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public void normalizeLocal() {
		double length = Math.sqrt(x * x + y * y + z * z);
		if (length > Double.MIN_NORMAL) {
			x /= length;
			y /= length;
			z /= length;
		} else {
			x = y = z = 0;
		}
	}
	
	// Obtain distance between position vectors
	public double distance(Vector3 other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
	}
	
	public double distanceSquared(Vector3 other) {
		return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2);
	}
	
	// Project the vector onto the plane passing through the origin, perpendicular to the given normal
	public Vector3 projectNormal(Vector3 normal) {
		return subtract(normal.multiply(this.dot(normal)));
	}
	
	public Vector3 rotate(Vector3 normal, double angle) {
    	// Parametric equation for circle in 3D space:
    	// P = Rcos(t)u + Rsin(t)nxu + c
    	//
    	// Where:
    	//  -u is a unit vector from the centre of the circle to any point
    	// on the circumference
    	//  -R is the radius
    	//  -n is a unit vector perpendicular to the plane
        //  -c is the centre of the circle.
    	
		//TODO: obtain a more efficient sin function
		
    	Vector3 rotated;
    	
    	rotated = normal.normalize();
    	rotated.crossLocal(this);
    	rotated.multiplyLocal(Math.sin(angle));
    	rotated.addLocal(this.multiply(Math.cos(angle)));
    	
    	return rotated;
    }
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
