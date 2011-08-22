package org.cytoscape.paperwing.internal;

/** A class representing a 3D camera object possessing a direction, up, left,
 * as well as a position vector
 * 
 * @author paperwing (Yue Dong)
 */
public class SimpleCamera {
	
	// A brief note about the camera:
	// -----------------------------
	//
	// A camera has a position vector as well as a target point. The target
	// point is such that the camera always points at it, and it is at
	// a variable distance away from the camera. This target point is very
	// useful for certain rotations and transformations.
	
	/** The minimum allowed distance between the camera's position vector and
	 * the target position vector
	 */
	public static final double MIN_DISTANCE = 2;
	
	/** The maximum allowed distance between the camera's position vector
	 * and the target position vector
	 */
	public static final double MAX_DISTANCE = 40;
	
	/** The direction vector for this camera object */
	private Vector3 direction;
	
	/** The up vector for this camera */
	private Vector3 up;
	
	/** The left vector for this camera */
	private Vector3 left;
	
	/** The position vector for this camera */
	private Vector3 position;
	
	/** The target position vector, which represents a point such that the
	 * camera points exactly through it
	 */
	private Vector3 target;
	
	// /** The minimum distance */
	// private double minDistance = 2;
	// private double maxDistance = 40;
	
	/** The current distance between the camera and its target point */
	private double distance;
	
	/** The speed at which the camera is able to move or translate */
	private double moveSpeed;
	
	/** The speed at which the camera is able to turn without changing position */
	private double turnSpeed;
	
	/** The speed at which the camera is able to orbit around the target point
	 * while keeping its direction vector fixed towards the target point
	 */
	private double orbitSpeed;
	
	/** The speed at which the camera is able to roll, keeping its position and
	 * direction vectors unchanged while doing so
	 */
	private double rollSpeed;
	
	/** The speed at which the camera is able to zoom in, that is, moving its
	 * position vector towards the target point while keeping its direction
	 * vector unchanged
	 */
	private double zoomSpeed;
	
	/** Construct a new SimpleCamera object with default position, target, and up
	 * vectors
	 */
	public SimpleCamera() {
		this(new Vector3(0, 0, 10), new Vector3(0, 0, 0), new Vector3(0, 1, 0));
	}
	
	/** Construct a new SimpleCamera object with specified position,
	 * target, and up vectors, but with default movement/rotation speeds
	 * 
	 * @param position The camera's position vector
	 * @param target The position vector of the target point
	 * @param up The up vector of the camera
	 */
	public SimpleCamera(Vector3 position, Vector3 target, Vector3 up) {
		// TODO: provide constants for default values
		
		this(position, target, up, 0.01, 0.002, 0.002, 0.1, 0.4);
	}

	/** Creates a new SimpleCamera object with specified positions,
	 * directions, and speeds
	 * 
	 * @param position The camera's position vector
	 * @param target The target point's position vector
	 * @param up The camera's up vector
	 * @param moveSpeed The camera's movement speed, related to the move methods
	 * @param turnSpeed The camera's fixed-position turning speed, related to the turn methods
	 * @param orbitSpeed The camera's fixed-target orbit speed, related to the orbit methods
	 * @param rollSpeed The camera's fixed-position roll speed, related to the rolling methods
	 * @param zoomSpeed The camera's fixed-direction zoom speed, related to the zoom methods
	 */
	public SimpleCamera(Vector3 position, Vector3 target, Vector3 up,
			double moveSpeed, double turnSpeed, double orbitSpeed,
			double rollSpeed, double zoomSpeed) {
		this.position = new Vector3(position);
		this.target = new Vector3(target);
		this.up = new Vector3(up);
		this.up.normalizeLocal();
		
		direction = target.subtract(position);
		direction.normalizeLocal();
		left = up.cross(direction);
		left.normalizeLocal();
		
		distance = position.distance(target);
		
		this.moveSpeed = moveSpeed;
		this.turnSpeed = turnSpeed;
		this.orbitSpeed = orbitSpeed;
		this.rollSpeed = rollSpeed;
		this.zoomSpeed = zoomSpeed;
	}

