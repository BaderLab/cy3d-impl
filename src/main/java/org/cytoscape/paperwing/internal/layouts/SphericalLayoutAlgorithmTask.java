package org.cytoscape.paperwing.internal.layouts;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.view.layout.AbstractBasicLayoutTask;
import org.cytoscape.view.layout.AbstractLayoutAlgorithmContext;
import org.cytoscape.view.layout.LayoutNode;
import org.cytoscape.view.layout.LayoutPartition;
import org.cytoscape.view.layout.PartitionUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

public class SphericalLayoutAlgorithmTask extends AbstractBasicLayoutTask {

	private SphericalLayoutContext context;
	
	public SphericalLayoutAlgorithmTask(String name,
			SphericalLayoutContext context) {
		super(name, context);
		this.context = context;
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {
		
		// Break graph into partitions
		List<LayoutPartition> partitions = PartitionUtil.partition(networkView, false, null);
		int numPartitions = partitions.size();
		int count = 0;
		
		System.out.println("Number of partitions: " + numPartitions);
		
		double xOffsetAmount = 200;
		double yOffsetAmount = 200;
		
		Collection<View<CyNode>> partitionNodeViews;
		
		for (LayoutPartition partition : partitions) {
			partitionNodeViews = new HashSet<View<CyNode>>();
			
			for (LayoutNode layoutNode : partition.getNodeList()) {
				View<CyNode> nodeView = layoutNode.getNodeView();
				partitionNodeViews.add(nodeView);
			}
			
			partition.offset(count * xOffsetAmount, count * yOffsetAmount);
			
			arrangeAsSphere(partitionNodeViews);
			
			count++;
		}
	
		// arrangeAsSphere(networkView.getNodeViews());
		
	}
	
	private void arrangeAsSphere(Collection<View<CyNode>> nodeViews) {
		int nodeCount = nodeViews.size();
		int current = 0;
		
		double sphereRadius = findSphereRadius(nodeCount);
		double x, y, z;
		
		Vector3 sphereCenter = findCenter(nodeViews);
		
		for (View<CyNode> nodeView : nodeViews) {
			
			int nodesPerLevel = (int) Math.max(Math.sqrt(nodeCount), 1);
			
			// The fraction should range from 0 to 1
			double levelFraction = Math.floor(current / nodesPerLevel) * nodesPerLevel / nodeCount;
		
			double thetaLimit = 0.0;
			double phiLimit = 0.05;
			
			// double theta = Math.PI / 2 - (Math.PI * thetaLimit + (double) level / numLevels * Math.PI * (1 - 2 * thetaLimit));	
			double theta = Math.PI / 2 - (Math.PI * thetaLimit + levelFraction * Math.PI * (2 - 2 * thetaLimit));
			double phi = Math.PI * phiLimit + (double) (current % nodesPerLevel) / (nodesPerLevel - 1) * Math.PI * (1 - 2 * phiLimit);
			
			/*
			int numLevels = (int) Math.sqrt(nodeCount);
			
			int level = (current / numLevels) * numLevels;
			
			double thetaLimit = 0.1;
			double phiLimit = 0.1;
				
			double theta = Math.PI / 2 - (Math.PI * thetaLimit + (double) level / nodeCount * Math.PI * (1 - 2 * thetaLimit));
			double phi = Math.PI * phiLimit + (double) (current % numLevels) / numLevels * Math.PI * (2 - 2 * phiLimit);			
			*/
			
			x = Math.cos(theta) * Math.sin(phi);
			y = Math.sin(theta) * Math.sin(phi);
			z = Math.cos(phi);
			
			x *= sphereRadius;
			y *= sphereRadius;
			z *= sphereRadius;
			
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x + sphereCenter.x());
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y + sphereCenter.y());
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, z + sphereCenter.z());	
			
