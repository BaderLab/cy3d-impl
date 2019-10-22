package org.baderlab.cy3d.internal.tools;

import java.util.Collection;
import java.util.Set;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewSnapshot;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class NetworkToolkit {

	
	/**
	 * Find the average position of a given set of nodes.
	 * 
	 * @param nodeIndices The indices of nodes whose average position is to be found
	 * @param networkView The network view containing the nodes
	 * @param distanceScale The distance scaling used to convert between Cytoscape and renderer coordinates.
	 * @return The average position
	 */
	public static Vector3 findCenter(Collection<View<CyNode>> selectedNodes, CyNetworkViewSnapshot networkView, double distanceScale) {		
		double x = 0, y = 0, z = 0;
		int visitedCount = 0;
		
		for (View<CyNode> nodeView : selectedNodes) {
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
	public static void fitInView(Camera camera, Collection<? extends View<CyNode>> nodeViews, 
			double distanceScale, double distanceFactor, double minDistance) {
		Vector3 center = NetworkToolkit.findCenter(nodeViews, distanceScale);
		Vector3 farthestNode = NetworkToolkit.findFarthestNodeFromCenter(nodeViews, center, distanceScale);
		
		double newDistance = farthestNode.distance(center);
		
		// Further increase the distance needed
		newDistance *= distanceFactor;
		
		// Enforce minimum distance
		newDistance = Math.max(newDistance, minDistance);
		
		Vector3 offset = camera.getDirection().multiply(-newDistance);
		
		camera.moveTo(center.plus(offset), null);
//		camera.setDistance(newDistance);
	}
	
	/**
	 * Obtain the average position of a set of nodes.
	 * 
	 * @param nodeViews The node views whose average position is to be obtained
	 * @param distanceScale The distance scaling used to convert between Cytoscape and render coordinates. Cytoscape coordinates
	 * are divided by this scale to obtain renderer coordinates.
	 * @return The average position
	 */
	public static Vector3 findCenter(Collection<? extends View<CyNode>> nodeViews, double distanceScale) {
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
	public static Vector3 findFarthestNodeFromCenter(Collection<? extends View<CyNode>> nodeViews, Vector3 networkCenter, double distanceScale) {
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
					row.set(CyNetwork.SELECTED, false);
				}
				
				networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_SELECTED, false);
			}
		}
	}
	
	// Sets data in CyTable as well as View<CyNode>
	public static void flipNodeSelection(long suid, CyNetworkView networkView) {
		CyRow row = getNodeRow(suid, networkView);
		boolean isSelected = Boolean.TRUE.equals(row.get(CyNetwork.SELECTED, Boolean.class));
		row.set(CyNetwork.SELECTED, !isSelected);
	}
	
	// Sets data in CyTable as well as View<CyNode>
	public static void setNodeSelection(long suid, CyNetworkView networkView, boolean selected) {
		CyRow row = getNodeRow(suid, networkView);
		row.set(CyNetwork.SELECTED, selected);
	}

	private static CyRow getNodeRow(long suid, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyNode node;
		var nodeView = networkView.getNodeView(suid);
		if(nodeView != null) {
			node = nodeView.getModel();
		} else {
			node = network.getNode(suid);
		}
		return table.getRow(node.getSUID());	
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
					row.set(CyNetwork.SELECTED, false);
				}
				
				networkView.getEdgeView(edge).setVisualProperty(BasicVisualLexicon.EDGE_SELECTED, false);
			}
		}
	}
	
	// Sets data in CyTable as well as View<CyNode>
	public static void flipEdgeSelection(long suid, CyNetworkView networkView) {
		CyRow row = getEdgeRow(suid, networkView);
		boolean isSelected = Boolean.TRUE.equals(row.get(CyNetwork.SELECTED, Boolean.class));
		row.set(CyNetwork.SELECTED, !isSelected);
	}
	
	// Sets data in CyTable as well as View<CyNode>
	public static void setEdgeSelection(long suid, CyNetworkView networkView, boolean selected) {
		CyRow row = getEdgeRow(suid, networkView);
		row.set(CyNetwork.SELECTED, selected);
	}
	private static CyRow getEdgeRow(long suid, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultEdgeTable();
		CyEdge edge;
		var edgeView = networkView.getEdgeView(suid);
		if(edgeView != null) {
			edge = edgeView.getModel();
		} else {
			edge = network.getEdge(suid);
		}
		return table.getRow(edge.getSUID());	
	}
	

	public static boolean checkEdgeSelected(long index, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyEdge edge = network.getEdge(index);
		
		if (edge != null) {
			CyRow row = table.getRow(edge.getSUID());
			return row.get(CyNetwork.SELECTED, Boolean.class);
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
	public static Vector3 obtainNodeCoordinates(View<CyNode> nodeView, CyNetworkViewSnapshot networkView, double distanceScale) {
		Vector3 coordinates = null;
		if (nodeView != null) {
			double x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) / distanceScale;
			double y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) / distanceScale;
			double z = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION) / distanceScale;
			// TODO: Perform a check to ensure none of x, y, z are null?
			coordinates = new Vector3(x, y, z);
		}
		return coordinates;
	}
	
}
