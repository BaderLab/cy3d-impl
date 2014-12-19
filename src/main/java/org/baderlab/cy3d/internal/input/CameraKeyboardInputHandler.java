package org.baderlab.cy3d.internal.input;

import java.awt.event.KeyEvent;
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

public class CameraKeyboardInputHandler implements InputHandler {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		
		Set<Integer> held = keys.getHeld();
		Set<Integer> pressed = keys.getPressed();
		System.out.println(held);
		SimpleCamera camera = graphicsData.getCamera();
		
		processCameraTranslation(held, camera);
		//processCameraRoll(held, camera);
		processCameraRotation(held, camera);
		
		processCameraFirstPersonLook(keys, mouse, camera);
		processCameraZoom(mouse, graphicsData);
		
		processResetCamera(held, graphicsData);
		
		processDebugAngles(held, camera);
	}
	
	private void processCameraZoom(MouseMonitor mouse, GraphicsData graphicsData) {
		SimpleCamera camera = graphicsData.getCamera();
		
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Varying distance between camera and camera's target point
		if (mouse.dWheel() != 0) {
//			camera.zoomOut((double) mouse.dWheel());
			camera.moveForwardQuickly(-mouse.dWheel());
			
			List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
			
			if (!selectedNodes.isEmpty()) {
				Set<Long> selectedNodeIndices = new HashSet<Long>();
				
				for (CyNode node : selectedNodes) {
					selectedNodeIndices.add(node.getSUID());
				}
				
				Vector3 averagePosition = NetworkToolkit.findCenter(selectedNodeIndices, networkView, graphicsData.getDistanceScale());
				selectionData.setSelectProjectionDistance(averagePosition.distance(camera.getPosition()));
			}
		}
	}

	private void processCameraFirstPersonLook(KeyboardMonitor keys, MouseMonitor mouse, SimpleCamera camera) {
		
		if (mouse.hasMoved()) {

			// First-person camera rotation
			if (keys.getHeld().contains(KeyEvent.VK_ALT)) {
				camera.turnRight(mouse.dX());
				camera.turnDown(mouse.dY());
			}
		}
	}

	private void processCameraTranslation(Set<Integer> held, SimpleCamera camera) {
		if (held.contains(KeyEvent.VK_W)) {
			camera.moveUp();
		}

		if (held.contains(KeyEvent.VK_S)) {
			camera.moveDown();
		}

		if (held.contains(KeyEvent.VK_A)) {
			camera.moveLeft();
		}

		if (held.contains(KeyEvent.VK_D)) {
			camera.moveRight();
		}

		if (held.contains(KeyEvent.VK_Q)) {
			camera.moveBackward();
		}

		if (held.contains(KeyEvent.VK_E)) {
			camera.moveForward();
		}
	}
	
	private void processCameraRotation(Set<Integer> held, SimpleCamera camera) {
		
		// If shift is pressed, perform orbit camera movement
		if (held.contains(KeyEvent.VK_SHIFT)) {

			if (held.contains(KeyEvent.VK_LEFT)) {
				camera.orbitLeft();
			}

			if (held.contains(KeyEvent.VK_RIGHT)) {
				camera.orbitRight();
			}

			if (held.contains(KeyEvent.VK_UP)) {
				camera.orbitUp();
			}

			if (held.contains(KeyEvent.VK_DOWN)) {
				camera.orbitDown();
			}

			// Otherwise, turn camera in a first-person like fashion
		} else {

			if (held.contains(KeyEvent.VK_LEFT)) {
				camera.turnLeft(4);
			}

			if (held.contains(KeyEvent.VK_RIGHT)) {
				camera.turnRight(4);
			}

			if (held.contains(KeyEvent.VK_UP)) {
				camera.turnUp(4);
			}

			if (held.contains(KeyEvent.VK_DOWN)) {
				camera.turnDown(4);
			}
		}
	}
	
	private void processCameraRoll(Set<Integer> held, SimpleCamera camera) {
		
		// Roll camera clockwise
		if (held.contains(KeyEvent.VK_X)) {
			camera.rollClockwise();
		}
	
		// Roll camera counter-clockwise
		if (held.contains(KeyEvent.VK_Z)) {
			camera.rollCounterClockwise();
		}
	}
	
	private void processResetCamera(Set<Integer> held, GraphicsData graphicsData) {
		
		// Reset camera
		if (held.contains(KeyEvent.VK_C)) {
			graphicsData.getCamera().set(new SimpleCamera());
			graphicsData.getNetworkView().fitContent();
		}
	}
	
	private void processDebugAngles(Set<Integer> held, SimpleCamera camera) {
		
		if (held.contains(KeyEvent.VK_V)) {
			System.out.println("Camera direction and up vectors: " + camera.getDirection()
					+ ", " + camera.getUp());
			
			Vector3 angles = GeometryToolkit.findYawPitchRoll(camera.getDirection(), camera.getUp());
			System.out.println("Camera yaw, pitch, roll: " + angles);
		
			Vector3 direction = GeometryToolkit.findDirectionVector(angles.x(), angles.y(), angles.z());
			Vector3 up = GeometryToolkit.findUpVector(angles.x(), angles.y(), angles.z());
			
			System.out.println("Camera direction and up calculated from angles: " + direction + ", " + up);
			
			System.out.println("Differences: " + (direction.subtract(camera.getDirection()).magnitudeSquared() + up.subtract(camera.getUp()).magnitudeSquared()));
		}
	}
}
