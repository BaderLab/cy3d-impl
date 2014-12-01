package org.baderlab.cy3d.internal;

import org.baderlab.cy3d.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.DialogTaskManager;

/** The RenderingEngineFactory for the WindRenderingEngine
 * 
 * @author paperwing (Yue Dong)
 */
public abstract class WindRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {

	/** The network view manager containing references to the network views */
	private CyNetworkViewManager networkViewManager;
	
	/** The RenderingEngineManager containing references to the current 
	 * rendering engines */
	private RenderingEngineManager renderingEngineManager;
	
	/** The visual lexicon for the wind rendering engine */
	private final VisualLexicon visualLexicon;
	
	/** A listener used to obtain the set of {@link TaskFactory} objects for use with creating context menus */
	private TaskFactoryListener taskFactoryListener;
	
	/** A task manager object that can be used to execute tasks created by TaskFactory objects */
	private DialogTaskManager taskManager;
	
	
	/** The service registrar used to listen for events regarding when
	 * the Graphics object is to be removed
	 */
	private CyServiceRegistrar serviceRegistrar;
	
	/** Construct a new WindRenderingEngineFactory object */
	public WindRenderingEngineFactory(CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager, VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			CyServiceRegistrar serviceRegistrar) {	
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		
		this.taskFactoryListener = taskFactoryListener;
		this.taskManager = taskManager;
		this.serviceRegistrar = serviceRegistrar;
	}
	
	@Override
	public RenderingEngine<CyNetwork> createRenderingEngine(
			Object container, View<CyNetwork> viewModel) {
		
		//TODO: NetworkViewManager does not contain all instances of CyNetworkView, so wait 
		WindRenderingEngine engine = getNewRenderingEngine(container, viewModel, visualLexicon);
		
//		engine.setUpNetworkView(networkViewManager);
		engine.setUpCanvas(container);
		engine.setUpListeners(serviceRegistrar);
		engine.setupTaskFactories(taskFactoryListener, taskManager);
		
//		serviceRegistrar.registerService(engine.getSetCurrentRenderingEngineListener(), SetCurrentRenderingEngineListener.class, new Properties());
		
		// System.out.println("returning engine: " + engine);
		renderingEngineManager.addRenderingEngine(engine);
		
		return engine;
	}
	
	protected abstract WindRenderingEngine getNewRenderingEngine(Object container, 
			View<CyNetwork> viewModel, VisualLexicon visualLexicon);
	
	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}
}
