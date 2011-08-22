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

/** The RenderingEngineFactory for the WindRenderingEngine
 * 
 * @author paperwing (Yue Dong)
 */
public class WindRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {

	/** The network view manager containing references to the network views */
	private CyNetworkViewManager networkViewManager;
	
	/** The RenderingEngineManager containing references to the current 
	 * rendering engines */
	private RenderingEngineManager renderingEngineManager;
	
	/** The visual lexicon for the wind rendering engine */
	private final VisualLexicon visualLexicon;
	
	/** The service registrar used to listen for events regarding when
	 * the Graphics object is to be removed
	 */
	private CyServiceRegistrar serviceRegistrar;
	
	/** Construct a new WindRenderingEngineFactory object */
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
		/*
		System.out.println("given model: " + viewModel.getModel());
		System.out.println("given model suid: " + viewModel.getModel().getSUID());
		System.out.println("given suid: " + viewModel.getSUID());
		System.out.println("networkViewSet: " + networkViewManager.getNetworkViewSet());
		*/
		
		//TODO: NetworkViewManager does not contain all instances of CyNetworkView, so wait 
		WindRenderingEngine engine = new WindRenderingEngine(container, viewModel, visualLexicon);
		// System.out.println("returning engine: " + engine);
		renderingEngineManager.addRenderingEngine(engine);
		
		// System.out.println("Engine active?: " + engine.isActive());
		
		// System.out.println("registering service to " + serviceRegistrar + ": " + engine.getEngineRemovedListener()
		// 		+ ", " + RenderingEngineAboutToBeRemovedListener.class);
		
//		serviceRegistrar.registerService(engine.getAboutToBeRemovedListener(), 
//				NetworkAboutToBeDestroyedListener.class, 
//				new Properties());
		
		return engine;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		// System.out.println("getVisualLexicon call");
		
		return visualLexicon;
	}

}