	/** Set the various speeds of the camera
	 * 
	 * @param moveSpeed The camera's movement speed, related to the move methods
	 * @param turnSpeed The camera's fixed-position turning speed, related to the turn methods
	 * @param orbitSpeed The camera's fixed-target orbit speed, related to the orbit methods
	 * @param rollSpeed The camera's fixed-position roll speed, related to the rolling methods
	 * @param zoomSpeed The camera's fixed-direction zoom speed, related to the zoom methods
	 */
	public void setSpeed(double moveSpeed, double turnSpeed, double orbitSpeed,
			double rollSpeed, double zoomSpeed) {
		this.moveSpeed = moveSpeed;
		this.turnSpeed = turnSpeed;
		this.orbitSpeed = orbitSpeed;
		this.rollSpeed = rollSpeed;
		this.zoomSpeed = zoomSpeed;
	}
	
	/** Return the camera's position vector
	 * 
	 * @return The camera's position vector
	 */
	public Vector3 getPosition() {
		return position;
	}
	
	/** Return the camera's target-point position vector
	 * 
	 * @return The target point's position vector
	 */
	public Vector3 getTarget() {
		return target;
	}
	
	/** Return the camera's up vector, as a unit vector
	 * 
	 * @return The camera's up vector, as a unit vector
	 */
	public Vector3 getUp() {
		return up;
	}
	
	/** Return the camera's left vector, as a unit vector
	 * 
	 * @return The camera's left vector, as a unit vector
	 */
	public Vector3 getLeft() {
		return left;
	}
	
	/** Return the camera's direction vector, as a unit vector
	 * 
	 * @return The camera's direction vector, as a unit vector
	 */
	public Vector3 getDirection() {
		return direction;
	}
	
	/** Return the distance between the target point and the camera's
	 * position vector
	 * 
	 * @return The distance between the target point and the camera's
	 * position vector
	 */
	public double getDistance() {
		return distance;
	}
	
	/** Translate the camera leftwards by its movement speed and direction vectors
	 */
	public void moveLeft() {
		move(left, moveSpeed);
	}
	
	/** Translate the camera rightwards by its movement speed and direction vectors
	 */
	public void moveRight() {
		move(left, -moveSpeed);
	}
	
	/** Translate the camera forwards by its movement speed
	 */
	public void moveForward() {
		move(direction, moveSpeed);
	}
	
	/** Translate the camera backwards by its movement speed
	 */
	public void moveBackward() {
		move(direction, -moveSpeed);
	}
	
	/** Translate the camera upwards by its movement speed
	 */
	public void moveUp() {
		move(up, moveSpeed);
	}
	
	/** Translate the camera downwards by its movement speed
	 */
	public void moveDown() {
		move(up, -moveSpeed);
	}
	
	/** Translates the camera to a given position. The target position vector is
	 * also shifted by the same amount as the camera's position vector
	 * 
	 * @param position The position vector representing the new position
	 */
	public void moveTo(Vector3 position) {
		this.position.set(position);
		
		// Recalculate the target position
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(this.position);
		
		target.set(newTarget);
	}
	
	/** Translates the camera to a given point. The target position vector
	 * is shifted by the same amount as the camera's position vector
	 * 
	 * @param x The new position's x-coordinate
	 * @param y The new position's y-coordinate
	 * @param z The new position's z-coordinate
	 */
	public void moveTo(double x, double y, double z) {
		this.position.set(x, y, z);
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(this.position);
		
		target.set(newTarget);
	}
	
	/** Turn the camera leftwards by a multiplier of its turning speed
	 * 
	 * @param multiplier The multiplier, ie. how many times its turning speed to turn
	 */
	public void turnLeft(double multiplier) {
		turnHorizontal(multiplier * turnSpeed);
	}
	
	/** Turn the camera rightwards by a multiplier of its turning speed
	 * 
	 * @param multiplier The multiplier, ie. how many times its turning speed to turn
	 */
	public void turnRight(double multiplier) {
		turnHorizontal(multiplier * -turnSpeed);
	}
	
