package org.baderlab.cy3d.internal.graphics;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

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
	
	private JComponent frame;
	private InputEventListener inputHandler;
	private ToolPanel toolPanel;
			
	
	public MainGraphicsConfiguration() {
		shapePickingProcessor = new DefaultShapePickingProcessor(new RenderNodesProcedure(), new RenderArcEdgesProcedure());
		
		add(new ResetSceneProcedure());
		add(new PositionCameraProcedure());
		
		add(new RenderNodesProcedure());
		add(new RenderArcEdgesProcedure());
		add(new RenderSelectionBoxProcedure());
		add(new RenderNodeLabelsProcedure());
	}
	
	
	@Override
	public void initializeFrame(JComponent component, JComponent inputComponent) {
		this.frame = component;
		if(component instanceof RootPaneContainer) {
			this.toolPanel = new ToolPanel((RootPaneContainer)component, inputComponent);
		}
	}
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		shapePickingProcessor.initialize(graphicsData);
		
		// Input handler
		MouseZoneInputListener mouseZoneListener = MouseZoneInputListener.attach(frame, graphicsData.getInputComponent(), graphicsData);
		inputHandler = MainInputEventListener.attach(graphicsData.getInputComponent(), graphicsData, mouseZoneListener);
		
		// EventBus
		EventBus eventBus = graphicsData.getEventBus();
		if(toolPanel != null) {
			toolPanel.setEventBus(eventBus);
		}
		MainEventBusListener eventBusListener = new MainEventBusListener(graphicsData);
		eventBus.register(eventBusListener);
		eventBus.register(mouseZoneListener);
		
		// Manually fit the network into the view for the first frame
		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews(); 
		eventBusListener.handleFitInViewEvent(new FitInViewEvent(nodeViews));
	}
	
	
	@Override
	public void update() {
		shapePickingProcessor.processPicking(graphicsData);
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