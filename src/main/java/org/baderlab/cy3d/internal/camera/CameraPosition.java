package org.baderlab.cy3d.internal.camera;

import org.baderlab.cy3d.internal.geometric.Vector3;

public interface CameraPosition {

	Vector3 getPosition();
	
	Vector3 getTarget();
	
	Vector3 getUp();
	
	/**
	 * Equivalent to target.subtract(position).normalize();
	 * @return
	 */
	Vector3 getDirection();
	
	/**
	 * Returns the distance between the camera position and
	 * the target location.
	 * @return
	 */
	double getDistance();
	
	void set(CameraPosition other);
	
}
