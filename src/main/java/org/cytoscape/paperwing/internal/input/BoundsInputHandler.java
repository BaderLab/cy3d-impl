package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.data.CoordinatorData;
import org.cytoscape.paperwing.internal.data.GraphicsData;
import org.cytoscape.paperwing.internal.geometric.Quadrilateral;
import org.cytoscape.paperwing.internal.geometric.Vector3;
import org.cytoscape.paperwing.internal.tools.GeometryToolkit;
import org.cytoscape.paperwing.internal.tools.SimpleCamera;

import com.jogamp.newt.event.MouseEvent;

public class BoundsInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		// TODO Auto-generated method stub
		processMoveBounds(mouse, graphicsData);
	}
	
	private void processMoveBounds(MouseMonitor mouse, GraphicsData graphicsData) {
		CoordinatorData coordinatorData = graphicsData.getCoordinatorData();
		SimpleCamera camera = graphicsData.getCamera();
		
		if (mouse.getHeld().contains(MouseEvent.BUTTON1)
				&& coordinatorData.isInitialBoundsMatched()) {
			Vector3 mousePosition = GeometryToolkit.convertMouseTo3d(mouse, graphicsData, camera.getDistance());
			
			// The y-coordinate needs to be inverted
			// mousePosition.set(mousePosition.x(), -mousePosition.y(), mousePosition.z());
			
			Quadrilateral oldBounds = coordinatorData.getBounds();
			
//			Vector3 newCenterPoint = GeometryToolkit.findLinePlaneIntersection(camera.getPosition(), 
//					mousePosition.subtract(camera.getPosition()), oldBounds.getCenterPoint(), camera.getDirection());
			
			Vector3 newCenterPoint = GeometryToolkit.findNewOrthogonalAnchoredPosition(mousePosition, oldBounds.getCenterPoint(), camera.getDirection());
			
			oldBounds.moveTo(newCenterPoint);
			
			System.out.println("newCenterPoint: " + newCenterPoint);
			
			coordinatorData.setBoundsManuallyChanged(true);
		}
	}
}
