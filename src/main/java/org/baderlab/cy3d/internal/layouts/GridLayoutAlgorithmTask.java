package org.baderlab.cy3d.internal.layouts;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.LayoutToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.layout.LayoutNode;
import org.cytoscape.view.layout.LayoutPartition;
import org.cytoscape.view.layout.PartitionUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

public class GridLayoutAlgorithmTask extends AbstractLayoutTask {

	private GridLayoutContext context;
	
	public GridLayoutAlgorithmTask(String name, GridLayoutContext context, CyNetworkView networkView,
                                   Set<View<CyNode>> nodesToLayOut, String layoutAttribute, UndoSupport undo) {
		super(name, networkView, nodesToLayOut, layoutAttribute, undo);
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
			arrangeAsGrid(partition, 130);
		}
		
		LayoutToolkit.arrangePartitions(partitions);
	}
	
	private void arrangeAsGrid(Collection<View<CyNode>> nodeViews, double spacing) {		
		
		int cubeLength = (int) Math.ceil(Math.pow(nodeViews.size(), 1.0/3));
		
		// System.out.println("cubeLength: " + cubeLength);
		
		// Average position of all nodes
		Vector3 center = LayoutToolkit.findCenter(nodeViews);
		
		int count = 0;
		
		for (View<CyNode> nodeView : nodeViews) {
			int x = count % cubeLength;
			int y = count / cubeLength % cubeLength;
			int z = count / cubeLength / cubeLength;
			
			// TODO: Need to set offset so that total average node position is preserved
			Vector3 offset = new Vector3(x * spacing, y * spacing, z * spacing);
			double halfCubeActualLength = (double) (cubeLength - 1) / 2 * spacing;
			offset.subtractLocal(halfCubeActualLength, halfCubeActualLength, halfCubeActualLength);

			Vector3 nodeNewPosition = offset.plus(center);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, nodeNewPosition.x());
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, nodeNewPosition.y());
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, nodeNewPosition.z());
			
			// System.out.println(new Vector3(x, y, z));
			count++;
		}
	}
}
