package org.baderlab.cy3d.internal.input.handler;

import java.awt.Component;

import org.baderlab.cy3d.internal.data.GraphicsData;

/**
 * MKTODO For now the birds eye input listener doesn't do anything.
 * @author mkucera
 *
 */
public class BirdsEyeInputEventListener extends InputEventListener {


	public BirdsEyeInputEventListener(GraphicsData graphicsData) {
		super(graphicsData);
	}
	
	public static BirdsEyeInputEventListener attach(Component component, GraphicsData graphicsData) {
//		BirdsEyeInputEventListener inputListener = new BirdsEyeInputEventListener(graphicsData);
//		
//		component.addMouseMotionListener(inputListener);
//		component.addMouseListener(inputListener);
//		
//		return inputListener;
		return null;
	}
	
}
