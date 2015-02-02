package org.baderlab.cy3d.internal.camera;

import org.baderlab.cy3d.internal.geometric.Vector3;

/**
 * A camera that always looks directly at the origin.
 * Also, the camera only allows you to go a certain distance to the origin.
 * 
 * @author mkucera
 */
public class OriginOrbitCamera implements Camera {

	public static final double DEFAULT_ORBIT_SPEED = 0.01;
	public static final double DEFAULT_ZOOM_SPEED = 0.4;
	
	// target is always (0,0,0)
	private Vector3 target;
	private Vector3 position;
	private Vector3 up;
	
	private double nearLimit = 1.0;
	private double farLimit  = 100.0;
	
	public OriginOrbitCamera() {
		reset();
	}
	
	@Override
	public void reset() {
		target   = new Vector3(0, 0, 0);
		position = new Vector3(0, 0, 3);
		up       = new Vector3(0, 1, 0);
	}
	
	@Override
	public void moveTo(Vector3 position) {
		this.position = position;
	}
	
	@Override
	public Vector3 getPosition() {
		return position;
	}

	@Override
	public Vector3 getTarget() {
		return target;
	}

	@Override
	public Vector3 getUp() {
		return up;
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
	
	@Override
	public Vector3 getLeft() {
		Vector3 left = up.cross(getDirection());
		left.normalizeLocal();
		return left;
	}
	
	@Override
	public void orbitUp(double angle) {
		angle *= DEFAULT_ORBIT_SPEED;
		
		Vector3 direction = getDirection();
		double distance = getDistance();
		
		Vector3 left = up.cross(direction);
		left.normalizeLocal();
		
		Vector3 newPosition = direction.multiply(-distance);
		newPosition = newPosition.rotate(left, angle);
		newPosition.addLocal(target);
		
		position.set(newPosition);
		
		up = up.projectNormal(direction);
		up.normalizeLocal();
	}
	
	@Override
	public void orbitRight(double angle) {
		angle *= DEFAULT_ORBIT_SPEED;
		
		Vector3 direction = getDirection();
		double distance = getDistance();
		
		Vector3 newPosition = direction.multiply(-distance);
		newPosition = newPosition.rotate(up, angle);
		newPosition.addLocal(target);
		
		position.set(newPosition);
	}
	
	@Override
	public void moveForward(double amount) {
		double distance = getDistance();
		
		double newDistance = Math.min(farLimit, Math.max(nearLimit, distance - amount));
		
		// Similar triangles
		double ratio = newDistance / distance;
		double x = position.x() * ratio;
		double y = position.y() * ratio;
		double z = position.z() * ratio;
		
		position.set(x, y, z);
	}
}
