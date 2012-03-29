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
import org.cytoscape.paperwing.internal.cytoscape.view.WindNetworkView;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.LayoutToolkit;
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
		List<LayoutPartition> layoutPartitions = PartitionUtil.partition(networkView, false, null);
		int numPartitions = layoutPartitions.size();
		
		Collection<Collection<View<CyNode>>> partitions = new HashSet<Collection<View<CyNode>>>(layoutPartitions.size());
		
		Collection<View<CyNode>> partitionNodeViews;
		
		for (LayoutPartition partition : layoutPartitions) {
			partitionNodeViews = new HashSet<View<CyNode>>();
			
			for (LayoutNode layoutNode : partition.getNodeList()) {
				View<CyNode> nodeView = layoutNode.getNodeView();
				partitionNodeViews.add(nodeView);
			}
			
			partitions.add(partitionNodeViews);
		}
		
		for (Collection<View<CyNode>> partition : partitions) {
			arrangeAsSphere(partition);
		}
		
		LayoutToolkit.arrangePartitions(partitions);
	}
	
	private void arrangeAsSphere(Collection<View<CyNode>> nodeViews) {
		int nodeCount = nodeViews.size();
		int current = 0;
		
		double sphereRadius = findSphereRadius(nodeCount);
		double x, y, z;
		
		Vector3 sphereCenter = LayoutToolkit.findCenter(nodeViews);
		
		for (View<CyNode> nodeView : nodeViews) {
			
			int nodesPerLevel = (int) Math.max(Math.sqrt(nodeCount), 3);
			
			// The fraction should range from 0 to 1
			double levelFraction = Math.floor(current / nodesPerLevel) * nodesPerLevel / nodeCount;
		
			double thetaLimit = 0.0;
			
			// Perform a correction for small numbers of nodes
			double phiLimit = 0.20 - Math.min((double) nodeCount / 125, 1) * 0.15;
			
			// double theta = Math.PI / 2 - (Math.PI * thetaLimit + (double) level / numLevels * Math.PI * (1 - 2 * thetaLimit));	
			double theta = Math.PI / 2 - (Math.PI * thetaLimit + levelFraction * Math.PI * (2 - 2 * thetaLimit));
			double phi = Math.PI * phiLimit + (double) (current % nodesPerLevel) / (nodesPerLevel - 1) * Math.PI * (1 - 2 * phiLimit);
			
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
	 * Find an appropriate sphere size given the number of nodes to arrange
	 */
	private double findSphereRadius(int nodeCount) {
		return 100 + nodeCount;
	}
}
