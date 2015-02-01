package org.baderlab.cy3d.internal.graphics;

import org.baderlab.cy3d.internal.cytoscape.processing.BirdsEyeCytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.input.handler.BirdsEyeInputEventListener;
import org.baderlab.cy3d.internal.rendering.PositionCameraProcedure;
import org.baderlab.cy3d.internal.rendering.RenderArcEdgesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodesProcedure;
import org.baderlab.cy3d.internal.rendering.ResetSceneProcedure;

/**
 * An implementation of the {@link GraphicsConfiguration} interface used
 * for bird's eye view rendering objects. This {@link GraphicsConfiguration}
 * implementation thusly will not provide support for picking and
 * advanced input handling, such as node and edge creation via the
 * keyboard.
 * 
 */
public class BirdsEyeGraphicsConfiguration extends AbstractGraphicsConfiguration {
	
	private final CytoscapeDataProcessor dataProcessor = new BirdsEyeCytoscapeDataProcessor();;

	public BirdsEyeGraphicsConfiguration() {
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
//		add(new RenderBoundingBoxProcedure());	
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		BirdsEyeInputEventListener.attach(graphicsData.getContainer(), graphicsData);
	}
	
	@Override
	public void update() {
		dataProcessor.processCytoscapeData(graphicsData);
	}
	
	@Override
	public String toString() {
		return "BirdsEyeGraphicsHandler";
	}
}
