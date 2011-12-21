package org.cytoscape.paperwing.internal.input;

import java.awt.event.KeyEvent;
import java.util.Set;

import org.cytoscape.paperwing.internal.KeyboardMonitor;
import org.cytoscape.paperwing.internal.MouseMonitor;
import org.cytoscape.paperwing.internal.SimpleCamera;
import org.cytoscape.paperwing.internal.graphics.GraphicsData;

public class CameraInputProcessor implements InputProcessor {

	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse,
			GraphicsData graphicsData) {
		
		Set<Integer> held = keys.getHeld();
		SimpleCamera camera = graphicsData.getCamera();
		
		processCameraTranslation(held, camera);
		processCameraRoll(held, camera);
		processCameraRotation(held, camera);
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
		if (held.contains(KeyEvent.VK_Z)) {
			camera.rollClockwise();
		}
	
		// Roll camera counter-clockwise
		if (held.contains(KeyEvent.VK_X)) {
			camera.rollCounterClockwise();
		}
	}
	
}
