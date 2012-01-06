package org.cytoscape.paperwing.internal.tools;

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
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class NetworkToolkit {

	private static final String SELECTED_COLUMN_NAME = "selected";
	
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

	public static void displaceNodes(Set<Integer> nodeIndices, CyNetworkView networkView, double distanceScale, Vector3 displacement) {
		View<CyNode> nodeView;
		
		for (Integer index : nodeIndices) {
			nodeView = networkView.getNodeView(networkView.getModel().getNode(index));
			
			if (nodeView != null) {
				nodeView.setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, 
						nodeView.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION)
								+ displacement.x() * distanceScale);
				
				nodeView.setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, 
						nodeView.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION)
								+ displacement.y() * distanceScale);
				
				nodeView.setVisualProperty(RichVisualLexicon.NODE_Z_LOCATION, 
						nodeView.getVisualProperty(RichVisualLexicon.NODE_Z_LOCATION)
								+ displacement.z() * distanceScale);
			}
		}
	}
	
	
	// Updates data in CyTable as well as View<CyNode>
	public static void deselectNodes(Set<Integer> nodeIndices, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		
		CyNode node;
		CyRow row;
		
		for (int index : nodeIndices) {
			node = network.getNode(index);
			
			if (node != null) {
				row = table.getRow(node.getSUID());
				
				if (row != null) {
					row.set(SELECTED_COLUMN_NAME, false);
				}
				
				networkView.getNodeView(node).setVisualProperty(RichVisualLexicon.NODE_SELECTED, false);
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
				RichVisualLexicon.NODE_SELECTED, selected);
	}

	public static boolean checkNodeSelected(int index, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyRow row = table.getRow(network.getNode(index).getSUID());
		
		return row.get(SELECTED_COLUMN_NAME, Boolean.class);
	}
	
	// Updates data in CyTable as well as View<CyNode>
	public static void deselectEdges(Set<Integer> edgeIndices, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultEdgeTable();
		
		CyEdge edge;
		CyRow row;
		
		for (int index : edgeIndices) {
			edge = network.getEdge(index);
			
			if (edge != null) {
				
				row = table.getRow(edge.getSUID());
				
				if (row != null) {
					row.set(SELECTED_COLUMN_NAME, false);
				}
				
				networkView.getEdgeView(edge).setVisualProperty(RichVisualLexicon.EDGE_SELECTED, false);
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
				RichVisualLexicon.EDGE_SELECTED, selected);
	}
	
	public static boolean checkEdgeSelected(int index, CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		CyTable table = network.getDefaultNodeTable();
		CyRow row = table.getRow(network.getEdge(index).getSUID());
		
		return row.get(SELECTED_COLUMN_NAME, Boolean.class);
	}
}
