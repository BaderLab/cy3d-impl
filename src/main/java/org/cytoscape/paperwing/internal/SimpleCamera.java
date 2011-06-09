package org.cytoscape.paperwing.internal;

public class SimpleCamera {
	private Vector3 direction;
	private Vector3 up;
	private Vector3 left;
	private Vector3 position;
	private Vector3 target;
	
	private double minDistance = 2;
	private double maxDistance = 40;
	
	private double distance;
	private double moveSpeed;
	private double turnSpeed;
	private double orbitSpeed;
	private double rollSpeed;
	private double zoomSpeed;
	
	public SimpleCamera() {
		this(new Vector3(0, 0, 10), new Vector3(0, 0, 0), new Vector3(0, 1, 0));
	}
	
	// TODO: provide constants for default values
	public SimpleCamera(Vector3 position, Vector3 target, Vector3 up) {
		this(position, target, up, 0.01, 0.002, 0.002, 0.1, 0.4);
	}

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

	public void setSpeed(double moveSpeed, double turnSpeed, double orbitSpeed,
			double rollSpeed, double zoomSpeed) {
		this.moveSpeed = moveSpeed;
		this.turnSpeed = turnSpeed;
		this.orbitSpeed = orbitSpeed;
		this.rollSpeed = rollSpeed;
		this.zoomSpeed = zoomSpeed;
	}
	
	public Vector3 getPosition() {
		return position;
	}
	
	public Vector3 getTarget() {
		return target;
	}
	
	public Vector3 getUp() {
		return up;
	}
	
	public Vector3 getLeft() {
		return left;
	}
	
	public Vector3 getDirection() {
		return direction;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void moveLeft() {
		move(left, moveSpeed);
	}
	
	public void moveRight() {
		move(left, -moveSpeed);
	}
	
	public void moveForward() {
		move(direction, moveSpeed);
	}
	
	public void moveBackward() {
		move(direction, -moveSpeed);
	}
	
	public void moveUp() {
		move(up, moveSpeed);
	}
	
	public void moveDown() {
		move(up, -moveSpeed);
	}
	
	public void moveTo(Vector3 position) {
		this.position.set(position);
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(this.position);
		
		target.set(newTarget);
	}
	
	public void moveTo(double x, double y, double z) {
		this.position.set(x, y, z);
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(this.position);
		
		target.set(newTarget);
	}
	
	public void turnLeft(double multiplier) {
		turnHorizontal(multiplier * turnSpeed);
	}
	
	public void turnRight(double multiplier) {
		turnHorizontal(multiplier * -turnSpeed);
	}
	
	public void turnUp(double multiplier) {
		turnVertical(multiplier * -turnSpeed);
	}
	
	public void turnDown(double multiplier) {
		turnVertical(multiplier * turnSpeed);
	}
	
	public void turnLeft() {
		turnHorizontal(turnSpeed);
	}
	
	public void turnRight() {
		turnHorizontal(-turnSpeed);
	}
	
	public void turnUp() {
		turnVertical(-turnSpeed);
	}
	
	public void turnDown() {
		turnVertical(turnSpeed);
	}
	
	public void orbitLeft(double multiplier) {
		orbitHorizontal(multiplier * -orbitSpeed);
	}
	
	public void orbitRight(double multiplier) {
		orbitHorizontal(multiplier * orbitSpeed);
	}
	
	public void orbitUp(double multiplier) {
		orbitVertical(multiplier * orbitSpeed);
	}
	
	public void orbitDown(double multiplier) {
		orbitVertical(multiplier * -orbitSpeed);
	}
	
	public void orbitLeft() {
		orbitHorizontal(-orbitSpeed);
	}
	
	public void orbitRight() {
		orbitHorizontal(orbitSpeed);
	}
	
	public void orbitUp() {
		orbitVertical(orbitSpeed);
	}
	
	public void orbitDown() {
		orbitVertical(-orbitSpeed);
	}
	
	public void rollClockwise(double multiplier) {
		roll(multiplier * rollSpeed);
	}
	
	public void rollCounterClockwise(double multiplier) {
		roll(multiplier * -rollSpeed);
	}
	
	public void rollClockwise() {
		roll(rollSpeed);
	}
	
	public void rollCounterClockwise() {
		roll(-rollSpeed);
	}
	
	public void zoomIn() {
		zoom(zoomSpeed);
	}
	
	public void zoomOut() {
		zoom(-zoomSpeed);
	}
	
	public void zoomIn(double multiplier) {
		zoom(multiplier * zoomSpeed);
	}
	
	public void zoomOut(double multiplier) {
		zoom(multiplier * -zoomSpeed);
	}
	
	private void move(Vector3 direction, double multiplier) {
		Vector3 offset = new Vector3(direction.multiply(multiplier));
		
		target.addLocal(offset);
		
		Vector3 newPosition = this.direction.multiply(-distance);
		newPosition.addLocal(target);
		
		position.set(newPosition);
	}
	
	private void turnHorizontal(double angle) {
		direction = direction.rotate(up, angle);
		direction.normalizeLocal();
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(position);
		target.set(newTarget);
		
		left = left.projectNormal(direction);
		left.normalizeLocal();
		
		// TODO: Check if this line is needed to maintain up, direction, and left are perpendicular
		// up.set(direction.cross(left));
	}
	
	private void turnVertical(double angle) {
		direction = direction.rotate(left, angle);
		direction.normalizeLocal();
		
		Vector3 newTarget = direction.multiply(distance);
		newTarget.addLocal(position);
		target.set(newTarget);
		
		up = up.projectNormal(direction);
		up.normalizeLocal();
		
		// TODO: Check if this line is needed to maintain up, direction, and left are perpendicular
		// left.set(up.cross(direction));
	}
	
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
	
	private void roll(double angle) {
		up = up.rotate(direction, angle);
		up.normalizeLocal();
		
		left = up.cross(direction);
		left.normalizeLocal();
	}
	
	// TODO: This method does not snap distance values to certain nearest values
	private void zoom(double amount) {
		distance -= amount;
		if (distance > maxDistance) {
			distance = maxDistance;
		} else if (distance < minDistance) {
			distance = minDistance;
		}
		
		Vector3 newPosition = direction.multiply(-distance);
		newPosition.addLocal(target);
		position.set(newPosition);
	}
}
