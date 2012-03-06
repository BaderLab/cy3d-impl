package org.cytoscape.paperwing.internal.tools;

import java.util.Collection;


import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.input.MouseMonitor;

public class GeometryToolkit {
	
	/**
	 * Converts 2D screen coordinates to 3D OpenGL coordinates, where the
	 * coordinate for the 3rd dimension is specified by the distance between 
	 * the camera and the plane which intersects a line passing through the eye and 
	 * the specified location on the plane
	 * 
	 * This method can be used for mouse coordinates, as mouse coordinates are
	 * screen coordinates.
	 * 
	 * @param x The x window coordinate of the mouse (0 for top left)
	 * @param y The y window coordinate of the mouse (0 for top left)
	 * @param planeDistance The distance between the camera and the 
	 * intersecting plane
	 * @return The 3D position of the mouse
	 */
	public static Vector3 convertScreenTo3d(int x, int y, int screenWidth, int screenHeight, 
			double planeDistance, SimpleCamera camera) {
		
		// Project mouse coordinates into 3d space for mouse interactions
		// --------------------------------------------------------------
		
		// Hnear = 2 * tan(fovy / 2) * nearDist
		// in our case: 
		//   fov = 45 deg
		//   nearDist = 0.2
		
		// TODO: Allow a variable field of view, unlike here, to be specified as a parameter
		double fieldOfView = Math.PI / 4;
		double nearDistance = 0.2;
		
		double nearPlaneHeight = 2 * Math.tan(fieldOfView / 2) * nearDistance;
		double nearPlaneWidth = nearPlaneHeight * screenWidth / screenHeight;
		
		double percentMouseOffsetX = (double) (x - screenWidth) / screenWidth + 0.5;
		double percentMouseOffsetY = (double) (y - screenHeight) / screenHeight + 0.5;
		
		// OpenGL has up as the positive y direction, whereas the mouse/screen coordinate is (0, 0) at the top left
		percentMouseOffsetY = -percentMouseOffsetY;
		
		double nearX = percentMouseOffsetX * nearPlaneWidth;
		double nearY = percentMouseOffsetY * nearPlaneHeight;
		
		// Obtain the near plane position vector
		Vector3 nearPosition;
		nearPosition = new Vector3(camera.getDirection());
		nearPosition.multiplyLocal(nearDistance);
		
		nearPosition.addLocal(camera.getPosition());
		nearPosition.addLocal(camera.getUp().multiply(nearY));
		// Note that nearX is positive to the right
		nearPosition.addLocal(camera.getLeft().multiply(-nearX)); 
		
		// Obtain the projection direction vector
		Vector3 projectionDirection = nearPosition.subtract(camera.getPosition());
		projectionDirection.normalizeLocal();
		
		double angle = projectionDirection.angle(camera.getDirection());
		double projectionDistance = (planeDistance) / Math.cos(angle);
		
		Vector3 projection = projectionDirection.multiply(projectionDistance);
		// projection.addLocal(camera.getPosition());
		// projection.addLocal(camera.getPosition().subtract(eye));
		projection.addLocal(camera.getPosition());
		
		return projection;
	}
	
	// public static Vector3 projectScreenCoordinates
	
	// Projects mouse into 3d coordinates. Intersection between eye-cursor line and a given plane,
	// which is perpendicular to the camera.
	public static Vector3 convertMouseTo3d(MouseMonitor mouse, GraphicsData graphicsData, 
			double planeDistance) {
		return convertScreenTo3d(mouse.x(), mouse.y(), graphicsData.getScreenWidth(), graphicsData.getScreenHeight(), planeDistance, graphicsData.getCamera());
		
	}
	
	// Needs camera direction vector, camera up vector to be unit
	// This method also uses the Math.tan method, so there might be room for finding a faster tangent method
	public static Quadrilateral generateViewingBounds(Vector3 cameraPosition, Vector3 cameraDirection, Vector3 cameraUp, 
			double planeDistance, double verticalFov, double aspectRatio) {
		// These x, y, z offsets are oriented relative to the camera's direction vector
		Vector3 zOffset = cameraDirection.copy();
		zOffset.multiplyLocal(planeDistance);
		
		Vector3 yOffset = cameraUp.copy();
		yOffset.multiplyLocal(planeDistance * Math.tan(verticalFov / 360.0 * Math.PI));
		
		Vector3 xOffset = cameraDirection.copy(); // xOffset will be the camera's right vector
		xOffset.crossLocal(cameraUp);
		xOffset.multiplyLocal(yOffset.magnitude() * aspectRatio); // aspect = width / length
		
		Vector3 centerPoint = cameraPosition.copy().plus(zOffset);
		
		Vector3 topLeft = centerPoint.plus(yOffset).subtract(xOffset);
		Vector3 topRight = centerPoint.plus(yOffset).plus(xOffset);
		Vector3 bottomLeft = centerPoint.subtract(yOffset).subtract(xOffset);
		Vector3 bottomRight = centerPoint.subtract(yOffset).plus(xOffset);
		
		return new Quadrilateral(topLeft, topRight, bottomLeft, bottomRight);
	}
	
	
	public static Vector3 generateCameraPosition(Quadrilateral bounds, Vector3 cameraDirection, double planeDistance) {
		Vector3 offset = cameraDirection.normalize();
		offset.multiplyLocal(planeDistance);
		
		return bounds.getCenterPoint().subtract(offset);
	}
	
