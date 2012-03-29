package org.cytoscape.paperwing.internal.tools;

import java.util.Collection;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class NetworkToolkit {

	private static final String SELECTED_COLUMN_NAME = "selected";
	
	/**
	 * Find the average position of a given set of nodes.
	 * 
	 * @param nodeIndices The indices of nodes whose average position is to be found
	 * @param networkView The network view containing the nodes
	 * @param distanceScale The distance scaling used to convert between Cytoscape and renderer coordinates.
	 * @return The average position
	 */
	public static Vector3 findCenter(Set<Integer> nodeIndices, CyNetworkView networkView, double distanceScale) {		
		double x = 0, y = 0, z = 0;
		int visitedCount = 0;
		
		View<CyNode> nodeView;
		
		for (Integer index : nodeIndices) {
			nodeView = networkView.getNodeView(networkView.getModel().getNode(index));

			if (nodeView != null) {
				x += nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION);
				visitedCount++;
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		
		if (visitedCount != 0) { 
			result.divideLocal(distanceScale * visitedCount);
		}
		
		return result;
	}
	
	/**
	 * Position the given camera as to fit all of the given nodes in the current view.
	 * 
	 * @param camera The camera to position
	 * @param nodeViews The node views to be fit into the current view
	 * @param distanceScale The distance scaling used to convert between Cytoscape and render coordinates. Cytoscape coordinates
	 * are divided by this scale to obtain renderer coordinates.
	 * @param distanceFactor The multiplier against the distance from the center of the nodes to the farthest node used to place the camera
	 * @param minDistance The minimum distance between the camera and the average node position.
	 */
	public static void fitInView(SimpleCamera camera, Collection<View<CyNode>> nodeViews, 
			double distanceScale, double distanceFactor, double minDistance) {
		Vector3 center = NetworkToolkit.findCenter(nodeViews, distanceScale);
		Vector3 farthestNode = NetworkToolkit.findFarthestNodeFromCenter(nodeViews, center, distanceScale);
		
		double newDistance = farthestNode.distance(center);
		
		// Further increase the distance needed
		newDistance *= distanceFactor;
		
		// Enforce minimum distance
		newDistance = Math.max(newDistance, minDistance);
		
		Vector3 offset = camera.getDirection().multiply(-newDistance);
		
		camera.moveTo(center.plus(offset));
		camera.setDistance(newDistance);
	}
	
	/**
	 * Obtain the average position of a set of nodes.
	 * 
	 * @param nodeViews The node views whose average position is to be obtained
	 * @param distanceScale The distance scaling used to convert between Cytoscape and render coordinates. Cytoscape coordinates
	 * are divided by this scale to obtain renderer coordinates.
	 * @return The average position
	 */
	public static Vector3 findCenter(Collection<View<CyNode>> nodeViews, double distanceScale) {
		double x = 0, y = 0, z = 0;
		int visitedCount = 0;
		
		for (View<CyNode> nodeView : nodeViews) {
			
			if (nodeView != null) {
				x += nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION);
				visitedCount++;
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		if (visitedCount != 0) { 
			result.divideLocal(distanceScale * visitedCount);
		}
		
		return result;
	}

	/**
	 * Obtain the average position of nodes in a network.
	 *
	 * @param networkView The network to find the average position of
	 * @param distanceScale The distance scaling used to convert between Cytoscape and render coordinates. Cytoscape coordinates
	 * are divided by this scale to obtain renderer coordinates.
	 * @return The average position
	 */
	public static Vector3 findNetworkCenter(CyNetworkView networkView, double distanceScale) {		
		double x = 0, y = 0, z = 0;
		int visitedCount = 0;
		
		View<CyNode> nodeView;
		
		for (CyNode node : networkView.getModel().getNodeList()) {
			nodeView = networkView.getNodeView(node);
			
			if (nodeView != null) {
				x += nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				y += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				z += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION);
				visitedCount++;
			}
		}
		
		Vector3 result = new Vector3(x, y, z);
		
		if (visitedCount != 0) {
			result.divideLocal(distanceScale * visitedCount);
		}
		
		return result;
	}

	/**
	 * Find the position of the node that is farthest from a given position.
	 * 
	 * @param nodeViews The node views used to find the farthest node
	 * @param networkCenter The position used as the center, to determine the farthest node
	 * @param distanceScale The distance scaling used to convert between Cytoscape and render coordinates. Cytoscape coordinates
	 * are divided by this scale to obtain renderer coordinates.
	 * @return The position of the farthest node
	 */
	public static Vector3 findFarthestNodeFromCenter(Collection<View<CyNode>> nodeViews, Vector3 networkCenter, double distanceScale) {
		double currentDistanceSquared;
		double maxDistanceSquared = -1;
		
		Vector3 currentPosition = new Vector3();
		Vector3 maxPosition = new Vector3();
		
		for (View<CyNode> nodeView : nodeViews) {
			
			if (nodeView != null) {
			
				currentPosition.set(nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION),
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION),
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION));
				currentPosition.divideLocal(distanceScale);
				
				currentDistanceSquared = networkCenter.distanceSquared(currentPosition);
				
				if (currentDistanceSquared > maxDistanceSquared) {
					maxDistanceSquared = currentDistanceSquared;
					maxPosition.set(currentPosition);
				}
			}
		}
		
		return maxPosition;
	}
	
	public static Vector3 findFarthestNodeFromCenter(CyNetworkView networkView, Vector3 networkCenter, double distanceScale) {
		return findFarthestNodeFromCenter(networkView.getNodeViews(), networkCenter, distanceScale);
	}

	public static Vector3 findFarthestNodeFromCenter(CyNetworkView networkView, double distanceScale) {
		return findFarthestNodeFromCenter(networkView, findNetworkCenter(networkView, distanceScale), distanceScale);
	}

	/**
	 * Moves all of a given set of nodes by an amount specified by a displacement vector.
	 * 
	 * @param nodeIndices The indices of nodes to move
	 * @param networkView The network view containing the nodes
	 * @param distanceScale The distance scaling used to convert between Cytoscape and renderer coordinates
	 * @param displacement The displacement vector
	 */
	public static void displaceNodes(Set<Integer> nodeIndices, CyNetworkView networkView, double distanceScale, Vector3 displacement) {
		View<CyNode> nodeView;
		
		for (Integer index : nodeIndices) {
			nodeView = networkView.getNodeView(networkView.getModel().getNode(index));
			
			if (nodeView != null) {
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, 
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)
								+ displacement.x() * distanceScale);
				
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, 
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)
								+ displacement.y() * distanceScale);
				
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, 
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION)
								+ displacement.z() * distanceScale);
			}
		}
	}
	
	/**
	 * Moves all of the given nodes by the given displacement vector.
	 * 
	 * @param nodeViews The node view objects to move
	 * @param distanceScale The distance scaling used to convert between Cytoscape and renderer coordinates
	 * @param displacement The displacement vector
	 */
	public static void displaceNodes(Collection<View<CyNode>> nodeViews, double distanceScale, Vector3 displacement) {
		for (View<CyNode> nodeView : nodeViews) {
			
			if (nodeView != null) {
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, 
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)
								+ displacement.x() * distanceScale);
				
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, 
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)
								+ displacement.y() * distanceScale);
				
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, 
						nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION)
								+ displacement.z() * distanceScale);
			}
		}
	}
	
	
	// Updates data in CyTable as well as View<CyNode>
	public static void deselectNodes(Set<CyNode> nodes, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		
		CyRow row;
		
		for (CyNode node : nodes) {
			if (node != null) {
				row = table.getRow(node.getSUID());
				
				if (row != null) {
					row.set(SELECTED_COLUMN_NAME, false);
				}
				
				networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_SELECTED, false);
			}
		}
	}
	
	// Sets data in CyTable as well as View<CyNode>
	public static void setNodeSelected(int index, CyNetworkView networkView, boolean selected) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyRow row = table.getRow(network.getNode(index).getSUID());
		
		row.set(SELECTED_COLUMN_NAME, selected);
		
		networkView.getNodeView(network.getNode(index)).setVisualProperty(
				BasicVisualLexicon.NODE_SELECTED, selected);
	}

	public static boolean checkNodeSelected(int index, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyRow row = table.getRow(network.getNode(index).getSUID());
		
		return row.get(SELECTED_COLUMN_NAME, Boolean.class);
	}
	
	// Updates data in CyTable as well as View<CyNode>
	public static void deselectEdges(Set<CyEdge> edges, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultEdgeTable();
		
		CyRow row;
		
		for (CyEdge edge : edges) {
			
			if (edge != null) {
				
				row = table.getRow(edge.getSUID());
				
				if (row != null) {
					row.set(SELECTED_COLUMN_NAME, false);
				}
				
				networkView.getEdgeView(edge).setVisualProperty(BasicVisualLexicon.EDGE_SELECTED, false);
			}
		}
	}
	
	// Sets data in CyTable as well as View<CyNode>
	public static void setEdgeSelected(int index, CyNetworkView networkView, boolean selected) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultEdgeTable();
		CyRow row = table.getRow(network.getEdge(index).getSUID());
		
		row.set(SELECTED_COLUMN_NAME, selected);
		
		networkView.getEdgeView(network.getEdge(index)).setVisualProperty(
				BasicVisualLexicon.EDGE_SELECTED, selected);
	}
	
	public static boolean checkEdgeSelected(int index, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyEdge edge = network.getEdge(index);
		
		if (edge != null) {
			CyRow row = table.getRow(edge.getSUID());
			return row.get(SELECTED_COLUMN_NAME, Boolean.class);
		} else {
			return false;
		}
	}
	
	/**
	 * Obtain the coordinates of a given node.
	 * 
	 * @param node The node whose coordinates are to be obtained
	 * @param networkView The {@link CyNetworkView} containing the node
	 * @param distanceScale The distance scale ratio used to convert from Cytoscape coordinates
	 * @return A {@link Vector3} object containing the coordinates, or <code>null</code> otherwise, such
	 * as if no such node was found.
	 */
	public static Vector3 obtainNodeCoordinates(CyNode node, CyNetworkView networkView, double distanceScale) {
		Vector3 coordinates = null;
		
		View<CyNode> nodeView = networkView.getNodeView(node);
		
		if (nodeView != null) {
			double x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) / distanceScale;
			double y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) / distanceScale;
			double z = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION) / distanceScale;
			
			// TODO: Perform a check to ensure none of x, y, z are null?
			coordinates = new Vector3(x, y, z);
		}
		
		return coordinates;
	}
	
	/**
	 * Find a temporary identifier that can uniquely identify a pair of nodes, ignoring the order of the nodes.
	 * The identifier is valid as long as the size of the network does not exceed the given network size.
	 * 
	 * @param source The source node
	 * @param target The target node
	 * @param networkSize The number of nodes in the network. If the network grew, the identifiers are no
	 * longer guaranteed to be unique.
	 * @return An identifier that uniquely identifies the pair of nodes, ignoring the order of the nodes.
	 */
	public static long obtainPairIdentifier(CyNode source, CyNode target, int networkSize) {
		long identifier;
		
		int sourceIndex = source.getIndex();
		int targetIndex = target.getIndex();
		
		if (sourceIndex >= targetIndex) {
			identifier = (long) networkSize * sourceIndex + targetIndex;
		} else {
			identifier = (long) networkSize * targetIndex + sourceIndex;
		}
		
		return identifier;
	}
}
