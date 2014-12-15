package org.baderlab.cy3d.internal.graphics;

import org.baderlab.cy3d.internal.coordinator.BirdsEyeCoordinatorProcessor;
import org.baderlab.cy3d.internal.coordinator.CoordinatorProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.BirdsEyeCytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.input.BirdsEyeInputProcessor;
import org.baderlab.cy3d.internal.input.InputProcessor;
import org.baderlab.cy3d.internal.rendering.PositionCameraProcedure;
import org.baderlab.cy3d.internal.rendering.RenderArcEdgesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderBoundingBoxProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodesProcedure;
import org.baderlab.cy3d.internal.rendering.ResetSceneProcedure;

/**
 * An implementation of the {@link GraphicsHandler} interface used
 * for bird's eye view rendering objects. This {@link GraphicsHandler}
 * implementation thusly will not provide support for picking and
 * advanced input handling, such as node and edge creation via the
 * keyboard.
 * 
 */
public class BirdsEyeGraphicsHandler extends AbstractGraphicsHandler {
	
	public BirdsEyeGraphicsHandler() {
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
		add(new RenderBoundingBoxProcedure());	
	}
	
	@Override
	public InputProcessor getInputProcessor() {
		return new BirdsEyeInputProcessor();
	}

	@Override
	public CoordinatorProcessor getCoordinatorProcessor() {
		return new BirdsEyeCoordinatorProcessor();
	}
	
	@Override
	public CytoscapeDataProcessor getCytoscapeDataProcessor() {
		return new BirdsEyeCytoscapeDataProcessor();
	}
}
