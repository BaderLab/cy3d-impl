package org.cytoscape.paperwing.internal.tools;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;

public class NetworkToolkit {

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

}