	// Just like Math.acos, but a bit safer
	public static double saferArcCos(double argument) {
		if (argument >= 1) {
			return 0;
		} else if (argument <= -1) {
			return Math.PI;
		} else {
			return Math.acos(argument);
		}
	}
	
	
	// This method solves the case where newAnchor and oldPosition are supposed to be aligned by the normal vector,
	// but newAnchor has moved and we need to update oldPosition accordingly.
	public static Vector3 findNewOrthogonalAnchoredPosition(Vector3 newAnchor, Vector3 oldPosition, Vector3 normal) {

		Vector3 diagonalDisplacement = newAnchor.subtract(oldPosition);
		double diagonalLength = diagonalDisplacement.magnitude();
		double dotProduct = diagonalDisplacement.dot(normal);
		
		// Use the dot product formula to find angle between diagonal displacement and camera's direction vector
		double angle = GeometryToolkit.saferArcCos(dotProduct / diagonalLength);
		
		double orthogonalDisplacementLength = Math.cos(angle) * diagonalLength;
		
		Vector3 orthogonalDisplacement = normal.normalize().multiply(-orthogonalDisplacementLength);
		
		return newAnchor.plus(orthogonalDisplacement);
	}
	
	public static Vector3 findMidpoint(Vector3 first, Vector3 second) {
		Vector3 result = first.plus(second);
		result.divideLocal(2);
		
		return result;
	}


	// Assumes lineDirection points from lineSamplePoint into the plane
	public static Vector3 findLinePlaneIntersection(Vector3 lineSamplePoint, Vector3 lineDirection, Vector3 planeSamplePoint, Vector3 planeNormal) {
		
		Vector3 sampleDisplacement = planeSamplePoint.subtract(lineSamplePoint);
		double hypotenuse = sampleDisplacement.magnitude();
		
		double lineHypotenuseAngle = lineDirection.angle(sampleDisplacement);
		double planeHypotenuseAngle = Math.PI / 2 - planeNormal.angle(sampleDisplacement);
		
		double intersectionCornerAngle = Math.PI - lineHypotenuseAngle - planeHypotenuseAngle;
		
		// Use sine law
		// sin(intersection) / hypotenuse = sin(planeHypotenuseAngle) / lineOffsetMagnitude (desired)
		
		double lineOffsetMultiplier = Math.sin(planeHypotenuseAngle) * hypotenuse / Math.sin(intersectionCornerAngle);
		
		Vector3 lineOffset = lineDirection.normalize();
		lineOffset.multiplyLocal(lineOffsetMultiplier);

		// TODO: Correct for when lineDirection points away from the plane?
		
		Vector3 result = lineSamplePoint.plus(lineOffset);
		
		return result;
	}
	
	// Distance from nearPoint to farPoint along the normal
	public static double findOrthogonalDistance(Vector3 nearPoint, Vector3 farPoint, Vector3 normal) {
		
		Vector3 displacement = farPoint.subtract(nearPoint);
		double hypotenuse = displacement.magnitude();

		double angle = displacement.angle(normal);
		
		return Math.abs(Math.cos(angle) * hypotenuse);
	}
	
	
	/**
	 * Given the fixed vertical field of view and screen dimensions, calculate the horizontal field of view assuming that 
	 * it varies according to the current aspect ratio. If the screen dimensions are equal, the returned horizontal field of view
	 * would be equal to the vertical field of view.
	 * 
	 * @param verticalFieldOfView The vertical field of view, in degrees
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 * @return The calculated horizontal field of view, in degrees
	 */
	public static double findHorizontalFieldOfView(double verticalFieldOfView, int screenWidth, int screenHeight) {
		double screenDistance = (screenHeight / 2) / Math.tan(verticalFieldOfView / 2 * Math.PI / 180);
		
		double horizontalFieldOfView = Math.atan(screenWidth / 2 / screenDistance);
		
		return (horizontalFieldOfView * 180 / Math.PI);
	}
	
	/**
	 * Assuming the initial direction is towards the negative z direction, calculate what
	 * the direction would be given yaw and pitch angles. The rotations are done by the right-hand rule.
	 * 
	 * This method assumes the Z-X-Y convention, that is, the first rotation is done with respect
	 * to the Z-axis and the last rotation is done with respect to the Y-axis.
	 * 
	 * @param yaw The yaw angle, in degrees
	 * @param pitch The pitch angle, in degrees
	 * @param roll The roll angle, in degrees
	 * @return The unit direction vector corresponding to the given yaw and pitch angles.
	 */
	public static Vector3 findDirectionVector(double yaw, double pitch, double roll) {
		
		Vector3 newAxisX = (new Vector3(1, 0, 0).rotate(new Vector3(0, 0, 1), Math.toRadians(roll)));
		Vector3 newAxisY = (new Vector3(0, 1, 0).rotate(new Vector3(0, 0, 1), Math.toRadians(roll)));
		
		newAxisY = newAxisY.rotate(newAxisX, Math.toRadians(pitch));
		newAxisX = newAxisX.rotate(newAxisY, Math.toRadians(yaw));
		
		Vector3 newAxisZ = newAxisX.cross(newAxisY);
		
		return newAxisZ.invert().normalize();
	}
	
