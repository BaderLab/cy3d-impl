package org.baderlab.cy3d.internal.input;

import java.awt.event.MouseEvent;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.data.SettingsData.CameraDragMode;
import org.baderlab.cy3d.internal.tools.SimpleCamera;

public class CameraMouseInputHandler implements InputHandler {

	
	
	@Override
	public void processInput(KeyboardMonitor keys, MouseMonitor mouse, GraphicsData graphicsData) {
		SimpleCamera camera = graphicsData.getCamera();
		
		if(mouse.getHeld().contains(MouseEvent.BUTTON1) && mouse.hasMoved()) {
			
			CameraDragMode cameraMode = graphicsData.getSettingsData().getCameraDragMode();
			
			switch(cameraMode) {
				case PAN:
					camera.turnRight(mouse.dX());
					camera.turnDown(mouse.dY());
					break;
				default:
			}
		}
		
	}

}
