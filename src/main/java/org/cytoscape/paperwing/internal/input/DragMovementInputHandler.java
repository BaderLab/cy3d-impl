package org.cytoscape.paperwing.internal.input;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

public class DragMovementInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		processDragMovement(keys, mouse, graphicsData);
	}
	
	public void processDragMovement(KeyboardMonitor keys, 
			MouseMonitor mouse, GraphicsData graphicsData) {
			
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		SimpleCamera camera = graphicsData.getCamera();
		CyNetworkView networkView = graphicsData.getNetworkView();
	
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
		Set<View<CyNode>> selectedNodeViews = new HashSet<View<CyNode>>(selectedNodes.size());
		
		for (CyNode node : selectedNodes) {
			selectedNodeViews.add(networkView.getNodeView(node));
		}
		
		System.out.println("Selected node count: " + selectedNodes.size() + ", " + selectedNodeViews.size());
		
		if (selectedNodeViews.isEmpty()) {
			return;
		}
		
		if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
			Vector3 selectedCenter = NetworkToolkit.findCenter(selectedNodeViews, graphicsData.getDistanceScale());
			
			selectionData.setSelectProjectionDistance(GeometryToolkit.findOrthogonalDistance(
					camera.getPosition(), selectedCenter, camera.getDirection()));
			
			selectionData.setPreviousSelectedProjection(GeometryToolkit.convertMouseTo3d(mouse, 
					graphicsData, selectionData.getSelectProjectionDistance()));
		}
		
		if (mouse.hasMoved() 
				&& mouse.getHeld().contains(MouseEvent.BUTTON1)
				&& keys.getHeld().contains(KeyEvent.VK_CONTROL)) {
			
			selectionData.setCurrentSelectedProjection(GeometryToolkit.convertMouseTo3d(mouse, 
					graphicsData, selectionData.getSelectProjectionDistance()));
			
			Vector3 nodeDisplacement = selectionData.getCurrentSelectedProjection().subtract(
					selectionData.getPreviousSelectedProjection());
			
			NetworkToolkit.displaceNodes(selectedNodeViews, graphicsData.getDistanceScale(), nodeDisplacement);
			
			selectionData.setPreviousSelectedProjection(
					selectionData.getCurrentSelectedProjection().copy());
		}
	}
}
