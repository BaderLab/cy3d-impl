package org.baderlab.cy3d.internal;

import java.awt.Container;

import javax.swing.JComponent;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.eventbus.EventBusProvider;
import org.baderlab.cy3d.internal.graphics.GraphicsConfiguration;
import org.baderlab.cy3d.internal.graphics.GraphicsConfigurationFactory;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;

/** The RenderingEngineFactory for the Cy3DRenderingEngine
 * 
 * @author paperwing (Yue Dong)
 */
public class Cy3DRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {
	
	private final RenderingEngineManager renderingEngineManager;
	private final VisualLexicon visualLexicon;
	private final TaskFactoryListener taskFactoryListener;
	private final DialogTaskManager taskManager;
	private final EventBusProvider eventBusProvider;
	
	private final GraphicsConfigurationFactory graphicsConfigFactory;
	
	
	public Cy3DRenderingEngineFactory(
			RenderingEngineManager renderingEngineManager, 
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			EventBusProvider eventBusFactory,
			GraphicsConfigurationFactory graphicsConfigFactory) {	
		
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		this.taskFactoryListener = taskFactoryListener;
		this.taskManager = taskManager;
		this.eventBusProvider = eventBusFactory;
		this.graphicsConfigFactory = graphicsConfigFactory;
	}
	
	
	/**
	 * Catch these errors up front.
	 * 
	 * @throws ClassCastException if the viewModel is not an instance of Cy3DNetworkView
	 * @throws ClassCastException if the container is not an instance of JComponent
	 */
	@Override
	public RenderingEngine<CyNetwork> createRenderingEngine(Object container, View<CyNetwork> viewModel) {
		// Verify the type of the view up front.
		Cy3DNetworkView cy3dViewModel = (Cy3DNetworkView) viewModel;
		JComponent component = (JComponent) container;
		
		GraphicsConfiguration configuration = graphicsConfigFactory.createGraphicsConfiguration();
		
		// TODO the birds eye view should not be attaching input listeners to the outer component
		// Is the Birds eye view above the top glass pane?
		JComponent inputComponent = getKeyboardComponent(component, cy3dViewModel.getSUID());
		if(inputComponent == null)
			inputComponent = component; // happens for birds-eye-view
		
		Cy3DRenderingEngine engine = new Cy3DRenderingEngine(component, inputComponent, cy3dViewModel, visualLexicon, eventBusProvider,
				                                             configuration, taskFactoryListener, taskManager);
		
		renderingEngineManager.addRenderingEngine(engine);
		return engine;
	}
	
	/**
	 * This is a HACK for now to get the component to attach hotkeys and cursors to.
	 */
	private JComponent getKeyboardComponent(JComponent start, long suid) {
		String componentName = "__CyNetworkView_" + suid; // see ViewUtil.createUniqueKey(CyNetworkView)
		Container parent = start;
		while(parent != null) {
			if(componentName.equals(parent.getName())) {
				return (JComponent) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	
	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}
}
