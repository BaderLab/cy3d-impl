package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.data.GraphicsSelectionData;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.NetworkToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;

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
	
		if (selectionData.getSelectedNodeIndices().isEmpty()) {
			return;
		}
		
		if (mouse.getPressed().contains(MouseEvent.BUTTON1)) {
			Vector3 selectedCenter = NetworkToolkit.findCenter(
					selectionData.getSelectedNodeIndices(), 
					graphicsData.getNetworkView(), 
					graphicsData.getDistanceScale());
			
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
			
			NetworkToolkit.displaceNodes(selectionData.getSelectedNodeIndices(), 
					graphicsData.getNetworkView(), graphicsData.getDistanceScale(), nodeDisplacement);
			
			selectionData.setPreviousSelectedProjection(
					selectionData.getCurrentSelectedProjection().copy());
		}
	}
}
