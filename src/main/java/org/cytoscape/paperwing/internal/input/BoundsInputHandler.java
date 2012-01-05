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
			
			// Line-plane intersection approach
//			Vector3 newCenterPoint = GeometryToolkit.findLinePlaneIntersection(camera.getPosition(), 
//					mousePosition.subtract(camera.getPosition()), oldBounds.getCenterPoint(), camera.getDirection());
			
			// Orthogonal anchor approach (not complete, approximate)
//			Vector3 newCenterPoint = GeometryToolkit.findNewOrthogonalAnchoredPosition(mousePosition, 
//					oldBounds.getCenterPoint(), camera.getDirection());
			
			// Direct calculation approach
			double projectionOrthogonalDistance = GeometryToolkit.findOrthogonalDistance(camera.getPosition(), 
					oldBounds.getCenterPoint(), camera.getDirection());

			Vector3 mouseCameraOffset = mousePosition.subtract(camera.getPosition());
			double mouseCameraAngle = camera.getDirection().angle(mouseCameraOffset);
			
			// TODO: implement checks so mouseCameraAngle stays away from 90 degrees, but should be ok as
			// this means the mouse is at an unlikely 90 degrees from the direction vector
			double projectionDiagonalDistance = projectionOrthogonalDistance / Math.cos(mouseCameraAngle);
			
			Vector3 newCenterPoint = mouseCameraOffset.normalize();
			newCenterPoint.multiplyLocal(projectionDiagonalDistance);
			newCenterPoint.addLocal(camera.getPosition());
			
			oldBounds.moveTo(newCenterPoint);
			
			// Debug useful
//			System.out.println("mouseX: " + mouse.x());
//			System.out.println("mouseY: " + mouse.y());
//	
//			System.out.println("mousePosition: " + mousePosition);
//			System.out.println("newCenterPoint: " + newCenterPoint);
			
			coordinatorData.setBoundsManuallyChanged(true);
		}
	}
}
