package org.baderlab.cy3d.internal;

import javax.swing.JComponent;

import org.baderlab.cy3d.internal.cytoscape.view.Cy3DNetworkView;
import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
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
public abstract class Cy3DRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {
	
	private final RenderingEngineManager renderingEngineManager;
	private final VisualLexicon visualLexicon;
	private final TaskFactoryListener taskFactoryListener;
	private final DialogTaskManager taskManager;
	
	/** The service registrar used to listen for events regarding when the Graphics object is to be removed */
	private final CyServiceRegistrar serviceRegistrar;
	
	
	public Cy3DRenderingEngineFactory(RenderingEngineManager renderingEngineManager, 
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			CyServiceRegistrar serviceRegistrar) {	
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		this.taskFactoryListener = taskFactoryListener;
		this.taskManager = taskManager;
		this.serviceRegistrar = serviceRegistrar;
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
		
		//TODO: NetworkViewManager does not contain all instances of CyNetworkView, so wait 
		Cy3DRenderingEngine engine = getNewRenderingEngine(cy3dViewModel, visualLexicon);
		engine.setUpCanvas(component);
		engine.setUpListeners(serviceRegistrar);
		engine.setupTaskFactories(taskFactoryListener, taskManager);
		
		renderingEngineManager.addRenderingEngine(engine);
		
		return engine;
	}
	
	protected abstract Cy3DRenderingEngine getNewRenderingEngine(Cy3DNetworkView viewModel, VisualLexicon visualLexicon);
	
	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}
}
