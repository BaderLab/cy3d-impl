package org.baderlab.cy3d.internal.input.handler.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.input.handler.MouseWheelCommand;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

public class CameraZoomCommand implements MouseWheelCommand {

	private final GraphicsData graphicsData;
	
	
	public CameraZoomCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}


	@Override
	public void execute(int dWheel) {
		Camera camera = graphicsData.getCamera();
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Varying distance between camera and camera's target point
		if (dWheel != 0) {
			camera.moveForward(-dWheel);
			
			List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
			
			if (!selectedNodes.isEmpty()) {
				Set<Long> selectedNodeIndices = new HashSet<Long>();
				
				for (CyNode node : selectedNodes) {
					selectedNodeIndices.add(node.getSUID());
				}
				
				Vector3 averagePosition = NetworkToolkit.findCenter(selectedNodeIndices, networkView, GraphicsData.DISTANCE_SCALE);
				selectionData.setSelectProjectionDistance(averagePosition.distance(camera.getPosition()));
			}
		}
	}

}