	/** Turn the camera upwards by a multiplier of its turning speed
	 * 
	 * @param multiplier The multiplier, ie. how many times its turning speed to turn
	 */
	public void turnUp(double multiplier) {
		turnVertical(multiplier * -turnSpeed);
	}
	
	/** Turn the camera downwards by a multiplier of its turning speed
	 * 
	 * @param multiplier The multiplier, ie. how many times its turning speed to turn
	 */
	public void turnDown(double multiplier) {
		turnVertical(multiplier * turnSpeed);
	}
	
	/** Turn the camera leftwards as decided by its turning speed
	 */
	public void turnLeft() {
		turnHorizontal(turnSpeed);
	}
	
	/** Turn the camera rightwards as decided by its turning speed
	 */
	public void turnRight() {
		turnHorizontal(-turnSpeed);
	}
	
	/** Turn the camera upwards as decided by its turning speed
	 */
	public void turnUp() {
		turnVertical(-turnSpeed);
	}
	
	/** Turn the camera downwards as decided by its turning speed
	 */
	public void turnDown() {
		turnVertical(turnSpeed);
	}
	
	/** Orbit the camera about the target point leftwards by a multiplier of
	 * its orbit speed
	 *
	 * @param multiplier The multiplier, ie. how many times its orbit speed
	 * to orbit
	 */
	public void orbitLeft(double multiplier) {
		orbitHorizontal(multiplier * -orbitSpeed);
	}
	
	/** Orbit the camera about the target point rightwards by a multiplier of
	 * its orbit speed
	 *
	 * @param multiplier The multiplier, ie. how many times its orbit speed
	 * to orbit
	 */
	public void orbitRight(double multiplier) {
		orbitHorizontal(multiplier * orbitSpeed);
	}
	
	/** Orbit the camera about the target point upwards by a multiplier of
	 * its orbit speed
	 *
	 * @param multiplier The multiplier, ie. how many times its orbit speed
	 * to orbit
	 */
	public void orbitUp(double multiplier) {
		orbitVertical(multiplier * orbitSpeed);
	}
	
	/** Orbit the camera about the target point downwards by a multiplier of
	 * its orbit speed
	 *
	 * @param multiplier The multiplier, ie. how many times its orbit speed
	 * to orbit
	 */
	public void orbitDown(double multiplier) {
		orbitVertical(multiplier * -orbitSpeed);
	}
	
	/** Orbit the camera about the target point leftwards by its
	 * orbit speed, which is in radians
	 */
	public void orbitLeft() {
		orbitHorizontal(-orbitSpeed);
	}
	
	/** Orbit the camera about the target point rightwards by its
	 * orbit speed, which is in radians
	 */
	public void orbitRight() {
		orbitHorizontal(orbitSpeed);
	}
	
	/** Orbit the camera about the target point upwards by its
	 * orbit speed, which is in radians
	 */
	public void orbitUp() {
		orbitVertical(orbitSpeed);
	}
	
	/** Orbit the camera about the target point downwards by its
	 * orbit speed, which is in radians
	 */
	public void orbitDown() {
		orbitVertical(-orbitSpeed);
	}
	
	/** Roll the camera clockwise by a multiplier of its roll speed
	 * 
	 * @param multiplier The multiplier, ie. how many times its roll speed to
	 * roll
	 */
	public void rollClockwise(double multiplier) {
		roll(multiplier * rollSpeed);
	}
	
	/** Roll the camera counter-clockwise by a multiplier of its roll speed
	 * 
	 * @param multiplier The multiplier, ie. how many times its roll speed to
	 * roll
	 */
	public void rollCounterClockwise(double multiplier) {
		roll(multiplier * -rollSpeed);
	}
	
	/** Roll the camera clockwise by its roll speed
	 */
	public void rollClockwise() {
		roll(rollSpeed);
	}
	
	/** Roll the camera counter-clockwise by its roll speed
	 */
	public void rollCounterClockwise() {
		roll(-rollSpeed);
	}
	
	/** Zoom in, that is, move the camera towards the target point by the
	 * zooming speed
	 */
	public void zoomIn() {
		zoom(zoomSpeed);
	}
	
