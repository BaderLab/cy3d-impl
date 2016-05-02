package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.BirdsEyeCameraChangeEvent;
import org.baderlab.cy3d.internal.input.handler.commands.CameraOrbitMouseCommand;

/**
 * @author mkucera
 * 
 * @deprecated In Cytoscape 3.4 the birds-eye view is layered on top of the main renderer. 
 * So we actually don't want to steal the input away from the main renderer.
 */
@Deprecated
public class BirdsEyeInputEventListener extends InputEventListener {


	public BirdsEyeInputEventListener(GraphicsData graphicsData, MouseZoneInputListener mouseZoneListener) {
		super(graphicsData);
		
		CameraOrbitMouseCommand orbitCommand = new CameraOrbitMouseCommand(graphicsData);
		orbitCommand.setIsRotateSampler(mouseZoneListener);
		setPrimaryMouseCommand(orbitCommand); 
		setSecondaryMouseCommand(orbitCommand); 
	}
	
	public static BirdsEyeInputEventListener attach(Component component, GraphicsData graphicsData, MouseZoneInputListener mouseZoneListener) {
		BirdsEyeInputEventListener inputListener = new BirdsEyeInputEventListener(graphicsData, mouseZoneListener);
		
		component.addMouseMotionListener(inputListener);
		component.addMouseListener(inputListener);
	
		return inputListener;
	}
	
	@Override
	protected void fireUpdateEvents() {
		graphicsData.getEventBus().post(new BirdsEyeCameraChangeEvent(graphicsData.getCamera()));
	}
}