			current++;
		}
	}
	
	/**
	 * Find the average position of a given set of nodes
	 * @param nodeViews A set of nodes whose average position is to be found
	 * @return The average position, in coordinates directly obtained from node visual properties
	 */
	private Vector3 findCenter(Collection<View<CyNode>> nodeViews) {
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
	 * Find an appropriate sphere size given the number of nodes to arrange
	 */
	private double findSphereRadius(int nodeCount) {
		return 100 + nodeCount;
	}

	private Map<CyNode, Collection<CyNode>> findCliques(CyNetwork network) {
		// TODO: Implement optimization by partitioning the graph before finding cliques
		
		Collection<CyNode> nodesList = new HashSet<CyNode>(network.getNodeList());
		
		// A map that maps every node to the biggest clique containing that node
		Map<CyNode, Collection<CyNode>> cliques = new HashMap<CyNode, Collection<CyNode>>();
		
		// Find the largest clique containing each node
		for (CyNode currentNode : nodesList) {

			Collection<CyNode> clique = new HashSet<CyNode>();
			clique.add(currentNode);

			// Loop through every neighbor of the current node
			for (CyNode neighbor : network.getNeighborList(currentNode, Type.ANY)) {

				boolean addToCurrentClique = true;
				
				for (CyNode cliqueNode : clique) {
					if (network.getConnectingEdgeList(cliqueNode, neighbor, Type.ANY).isEmpty()) {
						addToCurrentClique = false;
					}
				}
				
				if (addToCurrentClique) {
					clique.add(neighbor);
				}
			}
			
			cliques.put(currentNode, clique);
		}
		
		return cliques;
	}
	
	// Find the largest clique containing a given node from a given network
	private Collection<CyNode> findLargestClique(CyNode node, CyNetwork network) {
		Collection<CyNode> clique = new HashSet<CyNode>();
		clique.add(node);
		
		List<CyNode> neighbors = network.getNeighborList(node, Type.ANY);
		
		// Loop through every neighbor of the current node
		for (CyNode neighbor : neighbors) {

			boolean addToCurrentClique = true;
			
			for (CyNode cliqueNode : clique) {
				if (network.getConnectingEdgeList(cliqueNode, neighbor, Type.ANY).isEmpty()) {
					addToCurrentClique = false;
				}
			}
			
			if (addToCurrentClique) {
				clique.add(neighbor);
			}
		}
		
		return clique;
	}
	
	/**
	 * Return a list of all cliques in the given set of nodes, sorted in order of decreasing size.
	 * A clique is a subgraph where there is an edge between any 2 nodes.
	 * 
	 * @param nodeViews The set of node view objects that should be used to find cliques.
	 * @return A list of cliques found, sorted in order of decreasing size.
	 */
	private List<Collection<CyNode>> findCliquesOld(CyNetwork network) {
		List<Collection<CyNode>> cliques = new LinkedList<Collection<CyNode>>();
		
		// TODO: Implement optimization by partitioning the graph before finding cliques
		
		Collection<CyNode> nodesToBeVisited = new HashSet<CyNode>(network.getNodeList());
		Collection<CyNode> nodesPlacedInClique;
		
		Collection<CyNode> clique;
		CyNode currentNode;
		
		// Find the largest clique containing each node
		while(!nodesToBeVisited.isEmpty()) {
			currentNode = nodesToBeVisited.iterator().next();
			
			clique = new HashSet<CyNode>();
			clique.add(currentNode);

			nodesPlacedInClique = new HashSet<CyNode>();
			nodesPlacedInClique.add(currentNode);
			
			// Loop through every potential neighbor for the current node
			for (CyNode potentialNeighbor : nodesToBeVisited) {
				if (potentialNeighbor != currentNode) {
					boolean addToCurrentClique = true;
					
					for (CyNode cliqueNode : clique) {
						if (!network.containsEdge(potentialNeighbor, cliqueNode)
								&& !network.containsEdge(cliqueNode, potentialNeighbor)) {
							
							addToCurrentClique = false;
						}
					}
					
					if (addToCurrentClique) {
						clique.add(potentialNeighbor);
						nodesPlacedInClique.add(potentialNeighbor);
					}
				}
			}
			
			cliques.add(clique);
			nodesToBeVisited.removeAll(nodesPlacedInClique);
		}
		
		return null;
	}
}
