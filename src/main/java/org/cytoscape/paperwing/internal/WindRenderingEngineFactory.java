package org.cytoscape.paperwing.internal;

import java.util.Properties;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.events.RenderingEngineAboutToBeRemovedListener;

public class WindRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {

	private CyNetworkViewManager networkViewManager;
	private RenderingEngineManager renderingEngineManager;
	private final VisualLexicon visualLexicon;
	
	private CyServiceRegistrar serviceRegistrar;
	
	public WindRenderingEngineFactory(CyNetworkViewManager networkViewManager,
			RenderingEngineManager renderingEngineManager, VisualLexicon lexicon,
			CyServiceRegistrar serviceRegistrar) {	
		this.networkViewManager = networkViewManager;
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		
		this.serviceRegistrar = serviceRegistrar;
		
		// TestGraphics.initSingleton();
		Graphics.initSingleton();
		WindRenderingEngine.setNetworkViewManager(networkViewManager);
	}
	
	@Override
	public RenderingEngine<CyNetwork> getInstance(
			Object container, View<CyNetwork> viewModel) {
		
		/* For code below, seems that NetworkViewManager does not contain references to all available NetworkViews
		 */
		System.out.println("given model: " + viewModel.getModel());
		System.out.println("given model suid: " + viewModel.getModel().getSUID());
		System.out.println("given suid: " + viewModel.getSUID());
		System.out.println("networkViewSet: " + networkViewManager.getNetworkViewSet());
		
		/*
		// CyNetworkView networkView = networkViewManager.getNetworkView(viewModel.getSUID());
		CyNetworkView networkView = null;
		for (CyNetworkView view : networkViewManager.getNetworkViewSet()) {
			if (view.getModel() == viewModel.getModel()) {
				networkView = view;
			}
			System.out.println("current model: " + view.getModel());
			System.out.println("current model suid: " + view.getModel().getSUID());
			System.out.println("current suid: " + view.getSUID());
				
		}
		*/
		
		//TODO: NetworkViewManager does not contain all instances of CyNetworkView, so wait 
		WindRenderingEngine engine = new WindRenderingEngine(container, viewModel, visualLexicon);
		System.out.println("returning engine: " + engine);
		renderingEngineManager.addRenderingEngine(engine);
		
		System.out.println("Engine active?: " + engine.isActive());
		
		System.out.println("registering service to " + serviceRegistrar + ": " + engine.getEngineRemovedListener()
				+ ", " + RenderingEngineAboutToBeRemovedListener.class);
		serviceRegistrar.registerService(engine.getEngineRemovedListener(), 
				RenderingEngineAboutToBeRemovedListener.class, new Properties());
		
		return engine;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		// System.out.println("getVisualLexicon call");
		
		return visualLexicon;
	}

}
