package org.baderlab.cy3d.internal.input.handler.commands;

import java.util.Collection;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.input.handler.MouseWheelCommand;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewSnapshot;
import org.cytoscape.view.model.View;

public class CameraZoomCommand implements MouseWheelCommand {

	private final GraphicsData graphicsData;
	
	
	public CameraZoomCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}


	@Override
	public void execute(int dWheel) {
		Camera camera = graphicsData.getCamera();
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		CyNetworkViewSnapshot networkView = graphicsData.getNetworkSnapshot();
		
		// Varying distance between camera and camera's target point
		if (dWheel != 0) {
			camera.moveForward(-dWheel);
			
			Collection<View<CyNode>> selectedNodes = networkView.getSelectedNodes();
			if (!selectedNodes.isEmpty()) {
				Vector3 averagePosition = NetworkToolkit.findCenter(selectedNodes, networkView, GraphicsData.DISTANCE_SCALE);
				selectionData.setSelectProjectionDistance(averagePosition.distance(camera.getPosition()));
			}
		}
	}

}
