package org.cytoscape.paperwing.internal.input;

import org.cytoscape.paperwing.internal.coordinator.ViewingCoordinator;
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
	
	/**
	 * Move the rectangular boundaries on the bird's eye view based on where
	 * the mouse clicked
	 * 
	 * @param mouse
	 * @param graphicsData
	 */
	private void processMoveBounds(MouseMonitor mouse, GraphicsData graphicsData) {
		CoordinatorData coordinatorData = graphicsData.getCoordinatorData();
		SimpleCamera camera = graphicsData.getCamera();
		
		if (mouse.getHeld().contains(MouseEvent.BUTTON1)
				&& coordinatorData.isInitialBoundsMatched()) {
			Vector3 nearMousePosition = GeometryToolkit.convertMouseTo3d(
					mouse, graphicsData, ViewingCoordinator.NEAR_BOUNDS_DISTANCE);
			Vector3 farMousePosition = GeometryToolkit.convertMouseTo3d(
					mouse, graphicsData, ViewingCoordinator.FAR_BOUNDS_DISTANCE);
			
			// The y-coordinate needs to be inverted
			// mousePosition.set(mousePosition.x(), -mousePosition.y(), mousePosition.z());
			
			Quadrilateral oldNearBounds = coordinatorData.getNearBounds();
			
			oldNearBounds.moveTo(findClickedNewCenter(
					camera.getPosition(), camera.getDirection(), oldNearBounds.getCenterPoint(), nearMousePosition));

			coordinatorData.setBoundsManuallyChanged(true);
		}
	}
	
	/** 
	 * Moves a point from its previous position to align itself with the line formed between
	 * the camera and the projected mouse click position.
	 * 
	 * @param cameraPosition The position of the camera used to align with
	 * @param cameraDirection The direction vector of the camera used as the normal for orthogonal displacement
	 * @param oldPosition The old position, used to obtain orthogonal data
	 * @param projectedMousePosition The projected mouse position to align with
	 */
	private Vector3 findClickedNewCenter(Vector3 cameraPosition, Vector3 cameraDirection, Vector3 oldPosition, Vector3 projectedMousePosition) {
		// Line-plane intersection approach
//		Vector3 newCenterPoint = GeometryToolkit.findLinePlaneIntersection(camera.getPosition(), 
//				mousePosition.subtract(camera.getPosition()), oldBounds.getCenterPoint(), camera.getDirection());
		
		// Orthogonal anchor approach (not complete, approximate)
//		Vector3 newCenterPoint = GeometryToolkit.findNewOrthogonalAnchoredPosition(mousePosition, 
//				oldBounds.getCenterPoint(), camera.getDirection());
		
		// Direct calculation approach
		double projectionOrthogonalDistance = GeometryToolkit.findOrthogonalDistance(
				cameraPosition, 
				oldPosition,
				cameraDirection);

		Vector3 mouseCameraOffset = projectedMousePosition.subtract(cameraPosition);
		double mouseCameraAngle = cameraDirection.angle(mouseCameraOffset);
		
		// TODO: implement checks so mouseCameraAngle stays away from 90 degrees, but should be ok as
		// this means the mouse is at an unlikely 90 degrees from the direction vector
		double projectionDiagonalDistance = projectionOrthogonalDistance / Math.cos(mouseCameraAngle);
		
		Vector3 newPosition = mouseCameraOffset.normalize();
		newPosition.multiplyLocal(projectionDiagonalDistance);
		newPosition.addLocal(cameraPosition);
		
		return newPosition;
	}
}
