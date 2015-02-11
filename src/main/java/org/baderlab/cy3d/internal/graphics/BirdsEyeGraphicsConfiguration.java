package org.baderlab.cy3d.internal.graphics;

import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.BirdsEyeEventBusListener;
import org.baderlab.cy3d.internal.input.handler.BirdsEyeInputEventListener;
import org.baderlab.cy3d.internal.rendering.PositionCameraProcedure;
import org.baderlab.cy3d.internal.rendering.RenderArcEdgesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderBoundingBoxProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodesProcedure;
import org.baderlab.cy3d.internal.rendering.ResetSceneProcedure;

import com.google.common.eventbus.EventBus;

/**
 * An implementation of the {@link GraphicsConfiguration} interface used
 * for bird's eye view rendering objects. This {@link GraphicsConfiguration}
 * implementation thusly will not provide support for picking and
 * advanced input handling, such as node and edge creation via the
 * keyboard.
 * 
 */
public class BirdsEyeGraphicsConfiguration extends AbstractGraphicsConfiguration {
	

	private RenderBoundingBoxProcedure boundingBoxProc;
	
	public BirdsEyeGraphicsConfiguration() {
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
		add(boundingBoxProc = new RenderBoundingBoxProcedure());	
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		
		BirdsEyeInputEventListener.attach(graphicsData.getContainer(), graphicsData);
		
		EventBus eventBus = graphicsData.getEventBus();
		BirdsEyeEventBusListener eventBusListener = new BirdsEyeEventBusListener(graphicsData, boundingBoxProc);
		eventBus.register(eventBusListener);
		
		// Manually fit graph into the correct size for the first frame.
		eventBusListener.handleFitInViewEvent(null);
	}
	
	
	@Override
	public String toString() {
		return "BirdsEyeGraphicsConfiguration";
	}
}
