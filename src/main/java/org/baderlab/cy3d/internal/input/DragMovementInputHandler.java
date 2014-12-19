package org.baderlab.cy3d.internal.input;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.GraphicsSelectionData;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

public class DragMovementInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		
		processDragMovement(keys, mouse, graphicsData);
	}
	
	public void processDragMovement(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
			
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		SimpleCamera camera = graphicsData.getCamera();
		CyNetworkView networkView = graphicsData.getNetworkView();
	
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
		Set<View<CyNode>> selectedNodeViews = new HashSet<View<CyNode>>(selectedNodes.size());
		
		for (CyNode node : selectedNodes) {
			selectedNodeViews.add(networkView.getNodeView(node));
		}
		
		if (selectedNodeViews.isEmpty()) {
			return;
		}
		
		if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
			Vector3 selectedCenter = NetworkToolkit.findCenter(selectedNodeViews, graphicsData.getDistanceScale());
			
			selectionData.setSelectProjectionDistance(
					GeometryToolkit.findOrthogonalDistance(camera.getPosition(), selectedCenter, camera.getDirection()));
			
			selectionData.setPreviousSelectedProjection(
					GeometryToolkit.convertMouseTo3d(mouse, graphicsData, selectionData.getSelectProjectionDistance()));
		}
		
		if (mouse.hasMoved() 
				&& mouse.getHeld().contains(MouseEvent.BUTTON1)
				&& keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
			
			selectionData.setCurrentSelectedProjection(
					GeometryToolkit.convertMouseTo3d(mouse, graphicsData, selectionData.getSelectProjectionDistance()));
			
			Vector3 nodeDisplacement = selectionData.getCurrentSelectedProjection().subtract(selectionData.getPreviousSelectedProjection());
			
			NetworkToolkit.displaceNodes(selectedNodeViews, graphicsData.getDistanceScale(), nodeDisplacement);
			
			selectionData.setPreviousSelectedProjection(selectionData.getCurrentSelectedProjection().copy());
		}
	}
}
