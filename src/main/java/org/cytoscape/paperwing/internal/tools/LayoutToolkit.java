package org.cytoscape.paperwing.internal.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class LayoutToolkit {
	
	/**
	 * Arranges the given partitions in a 3D cube-like grid with equal spacing. The spacing between partitions is proportional
	 * to the radius of the largest partition.
	 * 
	 * The average position of all given nodes should remain the same after the arrangement.
	 * 
	 * @param partitions The graph partitions to arrange.
	 */
	public static void arrangePartitions(Collection<Collection<View<CyNode>>> partitions) {
		// Consider sorting partitions in order of decreasing radius?
		
		Map<Collection<View<CyNode>>, Double> partitionRadii = new HashMap<Collection<View<CyNode>>, Double>(partitions.size());
		
		// Basic approach: 1 partition per cube
		int cubeLength = (int) Math.ceil(Math.pow(partitions.size(), 1.0/3));
		
		// System.out.println("cubeLength: " + cubeLength);
		
		// Average position of all nodes
		Vector3 averageTotalNodePosition = new Vector3();
		int totalNodeCount = 0;
		
		double largestRadius = -1;
		
		for (Collection<View<CyNode>> partition : partitions) {
			averageTotalNodePosition.addLocal(findCenter(partition).multiply(partition.size()));
			totalNodeCount += partition.size();
			
			double partitionRadius = findSubgraphRadius(partition);
			partitionRadii.put(partition, partitionRadius);
			
			if (partitionRadius > largestRadius) {
				largestRadius = partitionRadius;
			}
		}
		
		largestRadius = Math.max(largestRadius, 50);
		largestRadius *= 2;
		
		// Calculate the average position of all nodes by using the average position of partitions weighted by their node count
		averageTotalNodePosition.divideLocal(totalNodeCount);
		
		int count = 0;
		for (Collection<View<CyNode>> partition : partitions) {
			int x = count % cubeLength;
			int y = count / cubeLength % cubeLength;
			int z = count / cubeLength / cubeLength;
			
			// TODO: Need to set offset so that total average node position is preserved
			Vector3 offset = new Vector3(x * largestRadius, y * largestRadius, z * largestRadius);
			double halfCubeActualLength = (double) (cubeLength - 1) / 2 * largestRadius;
			offset.subtractLocal(halfCubeActualLength, halfCubeActualLength, halfCubeActualLength);
			
			displaceNodes(partition, offset.plus(averageTotalNodePosition));
			
			// System.out.println(new Vector3(x, y, z));
			count++;
		}
	}
	
	/**
	 * Displace a set of nodes such that their average of position is moved to the given point. Each node's 
	 * position relative to the average position should remain unchanged.
	 * 
	 * @param nodeViews The node views to displace
	 * @param target The target position to move the nodes towards
	 */
	public static void displaceNodes(Collection<View<CyNode>> nodeViews, Vector3 target) {
		Vector3 currentCenter = findCenter(nodeViews);
		Vector3 displacement = target.subtract(currentCenter);
		
		for (View<CyNode> nodeView : nodeViews) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,
					nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) + displacement.x());
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,
					nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) + displacement.y());
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION,
					nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION) + displacement.z());
		}
	}
	

	/**
	 * Find the average position of a given set of nodes
	 * @param nodeViews A set of nodes whose average position is to be found
	 * @return The average position, in coordinates directly obtained from node visual properties
	 */
	public static Vector3 findCenter(Collection<View<CyNode>> nodeViews) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		for (View<CyNode> nodeView : nodeViews) {
			x += nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			y += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			z += nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION);
		}
		
		Vector3 center = new Vector3(x, y, z);
		center.divideLocal(nodeViews.size());
		
		return center;
	}
	
	/**
	 * Find the radius of the subgraph formed by the given set of nodes. This radius can be useful
	 * for determining the spacing between graph partitions.
	 * @return The radius of the subgraph, which is the distance from the average position of the nodes to
	 * the node that is farthest from the average position.
	 */
	public static double findSubgraphRadius(Collection<View<CyNode>> nodeViews) {
		
		// Obtain the average node position
		Vector3 averagePosition = findCenter(nodeViews);
		
		double maxDistanceSquared = -1;
		View<CyNode> farthestNode = null;
		double distanceSquared;
		
		for (View<CyNode> nodeView : nodeViews) {
			distanceSquared = Math.pow(nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) - averagePosition.x(), 2)
				+ Math.pow(nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) - averagePosition.y(), 2)
				+ Math.pow(nodeView.getVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION) - averagePosition.z(), 2);
			
			if (distanceSquared > maxDistanceSquared) {
				maxDistanceSquared = distanceSquared;
				farthestNode = nodeView;
			}
		}
		
		return Math.sqrt(maxDistanceSquared);
	}
}
