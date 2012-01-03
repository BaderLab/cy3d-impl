package org.cytoscape.paperwing.internal.utility;

import java.util.Collection;
import java.util.Set;

import javax.media.opengl.GL2;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.input.MouseMonitor;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class GraphicsUtility {
	
	/** Set up matrix transformations such that the position is 
	 * equal to the location vector and the z-axis is in the direction 
	 * of the given direction
	 * 
	 * @param gl The {@link GL2} rendering object
	 * @param location The desired position
	 * @param direction The desired direction, does not have to be a 
	 * unit vector
	 * 			
	 */
	public static void setUpFacingTransformation(GL2 gl, Vector3 location, Vector3 direction) {
		gl.glTranslated(location.x(), location.y(), location.z());
		
		Vector3 current = new Vector3(0, 0, 1);
		Vector3 rotateAxis = current.cross(direction);
		
		gl.glRotated(Math.toDegrees(direction.angle(current)), rotateAxis.x(), rotateAxis.y(),
				rotateAxis.z());
	}
	
	
	public static void setNonAlphaColors(GL2 gl, RenderColor color) {
		gl.glColor3d(color.getRed(), color.getGreen(), color.getBlue());
	}
	
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
		
		// Hnear = 2 * tan(fov / 2) * nearDist
		// in our case: 
		//   fov = 45 deg
		//   nearDist = 0.2
		
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
	
	public static Vector3 findCenter(Set<Integer> nodeIndices, CyNetworkView networkView, double distanceScale) {
		if (nodeIndices.isEmpty()) {
			return null;
		}
		
		double x = 0, y = 0, z = 0;
		int visitedCount = 0;
		
		View<CyNode> nodeView;
		
		for (Integer index : nodeIndices) {
			nodeView = networkView.getNodeView(networkView.getModel().getNode(index));
			
			if (nodeView != null) {
				x += nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION);
				visitedCount++;
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		result.divideLocal(distanceScale * visitedCount);
		
		return result;
	}
	
	public static Vector3 findNetworkCenter(CyNetworkView networkView, double distanceScale) {
		
		double x = 0, y = 0, z = 0;
		int visitedCount = 0;
		
		View<CyNode> nodeView;
		
		for (CyNode node : networkView.getModel().getNodeList()) {
			nodeView = networkView.getNodeView(node);
			
			if (nodeView != null) {
				x += nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION);
				visitedCount++;
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		
		if (visitedCount != 0) {
			result.divideLocal(distanceScale * visitedCount);
		}
		
		return result;
	}
	
	public static Vector3 findFarthestNodeFromCenter(CyNetworkView networkView, Vector3 networkCenter, double distanceScale) {
		double currentDistanceSquared;
		double maxDistanceSquared = 0;
		
		Vector3 currentPosition = new Vector3();
		Vector3 maxPosition = new Vector3();
		
		View<CyNode> nodeView;
		
		for (CyNode node : networkView.getModel().getNodeList()) {
			nodeView = networkView.getNodeView(node);
			
			currentPosition.set(nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION),
					nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION),
					nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION));
			currentPosition.divideLocal(distanceScale);
			
			currentDistanceSquared = networkCenter.distanceSquared(currentPosition);
			
			if (currentDistanceSquared > maxDistanceSquared) {
				maxDistanceSquared = currentDistanceSquared;
				maxPosition.set(currentPosition);
			}
		}
		
		return maxPosition;
	}

	public static Vector3 findFarthestNodeFromCenter(CyNetworkView networkView, double distanceScale) {
		return findFarthestNodeFromCenter(networkView, findNetworkCenter(networkView, distanceScale), distanceScale);
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
		double angle = GraphicsUtility.saferArcCos(dotProduct / diagonalLength);
		
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
		
		Vector3 sampleOffset = planeSamplePoint.subtract(lineSamplePoint);
		double hypotenuse = sampleOffset.magnitude();
		
		double lineHypotenuseAngle = lineDirection.angle(sampleOffset);
		double planeHypotenuseAngle = Math.PI / 2 - planeNormal.angle(sampleOffset);
		
		double intersectionCornerAngle = Math.PI - lineHypotenuseAngle - planeHypotenuseAngle;
		
		// Use sine law
		// sin(intersection) / hypotenuse = sin(planeHypotenuseAngle) / lineOffsetMagnitude (desired)
		
		double lineOffsetMagnitude = Math.sin(planeHypotenuseAngle) * hypotenuse / Math.sin(intersectionCornerAngle);
		
		Vector3 lineOffset = lineDirection.normalize();
		lineOffset.multiplyLocal(lineOffsetMagnitude);
		
		Vector3 result = lineSamplePoint.plus(lineOffset);
		
		return result;
	}
}
