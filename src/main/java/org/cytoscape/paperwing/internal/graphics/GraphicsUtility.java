package org.cytoscape.paperwing.internal.graphics;

import java.util.Collection;
import java.util.Set;

import javax.media.opengl.GL2;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.Vector3;
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
	public static Vector3 projectScreenCoordinates(int x, int y, int screenWidth, int screenHeight, 
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
	public static Vector3 projectMouseCoordinates(MouseMonitor mouse, GraphicsData graphicsData, 
			double planeDistance) {
		return projectScreenCoordinates(mouse.x(), mouse.y(), graphicsData.getScreenWidth(), graphicsData.getScreenHeight(), planeDistance, graphicsData.getCamera());
		
	}
	
	/**
	 * Obtain the average position of a set of nodes, where each node has the same
	 * weight in the average
	 * 
	 * @param nodes The {@link Collection} of nodes
	 * @return The average position
	 */
	public static Vector3 findAveragePosition(Collection<CyNode> nodes, CyNetworkView networkView) {
		if (nodes.isEmpty()) {
			return null;
		}
		
		double x = 0;
		double y = 0;
		double z = 0;
		
		View<CyNode> nodeView;
		
		for (CyNode node : nodes) {
			// TODO: This relies on an efficient traversal of nodes, as well
			// as efficient retrieval from the networkView object
			nodeView = networkView.getNodeView(node);
			
			if (nodeView != null) {
				x += nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION);
			} else {
				System.out.println("Node with no view found: " + node + 
						", index: " + node.getIndex());
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		// result.divideLocal(DISTANCE_SCALE * nodes.size());
		
		return result;
	}
	
	public static Vector3 findAveragePosition(Set<Integer> nodeIndices, CyNetworkView networkView, double distanceScale) {
		if (nodeIndices.isEmpty()) {
			return null;
		}
		
		double x = 0;
		double y = 0;
		double z = 0;
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
}