	// Older method; rotates about axes in order Y-X-Z
	public static Vector3 findDirectionVectorOld(double yaw, double pitch) {
		Vector3 direction = new Vector3(0, 0, -1);
		
		// Rotate about y-axis for yaw
		direction = direction.rotate(new Vector3(0, 1, 0), yaw);
		
		Vector3 newAxisX = (new Vector3(1, 0, 0)).rotate(new Vector3(0, 1, 0), yaw);
		
		// Rotate about its new x-axis for pitch
		direction = direction.rotate(newAxisX, pitch);
		
		return direction.normalize();
	}
	
	/**
	 * Assuming the initial up vector is towards the positive y direction, calculate what the
	 * up vector would be given the yaw, pitch, and roll angles. The rotations are done by
	 * the right-hand rule.
	 * 
	 * This method assumes the Z-Y-X convention, that is, the first rotation is done with respect
	 * to the Z-axis and the last rotation is done with respect to the X-axis.
	 * 
	 * @param yaw The yaw angle
	 * @param pitch The pitch angle
	 * @param roll The roll angle
	 * @return The unit up vector corresponding to the given roll angle.
	 */
	public static Vector3 findUpVector(double yaw, double pitch, double roll) {

		// Determine the rotated axes
		// Vector3 newAxisY = (new Vector3(0, 1, 0)).rotate(new Vector3(0, 0, 1), roll);
		// Vector3 newAxisX = (new Vector3(0, 1, 0)).rotate(new Vector3(0, 0, 1), roll);
		
		// rotate by z, change x and y
		// rotate by new x, y is in final position
		// rotate by new y, x is in final position
		// obtain z in final position (cross product)
		
		Vector3 newAxisX = (new Vector3(1, 0, 0).rotate(new Vector3(0, 0, 1), Math.toRadians(roll)));
		Vector3 newAxisY = (new Vector3(0, 1, 0).rotate(new Vector3(0, 0, 1), Math.toRadians(roll)));
		
		newAxisY = newAxisY.rotate(newAxisX, Math.toRadians(pitch));
		newAxisX = newAxisX.rotate(newAxisY, Math.toRadians(yaw));
		
		Vector3 newAxisZ = newAxisX.cross(newAxisY);
		
		return newAxisY.normalize();
	}
	
	// Older method; rotates about axes in order Y-X-Z
	public static Vector3 findUpVectorOld(double yaw, double pitch, double roll) {

		// Determine the rotated axes
		Vector3 newAxisX = (new Vector3(1, 0, 0)).rotate(new Vector3(0, 1, 0), yaw);
		Vector3 newAxisY = (new Vector3(0, 1, 0)).rotate(newAxisX, pitch);
		Vector3 newAxisZ = newAxisX.cross(newAxisY);
		
		Vector3 up = newAxisY.rotate(newAxisZ, roll);
		
		return up.normalize();
	}
	
	/**
	 * Given the direction and up vectors, calculate the yaw, pitch, and roll angles assuming
	 * the initial direction is towards the negative z direction and the initial up vector
	 * is towards the positive y direction.
	 * 
	 * This method assumes the Z-Y-X convention, that is, the first rotation is done with respect
	 * to the Z-axis and the last rotation is done with respect to the X-axis.
	 * 
	 * @param direction The direction vector
	 * @param up The up vector
	 * @return A 3-vector in the form of (yaw, pitch, roll) in degrees
	 */
	public static Vector3 findYawPitchRoll(Vector3 direction, Vector3 up) {
		
		// Obtain the rotated axes
		Vector3 newAxisZ = direction.multiply(-1);
		Vector3 newAxisY = up.copy();
		Vector3 newAxisX = newAxisY.cross(newAxisZ);
		
		// Calculate the vector representing the line of nodes for the Tait-Bryan convention
		Vector3 lineOfNodes = (new Vector3(0, 0, 1)).cross(newAxisX);
		
		// This vector is perpendicular to the line of nodes
		Vector3 lineOfNodesPerpendicular = lineOfNodes.rotate(new Vector3(0, 0, 1), -Math.PI / 2);
		
		double yaw, pitch, roll;
		
		yaw = newAxisX.angle(lineOfNodesPerpendicular);
		pitch = newAxisY.angle(lineOfNodes);		
		roll = (new Vector3(0, 1, 0)).angle(lineOfNodes);
		
		Vector3 result = new Vector3(yaw, pitch, roll);
		result.multiplyLocal(180.0 / Math.PI);
		return result;
	}
}
