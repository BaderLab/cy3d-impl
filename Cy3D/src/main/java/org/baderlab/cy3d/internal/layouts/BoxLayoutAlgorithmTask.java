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

public class BoxLayoutAlgorithmTask extends AbstractLayoutTask {

	
	public BoxLayoutAlgorithmTask(String name, CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut, String layoutAttribute, UndoSupport undo) {
		super(name, networkView, nodesToLayOut, layoutAttribute, undo);
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {
		
		// Break graph into partitions
		List<LayoutPartition> layoutPartitions = PartitionUtil.partition(networkView, false, null);
		
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
			arrangeAsBox(partition, 270);
		}
		
		LayoutToolkit.arrangePartitions(partitions);
	}
	
	private void arrangeAsBox(Collection<View<CyNode>> nodeViews, double nodeSpacing) {		
		int nodeCount = nodeViews.size();
		int nodesPerFace = (int) Math.ceil(nodeCount / 6.0);
		
		int sideLength = (int) Math.ceil(Math.sqrt(nodesPerFace));
		double halfSideLength = sideLength / 2.0;
		
		Vector3 center = LayoutToolkit.findCenter(nodeViews);
		
		// The position of the top-left corner of a face
		Vector3 faceCorner;
		// A unit vector pointing rightwards from the current corner
		Vector3 faceRight = new Vector3();
		// A unit vector pointing downwards from the current corner
		Vector3 faceDown = new Vector3();
		
		int count = 0;
		for (View<CyNode> nodeView : nodeViews) {
			int face = count / nodesPerFace;
		
			switch (face) {
				// Front face (positive z direction)
				case 0:
					faceCorner = center.plus(-halfSideLength * nodeSpacing, 
							halfSideLength * nodeSpacing,
							halfSideLength * nodeSpacing);
					faceRight.set(1, 0, 0);
					faceDown.set(0, -1, 0);
					break;
				// Back face (negative z direction)
				case 1:
					faceCorner = center.plus(halfSideLength * nodeSpacing, 
							halfSideLength * nodeSpacing,
							-halfSideLength * nodeSpacing);
					faceRight.set(-1, 0, 0);
					faceDown.set(0, -1, 0);
					break;
				// Left face (negative x direction)
				case 2:
					faceCorner = center.plus(-halfSideLength * nodeSpacing, 
							halfSideLength * nodeSpacing,
							-halfSideLength * nodeSpacing);
					faceRight.set(0, 0, 1);
					faceDown.set(0, -1, 0);
					break;
				// Right face (positive x direction)
				case 3:
					faceCorner = center.plus(halfSideLength * nodeSpacing, 
							halfSideLength * nodeSpacing,
							halfSideLength * nodeSpacing);
					faceRight.set(0, 0, -1);
					faceDown.set(0, -1, 0);
					break;
				// Top face (positive y direction)
				case 4:
					faceCorner = center.plus(-halfSideLength * nodeSpacing, 
							halfSideLength * nodeSpacing,
							-halfSideLength * nodeSpacing);
					faceRight.set(1, 0, 0);
					faceDown.set(0, 0, 1);
					break;
				// Bottom face (negative y direction)
				case 5:
					faceCorner = center.plus(-halfSideLength * nodeSpacing, 
							-halfSideLength * nodeSpacing,
							halfSideLength * nodeSpacing);
					faceRight.set(1, 0, 0);
					faceDown.set(0, 0, -1);
					break;
				default:
					throw new IllegalStateException("Current node cannot be allocated to a face of the arrangement cube.");
			}
			
			// The row that this node belongs to on the current face
			int row = (count % nodesPerFace) % sideLength + 1;
			
			// The column that this node belongs to on the current face
			int column = (count % nodesPerFace) / sideLength + 1;
			
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, 
					faceCorner.x() + faceRight.x() * nodeSpacing * column + faceDown.x() * nodeSpacing * row);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, 
					faceCorner.y() + faceRight.y() * nodeSpacing * column + faceDown.y() * nodeSpacing * row);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Z_LOCATION, 
					faceCorner.z() + faceRight.z() * nodeSpacing * column + faceDown.z() * nodeSpacing * row);
			
			count++;
		}
	}
}
