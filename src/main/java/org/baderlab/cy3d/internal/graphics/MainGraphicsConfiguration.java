package org.baderlab.cy3d.internal.graphics;

import java.util.Collection;

import javax.swing.JInternalFrame;

import org.baderlab.cy3d.internal.cytoscape.processing.CytoscapeDataProcessor;
import org.baderlab.cy3d.internal.cytoscape.processing.MainCytoscapeDataProcessor;
import org.baderlab.cy3d.internal.data.GraphicsData;
import org.baderlab.cy3d.internal.eventbus.FitInViewEvent;
import org.baderlab.cy3d.internal.input.handler.InputEventListener;
import org.baderlab.cy3d.internal.input.handler.MainEventBusListener;
import org.baderlab.cy3d.internal.input.handler.MainInputEventListener;
import org.baderlab.cy3d.internal.input.handler.MouseZoneInputListener;
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
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.google.common.eventbus.EventBus;

/**
 * An implementation for the {@link GraphicsConfiguration} interface to be used
 * for main rendering windows. That is, this handler fully supports keyboard
 * and mouse input, as well as selection and picking.
 * 
 */
public class MainGraphicsConfiguration extends AbstractGraphicsConfiguration {
	
	private final ShapePickingProcessor shapePickingProcessor;
	private final CytoscapeDataProcessor dataProcessor;
	
	private JInternalFrame frame;
	private InputEventListener inputHandler;
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
		add(new RenderNodeLabelsProcedure());
	}
	
	
	@Override
	public void initializeFrame(JInternalFrame frame) {
		this.frame = frame;
		this.toolPanel = new ToolPanel(frame);
	}
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		shapePickingProcessor.initialize(graphicsData);
		
		// Input handler
		MouseZoneInputListener mouseZoneListener = MouseZoneInputListener.attach(frame, graphicsData.getContainer(), graphicsData);
		inputHandler = MainInputEventListener.attach(graphicsData.getContainer(), graphicsData, mouseZoneListener);
		
		// EventBus
		EventBus eventBus = graphicsData.getEventBus();
		toolPanel.setEventBus(eventBus);
		MainEventBusListener eventBusListener = new MainEventBusListener(graphicsData);
		eventBus.register(eventBusListener);
		
		// Manually fit the network into the view for the first frame
		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews(); 
		eventBusListener.handleFitInViewEvent(new FitInViewEvent(nodeViews));
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
	

	@Override
	public String toString() {
		return "MainGraphicsConfiguration";
	}

}