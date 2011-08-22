package org.cytoscape.paperwing.internal;

import java.util.Properties;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;

/** This class is capable of creating instances of the WindMapRenderingEngine
 * 
 * @author Paperwing (Yue Dong)
 */
public class WindMapRenderingEngineFactory implements
		RenderingEngineFactory<CyNetwork> {

	/** A manager for the current list of network views */
	private CyNetworkViewManager networkViewManager;
	
	/** The RenderingEngineManager that keeps track of the current rendering
	 * engines */
	private RenderingEngineManager renderingEngineManager;
	
	/** The VisualLexicon for the WindMapRenderingEngine */
	private final VisualLexicon visualLexicon;
	
	/** The service registrar to be used for exporting listeners as 
	 * OSGi services */
	private CyServiceRegistrar serviceRegistrar;
	
	/** Create a new WindMapRenderingEngineFactory object */
	public WindMapRenderingEngineFactory(CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager, VisualLexicon lexicon,
			CyServiceRegistrar serviceRegistrar) {	
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		
		this.serviceRegistrar = serviceRegistrar;
		
		Graphics.initSingleton();
		WindMapRenderingEngine.setNetworkViewManager(networkViewManager);
	}
	
	@Override
	public RenderingEngine<CyNetwork> getInstance(
			Object container, View<CyNetwork> viewModel) {
		
		/* For code below, seems that NetworkViewManager does not contain 
		 * references to all available NetworkViews
		 */
		/*
		System.out.println("map given model: " + viewModel.getModel());
		System.out.println("map given model suid: " + viewModel.getModel().getSUID());
		System.out.println("map given suid: " + viewModel.getSUID());
		System.out.println("map networkViewSet: " + networkViewManager.getNetworkViewSet());
		*/
		
		WindMapRenderingEngine engine = 
			new WindMapRenderingEngine(container, viewModel, visualLexicon);
		//System.out.println("map returning engine: " + engine);
		renderingEngineManager.addRenderingEngine(engine);
		
		//System.out.println("map engine active?: " + engine.isActive());

//		serviceRegistrar.registerService(engine.getAboutToBeRemovedListener(), 
//				NetworkAboutToBeDestroyedListener.class, 
//				new Properties());
		
		return engine;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		//System.out.println("getVisualLexicon call");
		
		return visualLexicon;
	}

}
