package org.baderlab.cy3d.internal.camera;

import javax.media.opengl.glu.GLU;

import org.baderlab.cy3d.internal.geometric.Vector3;

/**
 * A simple class that just holds the position of a camera.
 * 
 * @author mkucera
 * 
 * @see GLU#gluLookAt(double, double, double, double, double, double, double, double, double)
 */
public class CameraPositionHolder implements CameraPosition {

	/**
	 * The location of the camera.
	 * Corresponds to the OpenGL 'eye' location.
	 */
	protected Vector3 position;
	
	/**
	 * The camera's up vector.
	 */
	protected Vector3 up;
	
	/**
	 * The target position vector, which represents a point such that the
	 * camera points exactly through it.
	 * Corresponds to the OpenGL 'center' location.
	 */
	protected Vector3 target;
	
	
	public CameraPositionHolder() {
		this.position = new Vector3(0, 0, 3);
		this.target = new Vector3(0, 0, 0);
		this.up = new Vector3(0, 1, 0);
	}
	
	public CameraPositionHolder(Vector3 position, Vector3 target, Vector3 up) {
		this.position = new Vector3(position);
		this.target = new Vector3(target);
		this.up = new Vector3(up);
	}
	
	@Override
	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position.set(position);
	}
	
	@Override
	public Vector3 getTarget() {
		return target;
	}

	public void setTarget(Vector3 target) {
		this.target.set(target);
	}
	
	@Override
	public Vector3 getUp() {
		return up;
	}
	
	public void setUp(Vector3 up) {
		this.up.set(up);
	}
	
	@Override
	public Vector3 getDirection() {
		Vector3 direction = target.subtract(position);
		direction.normalizeLocal();
		return direction;
	}
	
	@Override
	public double getDistance() {
		return position.distance(target);
	}
	
	/** 
	 * Return the camera's left vector, as a unit vector
	 */
	public Vector3 getLeft() {
		Vector3 direction = getDirection();
		Vector3 left = up.cross(direction);
		left.normalizeLocal();
		return left;
	}

	@Override
	public void set(CameraPosition other) {
		setPosition(other.getPosition());
		setTarget(other.getTarget());
		setUp(other.getUp());
	}

}
