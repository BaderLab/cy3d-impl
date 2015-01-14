package org.baderlab.cy3d.internal.input.handler.commands;

import org.baderlab.cy3d.internal.coordinator.ViewingCoordinator;
import org.baderlab.cy3d.internal.data.CoordinatorData;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.geometric.Quadrilateral;
import org.baderlab.cy3d.internal.geometric.Vector3;
import org.baderlab.cy3d.internal.input.handler.MouseCommand;
import org.baderlab.cy3d.internal.tools.GeometryToolkit;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class BirdsEyeBoundsMouseCommand implements MouseCommand {

	private final GraphicsData graphicsData;
	
	
	public BirdsEyeBoundsMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}


	@Override
	public void dragged(int x, int y) {
		CoordinatorData coordinatorData = graphicsData.getCoordinatorData();
		SimpleCamera camera = graphicsData.getCamera();
		
		if(coordinatorData.isInitialBoundsMatched()) {
			Vector3 nearMousePosition = GeometryToolkit.convertMouseTo3d(x, y, graphicsData, ViewingCoordinator.NEAR_BOUNDS_DISTANCE);
			Vector3 farMousePosition = GeometryToolkit.convertMouseTo3d(x, y, graphicsData, ViewingCoordinator.FAR_BOUNDS_DISTANCE);
			
			// The y-coordinate needs to be inverted
			// mousePosition.set(mousePosition.x(), -mousePosition.y(), mousePosition.z());
			
			Quadrilateral oldNearBounds = coordinatorData.getNearBounds();
			
			Vector3 newCenter = findClickedNewCenter(camera.getPosition(), camera.getDirection(), oldNearBounds.getCenterPoint(), nearMousePosition);
			oldNearBounds.moveTo(newCenter);

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
	
	
	
	@Override
	public void pressed(int x, int y) {
	}

	@Override
	public void clicked(int x, int y) {
	}

	@Override
	public void released(int x, int y) {
	}

	@Override
	public MouseCommand modify() {
		return this;
	}

}