	/** Zoom out, that is, move the camera away the target point by the
	 * zooming speed
	 */
	public void zoomOut() {
		zoom(-zoomSpeed);
	}
	
	/** Zoom in, that is, move the camera towards the target point by the
	 * zooming speed times a given multiplier
	 * 
	 * @param multiplier How much times the zooming speed to zoom by
	 */
	public void zoomIn(double multiplier) {
		zoom(multiplier * zoomSpeed);
	}
	
	/** Zoom in, that is, move the camera away from the target point by the
	 * zooming speed times a given multiplier
	 * 
	 * @param multiplier How much times the zooming speed to zoom by
	 */
	public void zoomOut(double multiplier) {
		zoom(multiplier * -zoomSpeed);
	}
	
	/** Move the camera by a given amount, towards a given direction
	 * 
	 * @param direction A unit vector representing the direction to
	 * move towards
	 * @param multiplier The multiplier times the direction vector's
	 * magnitude results in the distance travelled (which is equal
	 * to the multiplier if the given direct vector has magnitude
	 * 1)
	 */
	private void move(Vector3 direction, double multiplier) {
		Vector3 offset = new Vector3(direction.multiply(multiplier));
		
		target.addLocal(offset);
		
		Vector3 newPosition = this.direction.multiply(-distance);
		newPosition.addLocal(target);
		
		position.set(newPosition);
	}
	
	/** Turn the camera horizontally by a certain angle, positive for leftwards
	 * 
	 * @param angle The angle to turn, positive for leftwards
	 */
	private void turnHorizontal(double angle) {
		direction = direction.rotate(up, angle);
		direction.normalizeLocal();
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(position);
		target.set(newTarget);
		
		left.set(up.cross(direction));
		left.normalizeLocal();
	}
	
	/** Turn the camera vertically by a certain angle, negative for upwards
	 * 
	 * @param angle The angle to turn, negative for upwards
	 */
	private void turnVertical(double angle) {
		direction = direction.rotate(left, angle);
		direction.normalizeLocal();
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(position);
		target.set(newTarget);
		
		up.set(direction.cross(left));
		up.normalizeLocal();
	}
	
	/** Orbit the camera around the target point horizontally
	 * 
	 * @param angle The angle to orbit, positive for rightwards
	 */
	private void orbitHorizontal(double angle) {
		Vector3 newPosition = direction.multiply(-distance);
		newPosition = newPosition.rotate(up, angle);
		newPosition.addLocal(target);
		
		position.set(newPosition);
		
		direction.set(target.subtract(position));
		direction.normalizeLocal();
		
		left = left.projectNormal(direction);
		left.normalizeLocal();
	}
	
	/** Orbit the camera around the target point vertically, positive angle for upwards
	 * 
	 * @param angle The angle to orbit, positive for upwards
	 */
	private void orbitVertical(double angle) {
		Vector3 newPosition = direction.multiply(-distance);
		newPosition = newPosition.rotate(left, angle);
		newPosition.addLocal(target);
		
		position.set(newPosition);
		
		direction.set(target.subtract(position));
		direction.normalizeLocal();
		
		up = up.projectNormal(direction);
		up.normalizeLocal();
	}
	
	/** Roll the camera about the direction axis, positive for clockwise
	 * 
	 * @param angle The angle to roll, positive for clockwise
	 */
	private void roll(double angle) {
		up = up.rotate(direction, angle);
		up.normalizeLocal();
		
		left = up.cross(direction);
		left.normalizeLocal();
	}
	
	// TODO: This method does not snap distance values to certain nearest values
	/** Zoom in the camera, shifting it towards the target location
	 * 
	 * @param amount The distance to shift by
	 */
	private void zoom(double amount) {
		distance -= amount;
		if (distance > MAX_DISTANCE) {
			distance = MAX_DISTANCE;
		} else if (distance < MIN_DISTANCE) {
			distance = MIN_DISTANCE;
		}
		
		Vector3 newPosition = direction.multiply(-distance);
		newPosition.addLocal(target);
		position.set(newPosition);
	}
}
