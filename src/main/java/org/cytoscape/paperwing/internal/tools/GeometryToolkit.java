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
}
