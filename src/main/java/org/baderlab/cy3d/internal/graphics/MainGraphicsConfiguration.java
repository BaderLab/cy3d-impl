package org.baderlab.cy3d.internal.graphics;

import java.util.Collection;

import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.camera.Camera;
import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.FitInViewEvent;
import org.baderlab.cy3d.internal.eventbus.ShowLabelsEvent;
import org.baderlab.cy3d.internal.input.handler.MainInputEventListener;
import org.baderlab.cy3d.internal.input.handler.ToolPanel;
import org.baderlab.cy3d.internal.picking.DefaultShapePickingProcessor;
import org.baderlab.cy3d.internal.picking.ShapePickingProcessor;
import org.baderlab.cy3d.internal.rendering.PositionCameraProcedure;
import org.baderlab.cy3d.internal.rendering.RenderArcEdgesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderLightsProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodeLabelsProcedure;
import org.baderlab.cy3d.internal.rendering.RenderNodesProcedure;
import org.baderlab.cy3d.internal.rendering.RenderSelectionBoxProcedure;
import org.baderlab.cy3d.internal.rendering.ResetSceneProcedure;
import org.baderlab.cy3d.internal.tools.NetworkToolkit;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.google.common.eventbus.Subscribe;

/**
 * An implementation for the {@link GraphicsConfiguration} interface to be used
 * for main rendering windows. That is, this handler fully supports keyboard
 * and mouse input, as well as selection and picking.
 * 
 */
public class MainGraphicsConfiguration extends AbstractGraphicsConfiguration {
	
	private final RenderNodeLabelsProcedure renderNodeLabelsProcedure;
	
	private final ShapePickingProcessor shapePickingProcessor;
	private final CytoscapeDataProcessor dataProcessor;
	
	private MainInputEventListener inputHandler;
	private ToolPanel toolPanel;
			
	
	public MainGraphicsConfiguration() {
		dataProcessor = new MainCytoscapeDataProcessor();
		shapePickingProcessor = new DefaultShapePickingProcessor(new RenderNodesProcedure(), new RenderArcEdgesProcedure());
		
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
		add(new RenderSelectionBoxProcedure());
		add(new RenderLightsProcedure());
		
		// Make label rendering the last element in the list so that adding and removing it doesn't change the order
		// of the other elements in the list. 
		// (Note, if more complex enabling/disabling of render procedures is needed in the future then AbstractGraphicsConfiguration
		// should be changed to have a more sophisticated ordering mechanism.)
		add(renderNodeLabelsProcedure = new RenderNodeLabelsProcedure());
	}
	
	
	@Override
	public void initializeFrame(JInternalFrame frame) {
		toolPanel = new ToolPanel(frame);
	}
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		shapePickingProcessor.initialize(graphicsData);
		
		// Input handler
		inputHandler = MainInputEventListener.attach(graphicsData.getContainer(), graphicsData);
		
		// EventBus
		toolPanel.setEventBus(graphicsData.getEventBus());
		graphicsData.getEventBus().register(this);
	}
	
	
	@Override
	public void update() {
		shapePickingProcessor.processPicking(graphicsData);
		dataProcessor.processCytoscapeData(graphicsData);
	}

	
	@Override
	public void dispose() {
		inputHandler.dispose();
	}
	
	@Subscribe
	public void handleShowLabelsChangedEvent(ShowLabelsEvent showLabelsEvent) {
		if(showLabelsEvent.showLabels()) {
			add(renderNodeLabelsProcedure);
		} else {
			remove(renderNodeLabelsProcedure);
		}
		inputHandler.touch();
	}
	
	@Subscribe
	public void handleFitInViewEvent(FitInViewEvent e) {
		Camera camera = graphicsData.getCamera();
		Collection<View<CyNode>> selectedNodeViews = e.getSelectedNodeViews();
		NetworkToolkit.fitInView(camera, selectedNodeViews, 180.0, 2.3, 1.8);
	}
	

	@Override
	public String toString() {
		return "MainGraphicsConfiguration";
	}

}